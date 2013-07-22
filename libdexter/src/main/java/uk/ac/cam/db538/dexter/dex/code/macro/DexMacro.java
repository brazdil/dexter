package uk.ac.cam.db538.dexter.dex.code.macro;

import java.util.Arrays;

import lombok.Getter;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;

public class DexMacro extends DexCodeElement {

	@Getter private final InstructionList instructions;
	
	public DexMacro(InstructionList instructions) {
		this.instructions = instructions;
	}
	
	public DexMacro(DexCodeElement ... insns) {
		this(new InstructionList(Arrays.asList(insns)));
	}
}
