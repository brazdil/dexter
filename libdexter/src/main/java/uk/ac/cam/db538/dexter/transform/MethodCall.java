package uk.ac.cam.db538.dexter.transform;

import java.util.Set;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryEnd;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterType;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexType;
import uk.ac.cam.db538.dexter.dex.type.DexVoid;

public class MethodCall extends DexCodeElement {

    private final DexInstruction_Invoke insnInvoke;
    private final DexInstruction_MoveResult insnResult;
    private final DexTryStart ownTryBlock;

    public MethodCall(DexInstruction_Invoke invoke, DexInstruction_MoveResult result) {
    	this(invoke, result, null);
    }
    
    public MethodCall(DexInstruction_Invoke invoke, DexInstruction_MoveResult result, DexTryStart ownTryBlock) {
        this.insnInvoke = invoke;
        
        DexType returnType = insnInvoke.getMethodId().getPrototype().getReturnType();
        if (result != null) {
        	assert (!(returnType instanceof DexVoid));
        	
        	switch (result.getType()) {
        	case REFERENCE:
        		if (!(returnType instanceof DexReferenceType)) {
        			assert(returnType instanceof DexPrimitiveType);
        			assert (((DexPrimitiveType) returnType).getTypeWidth() == RegisterWidth.SINGLE);
        			
        			result = new DexInstruction_MoveResult((DexSingleRegister) result.getRegTo(), false, result.getHierarchy());
        		}
        		break;
        		
        	case SINGLE_PRIMITIVE:
        		if (!(returnType instanceof DexPrimitiveType)) {
        			assert(returnType instanceof DexReferenceType);
        			
        			result = new DexInstruction_MoveResult((DexSingleRegister) result.getRegTo(), true, result.getHierarchy());
        		}
        		break;
        		
        	case WIDE_PRIMITIVE:
        		assert (returnType instanceof DexPrimitiveType);
        		assert (((DexPrimitiveType) returnType).getTypeWidth() == RegisterWidth.WIDE);
        		break;
        	}
        }
        
        this.insnResult = result;
        this.ownTryBlock = ownTryBlock;
        
        assert !((this.insnResult == null) && (this.ownTryBlock != null)); // cannot have a try block without moving result
    }

    public boolean movesResult() {
        return insnResult != null;
    }

    public DexInstruction_Invoke getInvoke() {
        return insnInvoke;
    }

    public DexInstruction_MoveResult getResult() {
        return insnResult;
    }
    
    public DexCodeElement expand_ReplaceInternals(DexCodeElement internals) {
    	if (ownTryBlock == null)
    		return internals;
    	else
    		return new DexMacro(ownTryBlock, internals, ownTryBlock.getEndMarker());
    }
    
    public DexCodeElement expand_JustInternals() {
        if (movesResult())
    		return new DexMacro(insnInvoke, insnResult);
        else
            return insnInvoke;
    }
    
    public MethodCall replaceResultRegister(DexSingleRegister reg) {
    	assert movesResult();
    	assert insnResult.getRegTo() instanceof DexSingleRegister;
    	
    	return new MethodCall(
    			insnInvoke,
    			new DexInstruction_MoveResult(reg, insnResult.getType() == RegisterType.REFERENCE, insnResult.getHierarchy()));
    }
    
    public DexCodeElement expand() {
    	return expand_ReplaceInternals(expand_JustInternals());
    }

    public MethodCall clone() {
    	DexTryStart newTryBlock;
    	if (ownTryBlock == null)
    		newTryBlock = null;
    	else {
    		DexTryEnd newEnd = new DexTryEnd(ownTryBlock.getEndMarker().getId());
    		newTryBlock = new DexTryStart(ownTryBlock, newEnd);
    	}
    	
        return new MethodCall(
                   new DexInstruction_Invoke(insnInvoke),
                   insnResult == null ? null : new DexInstruction_MoveResult(insnResult),
                   newTryBlock);
    }

	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		throw new UnsupportedOperationException();
	}
}
