package uk.ac.cam.db538.dexter.dex.code.reg;

import lombok.Getter;

public class DexTaintRegister extends DexSingleRegister {

	@Getter private final DexStandardRegister originalRegister;

	// Only to be called by Dex{Single,Wide}OriginalRegister constructors
	DexTaintRegister(DexStandardRegister origReg) {
		this.originalRegister = origReg;
	}

	@Override
	String getAsmId() {
		return originalRegister.getAsmId();
	}

	@Override
	String getAsmPrefix() {
		return "t";
	}
}
