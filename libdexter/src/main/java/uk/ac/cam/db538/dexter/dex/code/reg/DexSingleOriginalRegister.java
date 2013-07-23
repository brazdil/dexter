package uk.ac.cam.db538.dexter.dex.code.reg;

import lombok.Getter;

public class DexSingleOriginalRegister extends DexSingleRegister {

	@Getter private final int id;
	private final DexTaintRegister taintRegister;
	
	public DexSingleOriginalRegister(int id) {
		this.id = id;
		this.taintRegister = new DexTaintRegister(this);
	}

	@Override
	String getAsmId() {
		return Integer.toString(id);
	}

	@Override
	String getAsmPrefix() {
		return "v";
	}
	
	@Override
	public DexTaintRegister getTaintRegister() {
		return this.taintRegister;
	}
}
