package uk.ac.cam.db538.dexter.dex.code.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.val;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.utils.Utils;

public class DexMacro extends DexCodeElement {

    @Getter private final List<DexCodeElement> instructions;

    public DexMacro(DexCodeElement ... insns) {
        this(Arrays.asList(insns));
    }

    public DexMacro(List<? extends DexCodeElement> insns) {
        this.instructions = Utils.finalList(expandMacros(insns));
    }

    @Override
    public String toString() {
        val str = new StringBuilder();
        for (val insn : instructions) {
            str.append(insn.toString());
            str.append("\n");
        }
        return str.toString();
    }

    private static DexMacro emptyMacro = null;

    public static DexMacro empty() {
        if (emptyMacro == null)
            emptyMacro = new DexMacro();
        return emptyMacro;
    }

    public static List<? extends DexCodeElement> expandMacros(List<? extends DexCodeElement> insns) {
        if (!hasMacros(insns))
            return insns;

        val expandedInsns = new ArrayList<DexCodeElement>();
        for (val insn : insns) {
            if (insn instanceof DexMacro) {
            	DexMacro macro = (DexMacro) insn;
            	assert !hasMacros(macro.instructions);
                expandedInsns.addAll(macro.instructions);
            } else
                expandedInsns.add(insn);
        }
        return expandedInsns;
    }

    private static boolean hasMacros(List<? extends DexCodeElement> insns) {
        for (val insn : insns)
            if (insn instanceof DexMacro)
                return true;
        return false;
    }

    /*
     * This is far from being precise. No fancy path analysis, simply OR canThrow of all subinstructions.
     */
	@Override
	public boolean canThrow() {
		for (DexCodeElement insn : instructions)
			if (insn.canThrow())
				return true;
		return false;
	}
}
