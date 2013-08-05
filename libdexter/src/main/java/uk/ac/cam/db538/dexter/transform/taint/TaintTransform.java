package uk.ac.cam.db538.dexter.transform.taint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.val;

import org.jf.dexlib.AnnotationVisibility;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexAnnotation;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.DexUtils;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode.Parameter;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayLength;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOpLiteral;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_CheckCast;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Compare;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ConstString;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Convert;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_InstanceGet;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_InstancePut;
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
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;
import uk.ac.cam.db538.dexter.hierarchy.ClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.InstanceFieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.transform.Transform;

import com.rx201.dx.translator.DexCodeAnalyzer;
import com.rx201.dx.translator.TypeSolver;

public class TaintTransform extends Transform {

	public TaintTransform() { }

	public TaintTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	private Dex dex;
	private AuxiliaryDex dexAux;
	private CodeGenerator codeGen;
	private RuntimeHierarchy hierarchy;
	private DexTypeCache typeCache;

	private Map<DexInstanceField, DexInstanceField> taintInstanceFields;

	@Override
	public void doFirst(Dex dex) {
		super.doFirst(dex);
		
		this.dex = dex;
		dexAux = dex.getAuxiliaryDex();
		codeGen = new CodeGenerator(dexAux);
		hierarchy = dexAux.getHierarchy();
		typeCache = hierarchy.getTypeCache();

		taintInstanceFields = new HashMap<DexInstanceField, DexInstanceField>();
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
		
		if (element instanceof DexInstruction_ConstString)
			return instrument_ConstString((DexInstruction_ConstString) element);

		if (element instanceof DexInstruction_Invoke) {
			
			DexCodeElement nextElement = code.getInstructionList().getNextInstruction(element);
			if (!(nextElement instanceof DexInstruction_MoveResult))
				nextElement = null;
			
			CallDestinationType type = invokeClassification.get(element);
			if (type == CallDestinationType.Internal)
				return instrument_Invoke_Internal((DexInstruction_Invoke) element, (DexInstruction_MoveResult) nextElement, code, method.getMethodDef());
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

		if (element instanceof DexInstruction_CheckCast)
			return instrument_CheckCast((DexInstruction_CheckCast) element);

		if (element instanceof DexInstruction_ArrayLength)
			return instrument_ArrayLength((DexInstruction_ArrayLength) element);
		
		if (element instanceof DexInstruction_InstancePut)
			return instrument_InstancePut((DexInstruction_InstancePut) element);
		
		if (element instanceof DexInstruction_InstanceGet)
			return instrument_InstanceGet((DexInstruction_InstanceGet) element);

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
	
	@Override
	public void doLast(Dex dex) {
		super.doLast(dex);
		
		// insert classes from dexAux to the resulting DEX
		dex.addClasses(dexAux.getClasses());
	}

	private DexCodeElement instrument_Const(DexInstruction_Const insn) {
		return new DexMacro(
				codeGen.setEmptyTaint(insn.getRegTo().getTaintRegister()),
				insn);
	}

	private DexCodeElement instrument_ConstString(DexInstruction_ConstString insn) {
		return new DexMacro(
				insn,
				codeGen.newEmptyExternalTaint(insn.getRegTo()));
	}

	private DexCodeElement instrument_Invoke_Internal(DexInstruction_Invoke insnInvoke, DexInstruction_MoveResult insnMoveResult, DexCode code, MethodDefinition methodDef) {
		DexPrototype prototype = insnInvoke.getMethodId().getPrototype();
		
		// Need to store taints in the ThreadLocal ARGS array ?
		
		DexCodeElement macroSetParamTaints;
		if (prototype.hasPrimitiveArgument())
			macroSetParamTaints = codeGen.setParamTaints(filterPrimitiveTaintRegisters(insnInvoke));
		else
			macroSetParamTaints = codeGen.empty();
		
		
		DexCodeElement macroHandleResult;
		
		// Need to retrieve taint from the ThreadLocal RES field ?
		
		if (insnMoveResult != null && prototype.getReturnType() instanceof DexPrimitiveType)
			macroHandleResult = codeGen.getResultTaint(insnMoveResult.getRegTo().getTaintRegister());
		
		// Was this a call to a constructor ?
		
		else if (insnInvoke.getMethodId().isConstructor()) {

			assert(insnMoveResult == null);
			DexSingleRegister regThis = (DexSingleRegister) insnInvoke.getArgumentRegisters().get(0);
			
			if (isCallToSuperclassConstructor(insnInvoke, code, methodDef))
				// Handle calls to internal superclass constructor
				macroHandleResult = codeGen.assigner_NewInternal(regThis);
			else
				// Handle call to a standard internal constructor
				macroHandleResult = codeGen.assigner_Lookup(regThis, insnInvoke.getClassType()); 
			
		} else
			macroHandleResult = codeGen.empty();
		
		// generate instrumentation
		return new DexMacro(macroSetParamTaints, generateInvoke(insnInvoke, insnMoveResult), macroHandleResult);
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
			return new DexMacro(
				codeGen.moveTaintObj((DexSingleRegister) insn.getRegTo(), (DexSingleRegister) insn.getRegFrom()),
				insn);
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
		DexSingleRegister regTo = insn.getRegTo();
		DexSingleRegister regSize = insn.getRegSize();
		
		DexSingleRegister auxSize;
		DexSingleRegister auxSizeTaint;
		
		// We need to be careful if the instruction overwrites the size register
		
		if (regTo.equals(regSize)) {
			auxSize = codeGen.auxReg();
			auxSizeTaint = codeGen.auxReg();
		} else {
			auxSize = regSize;
			auxSizeTaint = regSize.getTaintRegister();
		}

		if (insn.getValue().getElementType() instanceof DexPrimitiveType)
			return new DexMacro(
				codeGen.movePrim(auxSize, regSize),
				codeGen.movePrim(auxSizeTaint, regSize.getTaintRegister()),
				insn,
				codeGen.assigner_NewArrayPrimitive(regTo, auxSize, auxSizeTaint));
		else
			return new DexMacro(
				codeGen.movePrim(auxSize, regSize),
				codeGen.movePrim(auxSizeTaint, regSize.getTaintRegister()),
				insn,
				codeGen.assigner_NewArrayReference(regTo, auxSize, auxSizeTaint));
	}

	private DexCodeElement instrument_CheckCast(DexInstruction_CheckCast insn) {
		return new DexMacro(
			codeGen.cast(insn.getRegObject().getTaintRegister(), (DexReferenceType) taintType(insn.getValue())),
			insn);
	}
	
	private DexCodeElement instrument_ArrayLength(DexInstruction_ArrayLength insn) {
		return new DexMacro(
			codeGen.getTaint_Array_Length(insn.getRegTo().getTaintRegister(), insn.getRegArray().getTaintRegister()),
			insn);
	}
	
	private DexCodeElement instrument_InstancePut(DexInstruction_InstancePut insnIput) {
		InstanceFieldDefinition fieldDef = insnIput.getFieldDef();
		ClassDefinition classDef = (ClassDefinition) fieldDef.getParentClass();
		
		if (classDef.isInternal()) {
		
			DexClass parentClass = dex.getClass(classDef);
			DexInstanceField field = parentClass.getInstanceField(fieldDef);
			DexInstanceField taintField = getTaintField(field);
			DexTaintRegister regFromTaint = insnIput.getRegFrom().getTaintRegister(); 
			
			return new DexMacro(
				codeGen.iput(regFromTaint, insnIput.getRegObject(), taintField.getFieldDef()),	
				insnIput);
		
		} else 
			throw new UnsupportedOperationException();
	}

	private DexCodeElement instrument_InstanceGet(DexInstruction_InstanceGet insnIget) {
		InstanceFieldDefinition fieldDef = insnIget.getFieldDef();
		ClassDefinition classDef = (ClassDefinition) fieldDef.getParentClass();
		
		if (classDef.isInternal()) {
		
			DexClass parentClass = dex.getClass(classDef);
			DexInstanceField field = parentClass.getInstanceField(fieldDef);
			DexInstanceField taintField = getTaintField(field);
			DexTaintRegister regToTaint = insnIget.getRegTo().getTaintRegister(); 
			
			return new DexMacro(
				codeGen.iget(regToTaint, insnIget.getRegObject(), taintField.getFieldDef()),	
				insnIget);
		
		} else 
			throw new UnsupportedOperationException();
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
	
	private DexInstanceField getTaintField(DexInstanceField field) {
		
		// Check if it has been already created
		
		DexInstanceField cachedTaintField = taintInstanceFields.get(field);
		if (cachedTaintField != null)
			return cachedTaintField;

		// It hasn't, so let's create a new one...
		
		ClassDefinition classDef = (ClassDefinition) field.getParentClass().getClassDef();
		
		// Figure out a non-conflicting name for the new field
		
		String newName = "t_" + field.getFieldDef().getFieldId().getName();
		String suffix = "";
		long suffixNumber = 0L;
		while (classDef.getInstanceField(newName + suffix) != null)
			suffix = "$" + Long.toString(++suffixNumber); // this needs to be reflected in the name conflict test
		newName += suffix;

		// Generate the new taint field
		
		DexFieldId fieldId = DexFieldId.parseFieldId(newName, taintType(field.getFieldDef().getFieldId().getType()), typeCache);
		int fieldAccessFlags = DexUtils.assembleAccessFlags(field.getFieldDef().getAccessFlags());
		InstanceFieldDefinition fieldDef = new InstanceFieldDefinition(classDef, fieldId, fieldAccessFlags);
		classDef.addDeclaredInstanceField(fieldDef);
		
		DexClass parentClass = field.getParentClass();
		DexInstanceField taintField = new DexInstanceField(parentClass, fieldDef);
		parentClass.replaceInstanceFields(concat(parentClass.getInstanceFields(), taintField));
		
		// Cache it
		
		taintInstanceFields.put(field, taintField);
		
		// Return
		
		return taintField;
	}
	
	private DexRegisterType taintType(DexRegisterType type) {
		switch(hierarchy.classifyType(type)) {
		case PRIMITIVE:
			return typeCache.getCachedType_Integer();
		case REF_EXTERNAL:
			return dexAux.getType_TaintExternal().getClassDef().getType();
		case REF_INTERNAL:
			return dexAux.getType_TaintInternal().getClassDef().getType();
		case ARRAY_PRIMITIVE:
			return dexAux.getType_TaintArrayPrimitive().getClassDef().getType();
		case ARRAY_REFERENCE:
			return dexAux.getType_TaintArrayReference().getClassDef().getType();
		case REF_UNDECIDABLE:
			return dexAux.getType_Taint().getClassDef().getType();
		default:
			throw new UnsupportedOperationException();
		}
	}
}
