package uk.ac.cam.db538.dexter.transform;

import java.util.ArrayList;
import java.util.Map;

import lombok.val;
import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;
import uk.ac.cam.db538.dexter.transform.macros.MethodCallMacros;

public class DexterTransform extends Transform {

	public DexterTransform() { }

	public DexterTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	private int auxiliaryRegisterId = 0;
	private Map<DexInstruction_Invoke, CallDestinationType> methodCallClassification = null;
	
	@Override
	public DexCode doFirst(DexCode code) {
		code = super.doFirst(code);
		
		auxiliaryRegisterId = 0;
		
		val classification = MethodCallClassifier.classifyMethodCalls(code);
		methodCallClassification = classification.getValB();
		code = classification.getValA();
		
		return code;
	}

	@Override
	public DexCodeElement doFirst(DexCodeElement element, DexCode code) {
		element = super.doFirst(element, code);
		
		if (element instanceof DexInstruction_Const)
			return instrument_Const((DexInstruction_Const) element);
		
		if (element instanceof DexInstruction_Invoke) {
			
			DexCodeElement nextElement = code.getInstructionList().getNextInstruction(element);
			if (!(nextElement instanceof DexInstruction_MoveResult))
				nextElement = null;
			
			val type = methodCallClassification.get(element);
			if (type == CallDestinationType.Internal)
				return instrument_Invoke_Internal((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement, code);
			else if (type == CallDestinationType.External)
				return instrument_Invoke_External((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement, code);
			else
				throw new Error("Calls should never be classified as undecidable by this point");
			
		}
		
		if (element instanceof DexInstruction_MoveResult) {
			return DexMacro.empty(); // handled by Invoke and FillArray
		}
		
		return element;
	}
	
	@Override
	public DexCode doLast(DexCode code) {
		methodCallClassification = null;
		return super.doLast(code);
	}
	
	private DexCodeElement instrument_Const(DexInstruction_Const insn) {
		return new DexMacro(
				new DexInstruction_Const(
					insn.getRegTo().getTaintRegister(),
					0L,
					insn.getHierarchy()),
				insn);
	}

	private DexCodeElement instrument_Invoke_Internal(DexInstruction_Invoke invoke, DexInstruction_MoveResult moveResult, DexCode code) {
		val prototype = invoke.getMethodId().getPrototype();
		
		DexMacro macroStorePrimitiveArgumentTaint;
		DexMacro macroRetrievePrimitiveResultTaint;
		
		// need to store taints in the ThreadLocal ARGS array?
		if (prototype.hasPrimitiveArgument()) {
			
			val isStatic = invoke.getCallType() == Opcode_Invoke.Static;
			val paramCount = prototype.getParameterCount(isStatic);
			val taintRegs = new ArrayList<DexTaintRegister>(paramCount);
			
			for (int i = 0; i < paramCount; i++) {
				val paramType = prototype.getParameterType(i, isStatic, invoke.getClassType());
				if (paramType instanceof DexPrimitiveType)
					taintRegs.add(invoke.getArgumentRegisters().get(i).getTaintRegister());
			}
			
			macroStorePrimitiveArgumentTaint = MethodCallMacros.setParamTaints(
					getAuxiliaryDex(),
					genAuxiliaryRegister(), 
					genAuxiliaryRegister(), 
					taintRegs); 
		} else
			macroStorePrimitiveArgumentTaint = DexMacro.empty();
		
		// need to retrieve taint from the ThreadLocal RES field?
		if (moveResult != null && prototype.getReturnType() instanceof DexPrimitiveType)
			macroRetrievePrimitiveResultTaint = MethodCallMacros.getResultTaint(
					getAuxiliaryDex(),
					genAuxiliaryRegister(), 
					moveResult.getRegTo().getTaintRegister()); 
		else
			macroRetrievePrimitiveResultTaint = DexMacro.empty();
		
		return new DexMacro(
				macroStorePrimitiveArgumentTaint,
				generateInvoke(invoke, moveResult),
				macroRetrievePrimitiveResultTaint);
	}

	private DexCodeElement instrument_Invoke_External(DexInstruction_Invoke invoke, DexInstruction_MoveResult moveResult, DexCode code) {
		return generateInvoke(invoke, moveResult);
	}
	
	private static DexCodeElement generateInvoke(DexInstruction_Invoke invoke, DexInstruction_MoveResult moveResult) {
		if (moveResult == null)
			return invoke;
		else
			return new DexMacro(invoke, moveResult);
	}

	private DexSingleAuxiliaryRegister genAuxiliaryRegister() {
		return new DexSingleAuxiliaryRegister(auxiliaryRegisterId++);
	}
}
