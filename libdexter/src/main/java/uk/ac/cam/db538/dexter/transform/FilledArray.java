package uk.ac.cam.db538.dexter.transform;

import java.util.Set;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_FilledNewArray;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;

public class FilledArray extends DexCodeElement {

    private final DexInstruction_FilledNewArray insnFilledArray;
    private final DexInstruction_MoveResult insnResult;

    public FilledArray(DexInstruction_FilledNewArray array, DexInstruction_MoveResult result) {
        this.insnFilledArray = array;
        this.insnResult = result;
        
        assert this.insnResult != null;
        assert this.insnResult.getRegTo() instanceof DexSingleRegister;
    }

    public DexInstruction_FilledNewArray getFilledArray() {
        return insnFilledArray;
    }

    public DexInstruction_MoveResult getResult() {
        return insnResult;
    }

    public DexCodeElement expand() {
        return new DexMacro(insnFilledArray, insnResult);
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
