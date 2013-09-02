package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public abstract class SimpleSink extends SourceSinkDefinition {

	public SimpleSink(MethodCall methodCall, LeakageAlert leakageAlert) {
		super(methodCall, leakageAlert);
	}

	@Override
	public DexCodeElement insertJustBefore(DexSingleRegister regCombinedTaint, CodeGenerator codeGen) {
		DexSingleRegister auxIsSourceTaint = codeGen.auxReg();
		DexLabel lFalse = codeGen.label();
		
		return new DexMacro(
				codeGen.isSourceTaint(auxIsSourceTaint, regCombinedTaint),
				codeGen.ifZero(auxIsSourceTaint, lFalse),
				leakageAlert.generate(regCombinedTaint, codeGen),
				lFalse);
	}
}
