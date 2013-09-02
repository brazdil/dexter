package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public interface LeakageAlert {
	public DexCodeElement generate(DexSingleRegister regTaint, String leakType, CodeGenerator codeGen);
}
