package uk.ac.cam.db538.dexter.transform.taint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.val;

import org.jf.dexlib.AnnotationVisibility;
import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexAnnotation;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.DexUtils;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode.Parameter;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCatchAll;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayGet;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayLength;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayPut;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOpLiteral;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_CheckCast;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Compare;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ConstClass;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ConstString;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Convert;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Goto;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_IfTest;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_IfTestZero;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_InstanceGet;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_InstanceOf;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_InstancePut;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Monitor;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Move;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveException;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_NewArray;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_NewInstance;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Return;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ReturnVoid;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_StaticGet;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_StaticPut;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Switch;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Throw;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_UnaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_GetPut;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterType;
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;
import uk.ac.cam.db538.dexter.hierarchy.ClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.InstanceFieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy.TypeClassification;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;
import uk.ac.cam.db538.dexter.transform.InvokeClassifier;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.Transform;
import uk.ac.cam.db538.dexter.transform.TryBlockSplitter;
import uk.ac.cam.db538.dexter.utils.Utils;
import uk.ac.cam.db538.dexter.utils.Utils.NameAcceptor;

import com.rx201.dx.translator.DexCodeAnalyzer;
import com.rx201.dx.translator.RopType;
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
	private Map<DexStaticField, DexStaticField> taintStaticFields;

	@Override
	public void doFirst(Dex dex) {
		super.doFirst(dex);
		
		this.dex = dex;
		dexAux = dex.getAuxiliaryDex();
		codeGen = new CodeGenerator(dexAux);
		hierarchy = dexAux.getHierarchy();
		typeCache = hierarchy.getTypeCache();

		taintInstanceFields = new HashMap<DexInstanceField, DexInstanceField>();
		taintStaticFields = new HashMap<DexStaticField, DexStaticField>();
	}

	private DexCodeAnalyzer codeAnalysis;
	private Map<MethodCall, CallDestinationType> invokeClassification;
	private Set<DexCodeElement> noninstrumentableElements;
	
	@Override
	public DexCode doFirst(DexCode code, DexMethod method) {
		code = super.doFirst(code, method);

		codeGen.resetAsmIds(); // purely for esthetic reasons (each method will start with a0)
		
		codeAnalysis = new DexCodeAnalyzer(code);
		codeAnalysis.analyze();
		
		code = InvokeClassifier.collapseCalls(code);
		val classification = InvokeClassifier.classifyMethodCalls(code, codeAnalysis, codeGen);
		
		code = classification.getValA();
		invokeClassification = classification.getValB();
		noninstrumentableElements = classification.getValC();
		
		return code;
	}

	@Override
	public DexCodeElement doFirst(DexCodeElement element, DexCode code, DexMethod method) {
		element = super.doFirst(element, code, method);

		// code elements (markers etc.) should be left alone
		if (!(element instanceof DexInstruction) && !(element instanceof MethodCall))
			return element;
		
		// instructions added in preparation stage should be skipped over
		if (noninstrumentableElements.contains(element))
			return element;
		
		if (element instanceof DexInstruction_Const)
			return instrument_Const((DexInstruction_Const) element);
		
		if (element instanceof DexInstruction_ConstString)
			return instrument_ConstString((DexInstruction_ConstString) element);

		if (element instanceof DexInstruction_ConstClass)
			return instrument_ConstClass((DexInstruction_ConstClass) element);

		if (element instanceof MethodCall) {
			CallDestinationType type = invokeClassification.get(element);
			if (type == CallDestinationType.Internal)
				return instrument_MethodCall_Internal((MethodCall) element, code, method.getMethodDef());
			else if (type == CallDestinationType.External)
				return instrument_MethodCall_External((MethodCall) element, code, method.getMethodDef());
			else
				throw new Error("Calls should never be classified as undecidable by this point");
		}
		
		if (element instanceof DexInstruction_Invoke ||
			element instanceof DexInstruction_MoveResult)
			throw new Error("All method calls should be collapsed at this point");

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

		if (element instanceof DexInstruction_InstanceOf)
			return instrument_InstanceOf((DexInstruction_InstanceOf) element);

		if (element instanceof DexInstruction_ArrayLength)
			return instrument_ArrayLength((DexInstruction_ArrayLength) element);
		
		if (element instanceof DexInstruction_ArrayPut)
			return instrument_ArrayPut((DexInstruction_ArrayPut) element);

		if (element instanceof DexInstruction_ArrayGet)
			return instrument_ArrayGet((DexInstruction_ArrayGet) element);

		if (element instanceof DexInstruction_InstancePut)
			return instrument_InstancePut((DexInstruction_InstancePut) element);
		
		if (element instanceof DexInstruction_InstanceGet)
			return instrument_InstanceGet((DexInstruction_InstanceGet) element);

		if (element instanceof DexInstruction_StaticPut)
			return instrument_StaticPut((DexInstruction_StaticPut) element);
		
		if (element instanceof DexInstruction_StaticGet)
			return instrument_StaticGet((DexInstruction_StaticGet) element);

		if (element instanceof DexInstruction_MoveException)
			return instrument_MoveException((DexInstruction_MoveException) element);

		/*
		 * Monitor instructions can throw the IllegalMonitorStateException,
		 * but they do so to signal that a monitor was accessed from 
		 * a thread that doesn't own it. The assumption made here is that
		 * this behaviour does not leak any information and therefore
		 * instrumentation is not necessary.
		 */
		if (element instanceof DexInstruction_Monitor)
			return element;
		
		// instructions that do not require instrumentation
		if (element instanceof DexInstruction_Goto || 
			element instanceof DexInstruction_IfTest ||
			element instanceof DexInstruction_IfTestZero ||
			element instanceof DexInstruction_Switch ||
			element instanceof DexInstruction_Throw ||
			element instanceof DexInstruction_ReturnVoid)
			return element;
		
		throw new UnsupportedOperationException("Unhandled code element " + element.getClass().getSimpleName());
	}

	@Override
	public DexCode doLast(DexCode code, DexMethod method) {

		code = TryBlockSplitter.checkAndFixTryBlocks(code);
		code = InvokeClassifier.expandCalls(code);
		code = insertTaintInit(code, method);
		
		invokeClassification = null;
		noninstrumentableElements = null;
		codeAnalysis = null;
		
		return super.doLast(code, method);
	}
	
	@Override
	public DexMethod doLast(DexMethod method) {
		method = super.doLast(method);
		if (method.getMethodBody() != null) {
			DexAnnotation anno = new DexAnnotation(dexAux.getAnno_InternalMethod().getType(), AnnotationVisibility.RUNTIME);
			method = new DexMethod(method, anno);
		}
		
		return method;
	}

	@Override
	public void doLast(DexClass clazz) {

		// add InternalClassAnnotation
		clazz.replaceAnnotations(Utils.concat(
				clazz.getAnnotations(),
				new DexAnnotation(dexAux.getAnno_InternalClass().getType(), AnnotationVisibility.RUNTIME)));
		
		// implement the InternalDataStructure interface
		if (!clazz.getClassDef().isInterface()) {
			clazz.getClassDef().addImplementedInterface((InterfaceDefinition) dexAux.getType_InternalStructure().getClassDef());
			generateGetTaint(clazz);
			generateSetTaint(clazz);
		}
		
		super.doLast(clazz);
	}

	@Override
	public void doLast(Dex dex) {
		super.doLast(dex);
		
		// insert classes from dexAux to the resulting DEX
		dex.addClasses(dexAux.getClasses());
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
		
		return new DexCode(code, new InstructionList(Utils.concat(init.getInstructions(), code.getInstructionList())));
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

	private DexCodeElement instrument_ConstClass(DexInstruction_ConstClass insn) {
		// TODO: consider treating Class objects as immutable?
		return new DexMacro(
				insn,
				codeGen.taintLookup(insn.getRegTo(), TypeClassification.REF_EXTERNAL));
	}

	private DexCodeElement instrument_MethodCall_Internal(MethodCall methodCall, DexCode code, MethodDefinition methodDef) {
		DexInstruction_Invoke insnInvoke = methodCall.getInvoke();
		DexInstruction_MoveResult insnMoveResult = methodCall.getResult();
		
		DexPrototype prototype = insnInvoke.getMethodId().getPrototype();
		
		// Need to store taints in the ThreadLocal ARGS array ?
		
		DexCodeElement macroSetParamTaints;
		if (prototype.hasPrimitiveArgument())
			macroSetParamTaints = codeGen.setParamTaints(filterPrimitiveTaintRegisters(insnInvoke));
		else
			macroSetParamTaints = codeGen.empty();
		
		
		DexCodeElement macroHandleResult;
		
		// Need to retrieve taint from the ThreadLocal RES field ?
		
		if (methodCall.hasResult() && prototype.getReturnType() instanceof DexPrimitiveType)
			macroHandleResult = codeGen.getResultTaint(insnMoveResult.getRegTo().getTaintRegister());
		
		// Was this a call to a constructor ?
		
		else if (insnInvoke.getMethodId().isConstructor()) {

			assert(!methodCall.hasResult());
			DexSingleRegister regThis = (DexSingleRegister) insnInvoke.getArgumentRegisters().get(0);
			
			if (isCallToSuperclassConstructor(insnInvoke, code, methodDef))
				// Handle calls to internal superclass constructor
				macroHandleResult = codeGen.assigner_NewInternal(regThis);
			else
				// Handle call to a standard internal constructor
				macroHandleResult = codeGen.taintLookup(regThis, insnInvoke.getClassType()); 
			
		} else
			macroHandleResult = codeGen.empty();
		
		// generate instrumentation
		return new DexMacro(macroSetParamTaints, methodCall, macroHandleResult);
	}

	private DexCodeElement instrument_MethodCall_External(MethodCall methodCall, DexCode code, MethodDefinition methodDef) {
		DexInstruction_Invoke insnInvoke = methodCall.getInvoke();
		DexInstruction_MoveResult insnMoveResult = methodCall.getResult();
		
		DexSingleAuxiliaryRegister regCombinedTaint = codeGen.auxReg();
		if (isCallToSuperclassConstructor(insnInvoke, code, methodDef)) {
			
			// Handle calls to external superclass constructor
			
			assert(!methodCall.hasResult());
			DexSingleRegister regThis = (DexSingleRegister) code.getParameters().get(0).getRegister();
			
			return new DexMacro(
					codeGen.prepareExternalCall(regCombinedTaint, insnInvoke),
					methodCall,
					
					// At this point, the object reference is valid
					// Need to generate new TaintInternal object with it
					
					codeGen.taintCreate_External(regThis, regCombinedTaint),
					codeGen.assigner_NewInternal(regThis));
			
		} else {
		
			// Standard external call
			return new DexMacro(
				codeGen.prepareExternalCall(regCombinedTaint, insnInvoke),
				methodCall,
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
				codeGen.move_tobj((DexSingleRegister) insn.getRegTo(), (DexSingleRegister) insn.getRegFrom()),
				insn);
		else
			return new DexMacro(
				codeGen.combineTaint(insn.getRegTo(), insn.getRegFrom()),
				insn);
	}

	private DexCodeElement instrument_BinaryOp(DexInstruction_BinaryOp insn) {
		if (insn.isDividing()) {
			DexCatchAll catchAll = codeGen.catchAll();
			DexTryStart tryBlock = codeGen.tryBlock(catchAll);
			DexLabel lEnd = codeGen.label();
			
			DexRegister regTo = insn.getRegTo();
			DexRegister regA = insn.getRegArgA();
			DexRegister regB = insn.getRegArgB();
			DexSingleRegister regA_Taint = regA.getTaintRegister();
			DexSingleRegister regB_Taint = regB.getTaintRegister();
			DexSingleRegister regEx = codeGen.auxReg();

			return new DexMacro(
				tryBlock,
				insn,
				codeGen.combineTaint(regTo, regA_Taint, regB_Taint),
				codeGen.jump(lEnd),
				tryBlock.getEndMarker(),
				catchAll,
				codeGen.move_ex(regEx),
				codeGen.taintCreate_External(null, regEx, regB_Taint), // only cache the taint
				codeGen.thrw(regEx),
				lEnd);
		} else
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
				codeGen.move_prim(auxSize, regSize),
				codeGen.move_prim(auxSizeTaint, regSize.getTaintRegister()),
				insn,
				codeGen.assigner_NewArrayPrimitive(regTo, auxSize, auxSizeTaint));
		else
			return new DexMacro(
				codeGen.move_prim(auxSize, regSize),
				codeGen.move_prim(auxSizeTaint, regSize.getTaintRegister()),
				insn,
				codeGen.assigner_NewArrayReference(regTo, auxSize, auxSizeTaint));
	}

	private DexCodeElement instrument_CheckCast(DexInstruction_CheckCast insn) {
		return new DexMacro(
			codeGen.cast(insn.getRegObject().getTaintRegister(), (DexReferenceType) taintType(insn.getValue())),
			insn);
	}
	
	private DexCodeElement instrument_InstanceOf(DexInstruction_InstanceOf insn) {
		// TODO: if the argument is NULL, it will always return FALSE
		return new DexMacro(
				codeGen.getTaint(insn.getRegTo().getTaintRegister(), insn.getRegObject()),
				insn);
	}
	
	private DexCodeElement instrument_ArrayLength(DexInstruction_ArrayLength insn) {
		return new DexMacro(
			codeGen.getTaint_Array_Length(insn.getRegTo().getTaintRegister(), insn.getRegArray().getTaintRegister()),
			insn);
	}
	
	private DexCodeElement instrument_ArrayPut(DexInstruction_ArrayPut insn) {
		DexTaintRegister regFromTaint = insn.getRegFrom().getTaintRegister();
		DexTaintRegister regArrayTaint = insn.getRegArray().getTaintRegister();
		
		if (insn.getOpcode() == Opcode_GetPut.Object) {
			return new DexMacro(
				codeGen.setTaint_ArrayReference(regFromTaint, regArrayTaint, insn.getRegIndex()),
				insn);
		} else {
			return new DexMacro(
				codeGen.setTaint_ArrayPrimitive(regFromTaint, regArrayTaint, insn.getRegIndex()),
				insn);
		}
	}

	private DexCodeElement instrument_ArrayGet(DexInstruction_ArrayGet insn) {
		DexTaintRegister regToTaint = insn.getRegTo().getTaintRegister();
		DexTaintRegister regArrayTaint = insn.getRegArray().getTaintRegister();
		
		DexRegister regTo = insn.getRegTo();
		DexSingleRegister regIndex = insn.getRegIndex();
		DexSingleRegister regIndexBackup;
		if (regTo.equals(regIndex))
			regIndexBackup = codeGen.auxReg();
		else
			regIndexBackup = regIndex;

		if (insn.getOpcode() == Opcode_GetPut.Object) {
			RopType returnType = codeAnalysis.reverseLookup(insn).getDefinedRegisterSolver(insn.getRegTo()).getType();
			if (returnType.category != RopType.Category.Reference)
				throw new RuntimeException("Unknown type of AGET return value");
			
			return new DexMacro(
				codeGen.move_prim(regIndexBackup, regIndex),
				insn,
				codeGen.getTaint_ArrayReference(regToTaint, regArrayTaint, regIndexBackup),
				codeGen.cast(regToTaint, (DexReferenceType) taintType(returnType.type)));
		} else {
			return new DexMacro(
				codeGen.move_prim(regIndexBackup, regIndex),
				insn,
				codeGen.getTaint_ArrayPrimitive(regToTaint, regArrayTaint, regIndexBackup));
		}
	}

	private DexCodeElement instrument_InstancePut(DexInstruction_InstancePut insnIput) {
		InstanceFieldDefinition fieldDef = insnIput.getFieldDef();
		ClassDefinition classDef = (ClassDefinition) fieldDef.getParentClass();
		
		/*
		 * The field definition points directly to the accessed field (looked up 
		 * during parsing). Therefore we can check whether the containing class is 
		 * internal/external.
		 */
		
		if (classDef.isInternal()) {
		
			DexClass parentClass = dex.getClass(classDef);
			DexInstanceField field = parentClass.getInstanceField(fieldDef);
			DexInstanceField taintField = getTaintField(field);
			DexTaintRegister regFromTaint = insnIput.getRegFrom().getTaintRegister(); 
			
			return new DexMacro(
				codeGen.iput(regFromTaint, insnIput.getRegObject(), taintField.getFieldDef()),	
				insnIput);
		
		} else {
			
			if (insnIput.getFieldDef().getFieldId().getType() instanceof DexPrimitiveType)
				return new DexMacro(
					codeGen.setTaintExternal(insnIput.getRegFrom().getTaintRegister(), insnIput.getRegObject()),
					insnIput);
			else
				return new DexMacro(
					codeGen.propagateTaintExternal(insnIput.getRegObject(), (DexSingleRegister) insnIput.getRegFrom()),
					insnIput);
			
		}
			
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
		
		} else {

			DexRegisterType resultType = insnIget.getFieldDef().getFieldId().getType();
			
			if (resultType instanceof DexPrimitiveType)
				
				return new DexMacro(
					codeGen.getTaintExternal(insnIget.getRegTo().getTaintRegister(), insnIget.getRegObject()),
					insnIget);
			
			else {
				
				DexSingleRegister regObjectTaintBackup;
				DexSingleRegister regTo = (DexSingleRegister) insnIget.getRegTo();
				DexSingleRegister regObject = insnIget.getRegObject();
				
				if (regObject.equals(regTo))
					regObjectTaintBackup = codeGen.auxReg();
				else
					regObjectTaintBackup = regObject.getTaintRegister();
				
				return new DexMacro(
					codeGen.move_obj(regObjectTaintBackup, regObject.getTaintRegister()),
					insnIget,
					codeGen.taintLookup(regTo, (DexReferenceType) resultType),
					codeGen.propagateTaintExternal(regTo, regObjectTaintBackup));
				
			}
		}
	}
	
	private DexCodeElement instrument_StaticPut(DexInstruction_StaticPut insnSput) {
		StaticFieldDefinition fieldDef = insnSput.getFieldDef();
		ClassDefinition classDef = (ClassDefinition) fieldDef.getParentClass();
		
		/*
		 * The field definition points directly to the accessed field (looked up 
		 * during parsing). Therefore we can check whether the containing class is 
		 * internal/external.
		 */
		
		if (classDef.isInternal()) {
		
			DexClass parentClass = dex.getClass(classDef);
			DexStaticField field = parentClass.getStaticField(fieldDef);
			DexStaticField taintField = getTaintField(field);
			DexTaintRegister regFromTaint = insnSput.getRegFrom().getTaintRegister(); 
			
			return new DexMacro(
				codeGen.sput(regFromTaint, taintField.getFieldDef()),	
				insnSput);
		
		} else {
			
//			if (insnSput.getFieldDef().getFieldId().getType() instanceof DexPrimitiveType)
//				return new DexMacro(
//					codeGen.setTaintExternal(insnSput.getRegFrom().getTaintRegister(), insnSput.getRegObject()),
//					insnSput);
//			else
//				return new DexMacro(
//					codeGen.propagateTaintExternal(insnSput.getRegObject(), (DexSingleRegister) insnSput.getRegFrom()),
//					insnSput);

			// TODO
			throw new UnsupportedOperationException();
			
		}
			
	}
	
	private DexCodeElement instrument_StaticGet(DexInstruction_StaticGet insnSget) {
		StaticFieldDefinition fieldDef = insnSget.getFieldDef();
		ClassDefinition classDef = (ClassDefinition) fieldDef.getParentClass();
		
		if (classDef.isInternal()) {
		
			DexClass parentClass = dex.getClass(classDef);
			DexStaticField field = parentClass.getStaticField(fieldDef);
			DexStaticField taintField = getTaintField(field);
			DexTaintRegister regToTaint = insnSget.getRegTo().getTaintRegister(); 
			
			return new DexMacro(
				codeGen.sget(regToTaint, taintField.getFieldDef()),	
				insnSget);
		
		} else {

//			DexRegisterType resultType = insnSget.getFieldDef().getFieldId().getType();
//			
//			if (resultType instanceof DexPrimitiveType)
//				
//				return new DexMacro(
//					codeGen.getTaintExternal(insnSget.getRegTo().getTaintRegister(), insnSget.getRegObject()),
//					insnSget);
//			
//			else {
//				
//				DexSingleRegister regObjectTaintBackup;
//				DexSingleRegister regTo = (DexSingleRegister) insnSget.getRegTo();
//				DexSingleRegister regObject = insnSget.getRegObject();
//				
//				if (regObject.equals(regTo))
//					regObjectTaintBackup = codeGen.auxReg();
//				else
//					regObjectTaintBackup = regObject.getTaintRegister();
//				
//				return new DexMacro(
//					codeGen.move_obj(regObjectTaintBackup, regObject.getTaintRegister()),
//					insnSget,
//					codeGen.taintLookup(regTo, (DexReferenceType) resultType),
//					codeGen.propagateTaintExternal(regTo, regObjectTaintBackup));
//				
//			}
			
			// TODO
			throw new UnsupportedOperationException();

		}
	}

	private DexCodeElement instrument_MoveException(DexInstruction_MoveException insn) {
		RopType exceptionType = codeAnalysis.reverseLookup(insn).getDefinedRegisterSolver(insn.getRegTo()).getType();
		if (exceptionType.category != RopType.Category.Reference)
			throw new AssertionError("Cannot decide the type of the moved exception");

		return new DexMacro(
			insn,
			codeGen.taintLookup(insn.getRegTo(), exceptionType.type));
	}
	
	private void generateGetTaint(DexClass clazz) {
		DexTypeCache cache = hierarchy.getTypeCache();
		DexMethod implementationOf = dexAux.getMethod_InternalStructure_GetTaint();

		// generate bytecode
		
		DexSingleRegister regTotalTaint = new DexSingleAuxiliaryRegister(0);
		DexSingleRegister regFieldTaint = new DexSingleAuxiliaryRegister(1);
		DexSingleRegister regObject = new DexSingleAuxiliaryRegister(2);
		
		List<DexCodeElement> insns = new ArrayList<DexCodeElement>();

		if (clazz.getClassDef().getSuperclass().isInternal())
			insns.add(codeGen.call_super_int(clazz, implementationOf, regTotalTaint, Arrays.asList(regObject)));
		else
			insns.add(codeGen.setEmptyTaint(regTotalTaint));
		
		for (DexInstanceField ifield : clazz.getInstanceFields()) {
			if (!hasTaintField(ifield))
				continue;

			DexInstanceField tfield = getTaintField(ifield);
			
			insns.add(codeGen.iget(regFieldTaint, regObject, tfield.getFieldDef()));
			
			if (hierarchy.classifyType(ifield.getFieldDef().getFieldId().getType()) == TypeClassification.PRIMITIVE)
				insns.add(codeGen.combineTaint(regTotalTaint, regTotalTaint, regFieldTaint));
			else {
				DexLabel label = codeGen.label();
				insns.add(codeGen.ifZero(regFieldTaint, label));
				insns.add(codeGen.getTaint(regFieldTaint, regFieldTaint));
				insns.add(codeGen.combineTaint(regTotalTaint, regTotalTaint, regFieldTaint));
				insns.add(label);
			}
		}
		
		insns.add(codeGen.return_prim(regTotalTaint));
		
		InstructionList insnlist = new InstructionList(insns);
		
		// generate parameters
		
		Parameter paramThis = new Parameter(clazz.getClassDef().getType(), regObject);
		List<Parameter> params = Arrays.asList(paramThis);
		
		// generate DexCode
		
		DexCode methodBody = new DexCode(insnlist, params, cache.getCachedType_Integer(), false, hierarchy);
	
		// generate method and insert into the class
		
		implementMethod(clazz, implementationOf, methodBody);
	}

	private void generateSetTaint(DexClass clazz) {
		DexTypeCache cache = hierarchy.getTypeCache();
		DexMethod implementationOf = dexAux.getMethod_InternalStructure_SetTaint();
		
		// generate bytecode
		
		DexSingleRegister regFieldTaint = new DexSingleAuxiliaryRegister(0);
		DexSingleRegister regAddedTaint = new DexSingleAuxiliaryRegister(1);
		DexSingleRegister regObject = new DexSingleAuxiliaryRegister(2);
		
		List<DexCodeElement> insns = new ArrayList<DexCodeElement>();
		
		if (clazz.getClassDef().getSuperclass().isInternal())
			insns.add(codeGen.call_super_int(clazz, implementationOf, null, Arrays.asList(regObject, regAddedTaint)));
		
		for (DexInstanceField ifield : clazz.getInstanceFields()) {
			if (!hasTaintField(ifield))
				continue;
			
			DexInstanceField tfield = getTaintField(ifield);

			insns.add(codeGen.iget(regFieldTaint, regObject, tfield.getFieldDef()));

			if (hierarchy.classifyType(ifield.getFieldDef().getFieldId().getType()) == TypeClassification.PRIMITIVE) {
				insns.add(codeGen.combineTaint(regFieldTaint, regFieldTaint, regAddedTaint));
				insns.add(codeGen.iput(regFieldTaint, regObject, tfield.getFieldDef()));
			} else {
				DexLabel label = codeGen.label();
				insns.add(codeGen.ifZero(regFieldTaint, label));
				insns.add(codeGen.setTaint(regAddedTaint, regFieldTaint));
				insns.add(label);
			}
		}
		
		insns.add(codeGen.retrn());

		InstructionList insnlist = new InstructionList(insns);
		
		// generate parameters
		
		Parameter paramThis = new Parameter(clazz.getClassDef().getType(), regObject);
		Parameter paramAddedTaint = new Parameter(cache.getCachedType_Integer(), regAddedTaint);
		List<Parameter> params = Arrays.asList(paramThis, paramAddedTaint);
		
		// generate DexCode
		
		DexCode methodBody = new DexCode(insnlist, params, cache.getCachedType_Void(), false, hierarchy);
	
		// generate method and insert into the class
		
		implementMethod(clazz, implementationOf, methodBody);
	}
	
	private void implementMethod(DexClass clazz, DexMethod implementationOf, DexCode methodBody) {
		// generate method definition
		
		BaseClassDefinition classDef = clazz.getClassDef();
		DexMethodId methodId = implementationOf.getMethodDef().getMethodId();
		int accessFlags = DexUtils.assembleAccessFlags(AccessFlags.PUBLIC);
		MethodDefinition methodDef = new MethodDefinition(classDef, methodId, accessFlags);
		classDef.addDeclaredMethod(methodDef);
		
		// generate method
		
		DexMethod method = new DexMethod(clazz, methodDef, methodBody);
		
		// add it to the class
		
		clazz.replaceMethods(Utils.concat(clazz.getMethods(), method));
	}

	// UTILS
	
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
	
	private boolean hasTaintField(DexInstanceField field) {
		return taintInstanceFields.containsKey(field);
	}
	
	private DexInstanceField getTaintField(DexInstanceField field) {
		
		// Check if it has been already created
		
		DexInstanceField cachedTaintField = taintInstanceFields.get(field);
		if (cachedTaintField != null)
			return cachedTaintField;

		// It hasn't, so let's create a new one...
		
		final ClassDefinition classDef = (ClassDefinition) field.getParentClass().getClassDef();
		
		// Figure out a non-conflicting name for the new field
		
		// there is a test that tests this - need to change the names of methods if name generation changes!
		String newPrefix = "t_" + field.getFieldDef().getFieldId().getName();
		String newName = Utils.generateName(newPrefix, "", new NameAcceptor() {
			@Override
			public boolean accept(String name) {
				return classDef.getInstanceField(name) == null;
			}
		});
		
		// Generate the new taint field
		
		DexFieldId fieldId = DexFieldId.parseFieldId(newName, taintType(field.getFieldDef().getFieldId().getType()), typeCache);
		int fieldAccessFlags = DexUtils.assembleAccessFlags(field.getFieldDef().getAccessFlags());
		InstanceFieldDefinition fieldDef = new InstanceFieldDefinition(classDef, fieldId, fieldAccessFlags);
		classDef.addDeclaredInstanceField(fieldDef);
		
		DexClass parentClass = field.getParentClass();
		DexInstanceField taintField = new DexInstanceField(parentClass, fieldDef);
		parentClass.replaceInstanceFields(Utils.concat(parentClass.getInstanceFields(), taintField));
		
		// Cache it
		
		taintInstanceFields.put(field, taintField);
		
		// Return
		
		return taintField;
	}
	
	private DexStaticField getTaintField(DexStaticField field) {
		
		// Check if it has been already created
		
		DexStaticField cachedTaintField = taintStaticFields.get(field);
		if (cachedTaintField != null)
			return cachedTaintField;

		// It hasn't, so let's create a new one...
		
		final BaseClassDefinition classDef = field.getParentClass().getClassDef();
		
		// Figure out a non-conflicting name for the new field
		
		// there is a test that tests this - need to change the names of methods if name generation changes!
		String newPrefix = "t_" + field.getFieldDef().getFieldId().getName();
		String newName = Utils.generateName(newPrefix, "", new NameAcceptor() {
			@Override
			public boolean accept(String name) {
				return classDef.getStaticField(name) == null;
			}
		});
		
		// Generate the new taint field
		
		DexFieldId fieldId = DexFieldId.parseFieldId(newName, taintType(field.getFieldDef().getFieldId().getType()), typeCache);
		int fieldAccessFlags = DexUtils.assembleAccessFlags(field.getFieldDef().getAccessFlags());
		StaticFieldDefinition fieldDef = new StaticFieldDefinition(classDef, fieldId, fieldAccessFlags);
		classDef.addDeclaredStaticField(fieldDef);
		
		DexClass parentClass = field.getParentClass();
		DexStaticField taintField = new DexStaticField(parentClass, fieldDef, null);
		parentClass.replaceStaticFields(Utils.concat(parentClass.getStaticFields(), taintField));
		
		// Cache it
		
		taintStaticFields.put(field, taintField);
		
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
