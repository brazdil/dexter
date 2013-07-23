package uk.ac.cam.db538.dexter.dex.code.macro;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.val;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;

public class DexMacro extends DexCodeElement {

	@Getter private final InstructionList instructions;
	
	public DexMacro(InstructionList instructions) {
		this.instructions = instructions;
	}
	
	public DexMacro(DexCodeElement ... insns) {
		this(new InstructionList(Arrays.asList(insns)));
	}

	public DexMacro(List<DexInstruction> insns) {
		this(new InstructionList(insns));
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
}
