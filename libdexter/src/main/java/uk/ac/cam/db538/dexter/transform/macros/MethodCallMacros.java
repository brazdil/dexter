package uk.ac.cam.db538.dexter.transform.macros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;
import uk.ac.cam.db538.dexter.dex.AuxiliaryDex;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayPut;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_CheckCast;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_StaticGet;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_GetPut;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;

public final class MethodCallMacros {

	private MethodCallMacros() { }

	public static DexMacro setParamTaints(AuxiliaryDex dexAux, DexSingleAuxiliaryRegister auxReg1, DexSingleAuxiliaryRegister auxReg2, List<DexTaintRegister> taintRegs) {
		assert(!auxReg1.equals(auxReg2));
		
		val hierarchy = dexAux.getHierarchy();
		val cache = hierarchy.getTypeCache();
		return new DexMacro(
				
				// retrieve ThreadLocal<int[]> ARGS
				new DexInstruction_StaticGet(auxReg1, dexAux.getField_CallParamTaint().getFieldDef(), Opcode_GetPut.Object, hierarchy),
				
				// call ARGS.get(); automatically initializes the array
				new DexInstruction_Invoke(
						DexReferenceType.parse("Ljava/lang/ThreadLocal;", cache),
						DexMethodId.parseMethodId(
								"get",
								DexPrototype.parse(DexReferenceType.parse("Ljava/lang/Object;", cache), null, cache),
								cache),
						Arrays.asList(auxReg1),						
						Opcode_Invoke.Virtual,
						hierarchy),
				new DexInstruction_MoveResult(auxReg1, true, hierarchy),
				
				// cast to int[]
				new DexInstruction_CheckCast(auxReg1, DexReferenceType.parse("[I", cache), hierarchy),
				
				// store the taints inside the array
				storeIntsInArray(dexAux, auxReg1, auxReg2, taintRegs));
	}
	
	private static DexMacro storeIntsInArray(AuxiliaryDex dexAux, DexSingleRegister array, DexSingleRegister aux, List<DexTaintRegister> taints) {
		val hierarchy = dexAux.getHierarchy();
		val insns = new ArrayList<DexInstruction>(taints.size() * 2);
		
		for (int i = 0; i < taints.size(); i++) {
			insns.add(new DexInstruction_Const(aux, i, hierarchy));
			insns.add(new DexInstruction_ArrayPut(taints.get(i), array, aux, Opcode_GetPut.IntFloat, hierarchy));
		}
		
		return new DexMacro(insns);
	}
	
	public static DexMacro getResultTaint(AuxiliaryDex dexAux, DexSingleAuxiliaryRegister auxReg1, DexTaintRegister regTo) {
		val hierarchy = dexAux.getHierarchy();
		val cache = hierarchy.getTypeCache();
		val typeInteger = DexReferenceType.parse("Ljava/lang/Integer;", cache);
		
		return new DexMacro(
				
				// retrieve ThreadLocal<Integer> RES
				new DexInstruction_StaticGet(auxReg1, dexAux.getField_CallResultTaint().getFieldDef(), Opcode_GetPut.Object, hierarchy),
				
				// call RES.get()
				new DexInstruction_Invoke(
						DexReferenceType.parse("Ljava/lang/ThreadLocal;", cache),
						DexMethodId.parseMethodId(
								"get",
								DexPrototype.parse(DexReferenceType.parse("Ljava/lang/Object;", cache), null, cache),
								cache),
						Arrays.asList(auxReg1),						
						Opcode_Invoke.Virtual,
						hierarchy),
				new DexInstruction_MoveResult(auxReg1, true, hierarchy),
				
				// cast to Integer
				new DexInstruction_CheckCast(auxReg1, typeInteger, hierarchy),
				
				// call Integer.intValue()
				new DexInstruction_Invoke(
						typeInteger,
						DexMethodId.parseMethodId(
								"intValue",
								DexPrototype.parse(cache.getCachedType_Integer(), null, cache),
								cache),
						Arrays.asList(auxReg1),
						Opcode_Invoke.Virtual,
						hierarchy),
				new DexInstruction_MoveResult(regTo, hierarchy));
	}

	
	
}
