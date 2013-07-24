package uk.ac.cam.db538.dexter.transform.taint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.cam.db538.dexter.dex.AuxiliaryDex;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayPut;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_CheckCast;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_StaticGet;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_GetPut;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.type.DexArrayType;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public final class Macros {

	private final AuxiliaryDex dexAux;
	private final RuntimeHierarchy hierarchy;
	private final DexTypeCache cache;
	
	private final DexClassType typeObject;
	private final DexClassType typeInteger;
	private final DexClassType typeThreadLocal;
	private final DexArrayType typeIntArray;
	
	private final MethodDefinition method_ThreadLocal_Get;
	private final MethodDefinition method_ThreadLocal_Set;
	private final MethodDefinition method_Integer_intValue;
	private final MethodDefinition method_Integer_valueOf;
	
	public Macros(AuxiliaryDex dexAux) {
		this.dexAux = dexAux;

		this.hierarchy = dexAux.getHierarchy();
		this.cache = hierarchy.getTypeCache();
		
		this.typeObject = DexClassType.parse("Ljava/lang/Object;", cache);
		this.typeInteger = DexClassType.parse("Ljava/lang/Integer;", cache);
		this.typeThreadLocal = DexClassType.parse("Ljava/lang/ThreadLocal;", cache);
		this.typeIntArray = DexArrayType.parse("[I", cache);
	
		DexPrototype prototype_void_to_Object = DexPrototype.parse(typeObject, null, cache);
		DexPrototype prototype_Object_to_void = DexPrototype.parse(cache.getCachedType_Void(), Arrays.asList(typeObject), cache);
		DexPrototype prototype_void_to_int = DexPrototype.parse(cache.getCachedType_Integer(), null, cache);
		DexPrototype prototype_int_to_Integer = DexPrototype.parse(typeInteger, Arrays.asList(cache.getCachedType_Integer()), cache);
		
		DexMethodId methodId_get_void_to_Object = DexMethodId.parseMethodId("get", prototype_void_to_Object, cache);
		DexMethodId methodId_set_Object_to_void = DexMethodId.parseMethodId("set", prototype_Object_to_void, cache);
		DexMethodId methodId_intValue_void_to_int = DexMethodId.parseMethodId("intValue", prototype_void_to_int, cache);
		DexMethodId methodId_valueOf_int_to_Integer = DexMethodId.parseMethodId("valueOf", prototype_int_to_Integer, cache);
		
		this.method_ThreadLocal_Get = lookupMethod(typeThreadLocal, methodId_get_void_to_Object);
		this.method_ThreadLocal_Set = lookupMethod(typeThreadLocal, methodId_set_Object_to_void);
		this.method_Integer_intValue = lookupMethod(typeInteger, methodId_intValue_void_to_int);
		this.method_Integer_valueOf = lookupMethod(typeInteger, methodId_valueOf_int_to_Integer);
	}
	
	private MethodDefinition lookupMethod(DexReferenceType classType, DexMethodId methodId) {
		return hierarchy.getClassDefinition(classType).getMethod(methodId);
	}

	public DexMacro setParamTaints(DexSingleAuxiliaryRegister auxReg1, DexSingleAuxiliaryRegister auxReg2, List<DexTaintRegister> taintRegs) {
		assert(!auxReg1.equals(auxReg2));
		
		return new DexMacro(
			// retrieve ThreadLocal<int[]> ARGS => auxReg1
			new DexInstruction_StaticGet(auxReg1, dexAux.getField_CallParamTaint().getFieldDef(), Opcode_GetPut.Object, hierarchy),
			
			// call auxReg1.get(); automatically initializes the array
			new DexInstruction_Invoke(method_ThreadLocal_Get, Arrays.asList(auxReg1), hierarchy),
			new DexInstruction_MoveResult(auxReg1, true, hierarchy),
			
			// cast auxReg1 to int[]
			new DexInstruction_CheckCast(auxReg1, typeIntArray, hierarchy),
			
			// store the taints inside the auxReg1 array
			storeIntsInArray(auxReg1, auxReg2, taintRegs));
	}
	
	private DexMacro storeIntsInArray(DexSingleRegister array, DexSingleAuxiliaryRegister auxReg1, List<DexTaintRegister> taints) {
		List<DexInstruction> insns = new ArrayList<DexInstruction>(taints.size() * 2);
		
		for (int i = 0; i < taints.size(); i++) {
			insns.add(new DexInstruction_Const(auxReg1, i, hierarchy));
			insns.add(new DexInstruction_ArrayPut(taints.get(i), array, auxReg1, Opcode_GetPut.IntFloat, hierarchy));
		}
		
		return new DexMacro(insns);
	}
	
	public DexMacro getResultTaint(DexSingleAuxiliaryRegister auxReg1, DexTaintRegister regTo) {
		return new DexMacro(
			// retrieve ThreadLocal<Integer> RES => auxReg1
			new DexInstruction_StaticGet(auxReg1, dexAux.getField_CallResultTaint().getFieldDef(), Opcode_GetPut.Object, hierarchy),
			
			// virtual call auxReg1.get() => auxReg1
			new DexInstruction_Invoke(method_ThreadLocal_Get, Arrays.asList(auxReg1), hierarchy),
			new DexInstruction_MoveResult(auxReg1, true, hierarchy),
			
			// cast auxReg1 to Integer
			new DexInstruction_CheckCast(auxReg1, typeInteger, hierarchy),
			
			// virtual call auxReg1.intValue()
			new DexInstruction_Invoke(method_Integer_intValue, Arrays.asList(auxReg1), hierarchy),
			new DexInstruction_MoveResult(regTo, hierarchy));
	}

	public DexMacro setResultTaint(DexSingleAuxiliaryRegister auxReg1, DexSingleAuxiliaryRegister auxReg2, DexTaintRegister regFrom) {
		return new DexMacro(
			// static call Integer.valueOf(regFrom) => auxReg1
			new DexInstruction_Invoke(method_Integer_valueOf, Arrays.asList(regFrom), hierarchy),
			new DexInstruction_MoveResult(auxReg1, true, hierarchy),
			
			// retrieve ThreadLocal<Integer> RES => auxReg2
			new DexInstruction_StaticGet(auxReg2, dexAux.getField_CallResultTaint().getFieldDef(), Opcode_GetPut.Object, hierarchy),
			
			// virtual call auxReg2.set(auxReg1)
			new DexInstruction_Invoke(method_ThreadLocal_Set, Arrays.asList(auxReg2, auxReg1), hierarchy));
	}
}
