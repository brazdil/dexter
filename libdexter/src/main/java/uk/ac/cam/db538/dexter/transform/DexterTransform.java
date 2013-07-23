package uk.ac.cam.db538.dexter.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.val;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Return;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterType;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;
import uk.ac.cam.db538.dexter.transform.macros.MethodCallMacros;

public class DexterTransform extends Transform {

	public DexterTransform() { }

	public DexterTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	private MethodCallMacros macrosMethodCall;

	@Override
	public void doFirst(Dex dex) {
		super.doFirst(dex);
		
		macrosMethodCall = new MethodCallMacros(dex.getAuxiliaryDex());
	}

	private int auxiliaryRegisterId;
	private Map<DexInstruction_Invoke, CallDestinationType> methodCallClassification;
	
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
			
			CallDestinationType type = methodCallClassification.get(element);
			if (type == CallDestinationType.Internal)
				return instrument_Invoke_Internal((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement);
			else if (type == CallDestinationType.External)
				return instrument_Invoke_External((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement);
			else
				throw new Error("Calls should never be classified as undecidable by this point");
			
		}
		
		if (element instanceof DexInstruction_MoveResult) {
			return DexMacro.empty(); // handled by Invoke and FillArray
		}

		if (element instanceof DexInstruction_Return) {
			return instrument_Return((DexInstruction_Return) element);
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

	private DexCodeElement instrument_Invoke_Internal(DexInstruction_Invoke insnInvoke, DexInstruction_MoveResult insnMoveResult) {
		DexPrototype prototype = insnInvoke.getMethodId().getPrototype();
		
		DexMacro macroSetParamTaints;
		DexMacro macroGetResultTaint;
		
		// need to store taints in the ThreadLocal ARGS array?
		
		if (prototype.hasPrimitiveArgument()) {
			
			boolean isStatic = insnInvoke.getCallType() == Opcode_Invoke.Static;
			int paramCount = prototype.getParameterCount(isStatic);
			List<DexTaintRegister> taintRegs = new ArrayList<DexTaintRegister>(paramCount);
			
			for (int i = 0; i < paramCount; i++) {
				DexRegisterType paramType = prototype.getParameterType(i, isStatic, insnInvoke.getClassType());
				if (paramType instanceof DexPrimitiveType)
					taintRegs.add(insnInvoke.getArgumentRegisters().get(i).getTaintRegister());
			}
			
			macroSetParamTaints = macrosMethodCall.setParamTaints(auxReg(), auxReg(), taintRegs);
			
		} else
			macroSetParamTaints = DexMacro.empty();
		
		// need to retrieve taint from the ThreadLocal RES field?
		
		if (insnMoveResult != null && prototype.getReturnType() instanceof DexPrimitiveType)
			macroGetResultTaint = macrosMethodCall.getResultTaint(auxReg(), insnMoveResult.getRegTo().getTaintRegister()); 
		else
			macroGetResultTaint = DexMacro.empty();
		
		// generate instrumentation
		
		return new DexMacro(
				macroSetParamTaints,
				generateInvoke(insnInvoke, insnMoveResult),
				macroGetResultTaint);
	}

	private DexCodeElement instrument_Invoke_External(DexInstruction_Invoke insnInvoke, DexInstruction_MoveResult insnMoveResult) {
		return generateInvoke(insnInvoke, insnMoveResult);
	}
	
	private DexCodeElement instrument_Return(DexInstruction_Return insnReturn) {
		if (insnReturn.getOpcode() == RegisterType.REFERENCE)
			return insnReturn;
		else
			return new DexMacro(
				macrosMethodCall.setResultTaint(auxReg(), auxReg(), insnReturn.getRegFrom().getTaintRegister()),
				insnReturn);
	}

	// UTILS
	
	private static DexCodeElement generateInvoke(DexInstruction_Invoke invoke, DexInstruction_MoveResult moveResult) {
		if (moveResult == null)
			return invoke;
		else
			return new DexMacro(invoke, moveResult);
	}

	private DexSingleAuxiliaryRegister auxReg() {
		return new DexSingleAuxiliaryRegister(auxiliaryRegisterId++);
	}
}
