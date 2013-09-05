package uk.ac.cam.db538.dexter.transform.taint;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.utils.Pair;

import com.rx201.dx.translator.DexCodeAnalyzer;
import com.rx201.dx.translator.RopType;

public class TemplateBuilder {

	private final DexCode code;
	private final DexCodeAnalyzer analyzedCode;
	private final CodeGenerator codeGen;
	private final RuntimeHierarchy hierarchy;
	private final DexTypeCache cache;
	
	public TemplateBuilder(DexCode code, DexCodeAnalyzer analyzedCode, CodeGenerator codeGen) {
		this.code = code;
		this.analyzedCode = analyzedCode;
		this.codeGen = codeGen;
		this.hierarchy = code.getHierarchy();
		this.cache = this.hierarchy.getTypeCache();
	}
	
	/**
	 * Generates instrumentation of given instruction. If it succeeds,
	 * the instrumentation will be executed. If it throws, taint of
	 * the referenced registers will be propagated into the exception.
	 * 
	 * @param insn       Original instruction
	 * @param tainting   Tainting instrumentation. 
	 *                   Must define the taint register of the register defined by the instruction. 
	 *                   Must never actually throw!
	 */
	public DexMacro create(DexInstruction insn, DexCodeElement tainting) {
		return create(insn, insn, tainting);
	}
	
	public DexMacro create(DexInstruction insn, DexCodeElement replacementInsn, DexCodeElement tainting) {
		return new DexMacro(
			generateThrowingPath(insn, replacementInsn),
			generateNonThrowingPath(insn, tainting));
	}

    private DexCodeElement generateThrowingPath(DexInstruction insn, DexCodeElement inside) {
    	if (!insn.canThrow())
    		return insn;
    	
    	Pair<? extends DexCodeElement, ? extends DexSingleRegister> taintCombination = combineReferencedTaint(insn);
    	return taintException(inside, taintCombination.getValA(), taintCombination.getValB());
    }
    
    public DexCodeElement taintException(DexCodeElement insideBlock, DexCodeElement afterMoveException, DexSingleRegister regTaint) {
    	DexTryStart block = codeGen.tryBlock(codeGen.catchAll());
    	DexLabel lAfter = codeGen.label();
    	
    	DexSingleRegister auxExObj = codeGen.auxReg();
    	DexSingleRegister auxExTaint = codeGen.auxReg();
    	
    	return new DexMacro(
    			block,
    			insideBlock,
    			block.getEndMarker(),
    			codeGen.jump(lAfter),
    			block.getCatchAllHandler(),
    			codeGen.move_ex(auxExObj),
    			afterMoveException,
    			codeGen.taintLookup(auxExTaint, auxExObj, regTaint, hierarchy.classifyType(cache.TYPE_Throwable)),
    			codeGen.thrw(auxExObj),
    			lAfter);
    }
    
    private DexCodeElement generateNonThrowingPath(DexInstruction insn, DexCodeElement tainting) {
    	if (!tainting.canThrow())
    		return tainting;
    	
    	DexTryStart block = codeGen.tryBlock(codeGen.catchAll());
    	DexLabel lAfter = codeGen.label();
    	
    	DexSingleRegister auxExObj = codeGen.auxReg();
    	
    	return new DexMacro(
    			block,
    			tainting,
    			block.getEndMarker(),
    			codeGen.jump(lAfter),
    			
    			/*
    			 * Should never happen. Only for the compiler's sake.
    			 */
    			block.getCatchAllHandler(),
    			codeGen.move_ex(auxExObj),
    			defineAllRegisters(insn),
    			
    			lAfter);
    }
    
    private Pair<? extends DexCodeElement, ? extends DexSingleRegister> combineReferencedTaint(DexInstruction insn) {
    	DexSingleRegister auxCombinedTaint = codeGen.auxReg();
    	DexSingleRegister auxObjTaint = codeGen.auxReg();

    	List<DexCodeElement> taintCombination = new ArrayList<DexCodeElement>();
    	taintCombination.add(codeGen.setEmptyTaint(auxCombinedTaint));
    	
    	for (DexRegister regRef : insn.lvaReferencedRegisters()) {
    		RopType type = analyzedCode.reverseLookup(insn).getUsedRegisterSolver(regRef).getType();
    		if (isPrimitive(type))
    			taintCombination.add(codeGen.combineTaint(auxCombinedTaint, auxCombinedTaint, regRef.getTaintRegister()));
    		else {
    			taintCombination.add(codeGen.taintClearVisited(type.type));
    			taintCombination.add(codeGen.getTaint(auxObjTaint, regRef.getTaintRegister()));
    			taintCombination.add(codeGen.combineTaint(auxCombinedTaint, auxCombinedTaint, auxObjTaint));
    		}
    	}

    	return Pair.create(new DexMacro(taintCombination), auxCombinedTaint);
    }
    
    private DexCodeElement defineAllRegisters(DexInstruction insn) {
    	List<DexCodeElement> taintAssignemnt = new ArrayList<DexCodeElement>();
    	
    	for (DexRegister regDef : insn.lvaDefinedRegisters()) {
    		RopType type = analyzedCode.reverseLookup(insn).getDefinedRegisterSolver(regDef).getType();
    		if (isPrimitive(type))
    			taintAssignemnt.add(codeGen.setEmptyTaint(regDef.getTaintRegister()));
    		else
    			taintAssignemnt.add(codeGen.setZero(regDef.getTaintRegister()));
    	}
    	
    	return new DexMacro(taintAssignemnt);
    }

    private boolean isPrimitive(RopType type) {
		switch(type.category) {
		case Boolean:
		case Byte:
		case Char:
		case DoubleHi:
		case DoubleLo:
		case Float:
		case Integer:
		case IntFloat:
		case LongHi:
		case LongLo:
		case One:
		case Zero:
		case Primitive:
		case Short:
		case Wide:
			return true;
			
		case Reference:
		case Null:
			return false;
			
		default:
			throw new AssertionError("Type of register is " + type.category.name());
		}
    }
}
