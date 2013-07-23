package uk.ac.cam.db538.dexter.dex.code.reg;


public abstract class DexStandardRegister extends DexRegister {

	abstract String getAsmId();
	abstract String getAsmPrefix(); 

	@Override
	public String toString() {
		return getAsmPrefix() + getAsmId();
	}
}
