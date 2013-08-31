package uk.ac.cam.db538.dexter.transform;

import java.util.Set;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexType;
import uk.ac.cam.db538.dexter.dex.type.DexVoid;

public class MethodCall extends DexCodeElement {

    private final DexInstruction_Invoke insnInvoke;
    private final DexInstruction_MoveResult insnResult;

    public MethodCall(DexInstruction_Invoke invoke, DexInstruction_MoveResult result) {
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

    public DexCodeElement expand() {
        if (movesResult())
            return new DexMacro(insnInvoke, insnResult);
        else
            return insnInvoke;
    }

    public MethodCall clone() {
        return new MethodCall(
                   new DexInstruction_Invoke(insnInvoke),
                   insnResult == null ? null : new DexInstruction_MoveResult(insnResult));
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
