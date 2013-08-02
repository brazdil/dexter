package uk.ac.cam.db538.dexter.transform.taint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.val;

import org.jf.dexlib.AnnotationVisibility;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexAnnotation;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode.Parameter;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayLength;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOpLiteral;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Compare;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Convert;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Move;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_NewArray;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_NewInstance;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Return;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_UnaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterType;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.transform.Transform;

import com.rx201.dx.translator.DexCodeAnalyzer;
import com.rx201.dx.translator.TypeSolver;

public class TaintTransform extends Transform {

	public TaintTransform() { }

	public TaintTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	private AuxiliaryDex dexAux;
	private CodeGenerator codeGen;

	@Override
	public void doFirst(Dex dex) {
		super.doFirst(dex);
		
		dexAux = dex.getAuxiliaryDex();
		codeGen = new CodeGenerator(dexAux);
	}

	private DexCodeAnalyzer codeAnalysis;
	private Map<DexInstruction_Invoke, CallDestinationType> invokeClassification;
	
	@Override
	public DexCode doFirst(DexCode code, DexMethod method) {
		code = super.doFirst(code, method);

		codeGen.resetAsmIds(); // purely for esthetic reasons (each method will start with a0)
		
		codeAnalysis = new DexCodeAnalyzer(code);
		codeAnalysis.analyze();
		
		val classification = InvokeClassifier.classifyMethodCalls(code, codeAnalysis);
		invokeClassification = classification.getValB();
		code = classification.getValA();
		
		return code;
	}

