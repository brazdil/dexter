package uk.ac.cam.db538.dexter.analysis.cfg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.val;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.utils.Utils;

public class CfgBasicBlock extends CfgBlock {

    @Getter private final List<DexCodeElement> instructions;

    public CfgBasicBlock(List<DexCodeElement> instructions) {
        if (instructions == null || instructions.isEmpty())
            throw new UnsupportedOperationException("BasicBlock must contain at least one instruction");

        this.instructions = Utils.finalList(instructions);
    }

    public DexCodeElement getFirstInstruction() {
        return instructions.get(0);
    }

    public DexCodeElement getLastInstruction() {
        return instructions.get(instructions.size() - 1);
    }

    public Set<DexRegister> getAllDefinedRegisters() {
        val set = new HashSet<DexRegister>();
        for (val insn : instructions)
            set.addAll(insn.lvaDefinedRegisters());
        return set;
    }
}