	@Override
	public DexCodeElement doFirst(DexCodeElement element, DexCode code, DexMethod method) {
		element = super.doFirst(element, code, method);
		
		if (element instanceof DexInstruction_Const)
			return instrument_Const((DexInstruction_Const) element);
		
		if (element instanceof DexInstruction_Invoke) {
			
			DexCodeElement nextElement = code.getInstructionList().getNextInstruction(element);
			if (!(nextElement instanceof DexInstruction_MoveResult))
				nextElement = null;
			
			CallDestinationType type = invokeClassification.get(element);
			if (type == CallDestinationType.Internal)
				return instrument_Invoke_Internal((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement);
			else if (type == CallDestinationType.External)
				return instrument_Invoke_External((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement, code, method.getMethodDef());
			else
				throw new Error("Calls should never be classified as undecidable by this point");
		}
		
		if (element instanceof DexInstruction_MoveResult)
			return DexMacro.empty(); // handled by Invoke and FillArray

		if (element instanceof DexInstruction_Return)
			return instrument_Return((DexInstruction_Return) element);

		if (element instanceof DexInstruction_Move)
			return instrument_Move((DexInstruction_Move) element);

		if (element instanceof DexInstruction_BinaryOp)
			return instrument_BinaryOp((DexInstruction_BinaryOp) element);

		if (element instanceof DexInstruction_BinaryOpLiteral)
			return instrument_BinaryOpLiteral((DexInstruction_BinaryOpLiteral) element);

		if (element instanceof DexInstruction_Compare)
			return instrument_Compare((DexInstruction_Compare) element);

		if (element instanceof DexInstruction_Convert)
			return instrument_Convert((DexInstruction_Convert) element);

		if (element instanceof DexInstruction_UnaryOp)
			return instrument_UnaryOp((DexInstruction_UnaryOp) element);
		
		if (element instanceof DexInstruction_NewInstance)
			return instrument_NewInstance((DexInstruction_NewInstance) element);

		if (element instanceof DexInstruction_NewArray)
			return instrument_NewArray((DexInstruction_NewArray) element);

		if (element instanceof DexInstruction_ArrayLength)
			return instrument_ArrayLength((DexInstruction_ArrayLength) element);
		
		return element;
		
		// TODO: throw an exception here to make sure all cases are taken care of
	}

	@Override
	public DexCode doLast(DexCode code, DexMethod method) {
		
		code = insertTaintInit(code, method);
		invokeClassification = null; // get rid of the method call classification
		codeAnalysis = null;
		
		return super.doLast(code, method);
	}
	
	private boolean canBeCalledFromExternalOrigin(MethodDefinition methodDef) {
		return methodDef.isVirtual();
	}
	
	private DexCode insertTaintInit(DexCode code, DexMethod method) {
		// If there are no parameters, no point in intializing them
		if (code.getParameters().size() <= (code.isConstructor() ? 1 : 0))
			return code;
		
		DexSingleRegister regAnnotation = codeGen.auxReg();
		DexSingleRegister regCallerName = codeGen.auxReg();
		DexSingleRegister regInitTaint = codeGen.auxReg();
		
		DexLabel labelExternal = codeGen.label();
		DexLabel labelEnd = codeGen.label();
		
		List<DexTaintRegister> primitiveTaints = filterPrimitiveTaintRegisters(code.getParameters());
		
		DexMacro initInternalOrigin = new DexMacro(
			codeGen.initPrimitiveTaints(primitiveTaints),
			codeGen.setEmptyTaint(regInitTaint),
			codeGen.initReferenceTaints(code, regInitTaint));

		DexMacro init;
		if (canBeCalledFromExternalOrigin(method.getMethodDef()))
			init = new DexMacro(
				codeGen.getMethodCaller(regCallerName),
				codeGen.ifZero(regCallerName, labelExternal),
					codeGen.getClassAnnotation(regAnnotation, regCallerName, dexAux.getAnno_InternalClass().getType()),
					codeGen.ifZero(regAnnotation, labelExternal),
						// INTERNAL ORIGIN
						initInternalOrigin,
						codeGen.jump(labelEnd),
				labelExternal,
					// EXTERNAL ORIGIN
					codeGen.setEmptyTaint(regInitTaint),
					codeGen.setAllTo(primitiveTaints, regInitTaint),
					codeGen.initReferenceTaints(code, regInitTaint),
				labelEnd);
		else
			init = initInternalOrigin;
		
		return new DexCode(code, new InstructionList(concat(init.getInstructions(), code.getInstructionList())));
	}
	
	@Override
	public void doLast(DexClass clazz) {

		// add InternalClassAnnotation
		clazz.replaceAnnotations(concat(
				clazz.getAnnotations(),
				new DexAnnotation(dexAux.getAnno_InternalClass().getType(), AnnotationVisibility.RUNTIME)));
		
		super.doLast(clazz);
	}

	private DexCodeElement instrument_Const(DexInstruction_Const insn) {
		return new DexMacro(
				codeGen.setEmptyTaint(insn.getRegTo().getTaintRegister()),
				insn);
	}

	private DexCodeElement instrument_Invoke_Internal(DexInstruction_Invoke insnInvoke, DexInstruction_MoveResult insnMoveResult) {
		DexPrototype prototype = insnInvoke.getMethodId().getPrototype();
		
		// need to store taints in the ThreadLocal ARGS array?
		DexMacro macroSetParamTaints;
		if (prototype.hasPrimitiveArgument())
			macroSetParamTaints = codeGen.setParamTaints(filterPrimitiveTaintRegisters(insnInvoke));
		else
			macroSetParamTaints = codeGen.empty();
		
		// need to retrieve taint from the ThreadLocal RES field?
		DexMacro macroGetResultTaint;
		if (insnMoveResult != null && prototype.getReturnType() instanceof DexPrimitiveType)
			macroGetResultTaint = codeGen.getResultTaint(insnMoveResult.getRegTo().getTaintRegister()); 
		else
			macroGetResultTaint = codeGen.empty();
		
		// generate instrumentation
		return new DexMacro(macroSetParamTaints, generateInvoke(insnInvoke, insnMoveResult), macroGetResultTaint);
	}

	private DexCodeElement instrument_Invoke_External(DexInstruction_Invoke insnInvoke, DexInstruction_MoveResult insnMoveResult, DexCode code, MethodDefinition methodDef) {
		DexSingleAuxiliaryRegister regCombinedTaint = codeGen.auxReg();
		if (isCallToSuperclassConstructor(insnInvoke, code, methodDef)) {
			
			// Handle calls to external superclass constructor
			
			assert(insnMoveResult == null);
			DexSingleRegister regThis = (DexSingleRegister) code.getParameters().get(0).getRegister();
			
			return new DexMacro(
					codeGen.prepareExternalCall(regCombinedTaint, insnInvoke),
					generateInvoke(insnInvoke, insnMoveResult),
					
					// At this point, the object reference is valid
					// Need to generate new TaintInternal object with it
					
					codeGen.assigner_NewExternal(regThis, regCombinedTaint),
					codeGen.assigner_NewInternal(regThis));
			
		} else {
		
			// Standard external call
			return new DexMacro(
				codeGen.prepareExternalCall(regCombinedTaint, insnInvoke),
				generateInvoke(insnInvoke, insnMoveResult),
				codeGen.finishExternalCall(regCombinedTaint, insnInvoke, insnMoveResult));
			
		}
	}
	
	private DexCodeElement instrument_Return(DexInstruction_Return insnReturn) {
		if (insnReturn.getOpcode() == RegisterType.REFERENCE)
			return insnReturn;
		else
			return new DexMacro(
				codeGen.setResultTaint(insnReturn.getRegFrom().getTaintRegister()),
				insnReturn);
	}

	private DexCodeElement instrument_Move(DexInstruction_Move insn) {
		if (insn.getType() == RegisterType.REFERENCE)
			return insn;
		else
			return new DexMacro(
				codeGen.combineTaint(insn.getRegTo(), insn.getRegFrom()),
				insn);
	}

	private DexCodeElement instrument_BinaryOp(DexInstruction_BinaryOp insn) {
		return new DexMacro(
			codeGen.combineTaint(insn.getRegTo(), insn.getRegArgA(), insn.getRegArgB()),
			insn);
	}
	
	private DexCodeElement instrument_BinaryOpLiteral(DexInstruction_BinaryOpLiteral insn) {
		return new DexMacro(
			codeGen.combineTaint(insn.getRegTo(), insn.getRegArgA()),
			insn);
	}

	private DexCodeElement instrument_Compare(DexInstruction_Compare insn) {
		return new DexMacro(
			codeGen.combineTaint(insn.getRegTo(), insn.getRegSourceA(), insn.getRegSourceB()),
			insn);
	}

	private DexCodeElement instrument_Convert(DexInstruction_Convert insn) {
		return new DexMacro(
			codeGen.combineTaint(insn.getRegTo(), insn.getRegFrom()),
			insn);
	}

	private DexCodeElement instrument_UnaryOp(DexInstruction_UnaryOp insn) {
		return new DexMacro(
			codeGen.combineTaint(insn.getRegTo(), insn.getRegFrom()),
			insn);
	}

	private DexCodeElement instrument_NewInstance(DexInstruction_NewInstance insn) {
		// nothing happening here...
		// taint initialization handled as the constructor returns
		return insn;
	}

	private DexCodeElement instrument_NewArray(DexInstruction_NewArray insn) {
		return insn;
//		DexSingleRegister regTo = insn.getRegTo();
//		DexSingleRegister regSize = insn.getRegSize();
//		
//		DexSingleRegister regTaintObject;
//		if (regTo.equals(regSize))
//			regTaintObject = codeGen.auxReg();
//		else
//			regTaintObject = regTo.getTaintRegister();
//
//		if (insn.getValue().getElementType() instanceof DexPrimitiveType)
//			return new DexMacro(
//				codeGen.newTaint_ArrayPrimitive(regTaintObject, regSize),
//				insn,
//				codeGen.moveObj(regTo.getTaintRegister(), regTaintObject));
//		else
//			// WRONG!!! Change to TaintArrayReference
//			// Only here to be able to compile the unit tests
//			return new DexMacro(
//					codeGen.newTaint_ArrayPrimitive(regTaintObject, regSize),
//					insn,
//					codeGen.moveObj(regTo.getTaintRegister(), regTaintObject));
	}

	private DexCodeElement instrument_ArrayLength(DexInstruction_ArrayLength insn) {
		return new DexMacro(
			codeGen.setZero(insn.getRegTo().getTaintRegister()),
			insn);
			
//		return new DexMacro(
//			codeGen.getTaint_Array_Length(insn.getRegTo().getTaintRegister(), insn.getRegArray().getTaintRegister()),
//			insn);
	}

	// UTILS
	
	private static DexCodeElement generateInvoke(DexInstruction_Invoke invoke, DexInstruction_MoveResult moveResult) {
		if (moveResult == null)
			return invoke;
		else
			return new DexMacro(invoke, moveResult);
	}
	
	protected static <T> List<? extends T> concat(Collection<? extends T> list1, Collection<? extends T> list2) {
		List<T> result = new ArrayList<T>(list1.size() + list2.size());
		result.addAll(list1);
		result.addAll(list2);
		return result;
	}

	protected static <T> List<? extends T> concat(Collection<? extends T> list1, T elem) {
		List<T> result = new ArrayList<T>(list1.size() + 1);
		result.addAll(list1);
		result.add(elem);
		return result;
	}

	private static boolean hasPrimitiveArgument(DexCode code) {
		for (Parameter param : code.getParameters())
			if (param.getType() instanceof DexPrimitiveType)
				return true;
		return false;
	}
	
	private static List<DexTaintRegister> filterPrimitiveTaintRegisters(DexInstruction_Invoke insnInvoke) {
		DexPrototype prototype = insnInvoke.getMethodId().getPrototype();
		boolean isStatic = insnInvoke.getCallType() == Opcode_Invoke.Static;
		int paramCount = prototype.getParameterCount(isStatic);
		
		List<DexTaintRegister> taintRegs = new ArrayList<DexTaintRegister>(paramCount);
		
		for (int i = 0; i < paramCount; i++) {
			DexRegisterType paramType = prototype.getParameterType(i, isStatic, insnInvoke.getClassType());
			if (paramType instanceof DexPrimitiveType)
				taintRegs.add(insnInvoke.getArgumentRegisters().get(i).getTaintRegister());
		}
		
		return taintRegs;
	}
	
	private static List<DexTaintRegister> filterPrimitiveTaintRegisters(List<Parameter> params) {
		List<DexTaintRegister> taintRegs = new ArrayList<DexTaintRegister>(params.size());

		for (Parameter param : params)
			if (param.getType() instanceof DexPrimitiveType)
				taintRegs.add(param.getRegister().getTaintRegister());
		
		return taintRegs;
	}
	
	private boolean isCallToSuperclassConstructor(DexInstruction_Invoke insnInvoke, DexCode code, MethodDefinition insideMethodDef) {
		return 
			insideMethodDef.isConstructor() &&
			insnInvoke.getMethodId().isConstructor() &&
			insnInvoke.getClassType().equals(insideMethodDef.getParentClass().getSuperclass().getType()) &&
			isThisValue(insnInvoke, code);
	}
	
	private boolean isThisValue(DexInstruction_Invoke insnInvoke, DexCode code) {
		DexRegister firstInsnParam = insnInvoke.getArgumentRegisters().get(0);
		
		// First check that the register is the same as this param of the method
		DexRegister firstMethodParam = code.getParameters().get(0).getRegister();
		if (firstMethodParam != firstInsnParam)
			return false;

		// Then check that they are unified, i.e. reg inherits the value
		TypeSolver solverStart = codeAnalysis.getStartOfMethod().getDefinedRegisterSolver(firstMethodParam);
		TypeSolver solverRefPoint = codeAnalysis.reverseLookup(insnInvoke).getUsedRegisterSolver(firstInsnParam);
		
		return solverStart.areUnified(solverRefPoint);
	}
}
