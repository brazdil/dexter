package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Sink_Log extends SourceSinkDefinition {

	public Sink_Log(MethodCall methodCall, LeakageAlert leakageAlert) {
		super(methodCall, leakageAlert);
	}

	@Override
	protected boolean isApplicable() {
		return 
			isStaticCall() &&
			classIsChildOf("Landroid/util/Log;") &&
			(
				(
					(
						methodIsCalled("d") || 
						methodIsCalled("e") || 
						methodIsCalled("i") || 
						methodIsCalled("v") || 
						methodIsCalled("w") || 
						methodIsCalled("wtf")
					) &&
					paramIsOfType(0, "Ljava/lang/String;") &&
					paramIsOfType(1, "Ljava/lang/String;")
				) || (
					methodIsCalled("println") &&
					paramIsOfType(0, "I") &&
					paramIsOfType(1, "Ljava/lang/String;") &&
					paramIsOfType(2, "Ljava/lang/String;")
				)
			) &&
			returnTypeIs("I");
	}

	@Override
	public DexCodeElement insertJustBefore(DexSingleRegister regCombinedTaint, CodeGenerator codeGen) {
		DexSingleRegister auxIsSourceTaint = codeGen.auxReg();
		DexLabel lFalse = codeGen.label();
		
		return new DexMacro(
				codeGen.isSourceTaint(auxIsSourceTaint, regCombinedTaint),
				codeGen.ifZero(auxIsSourceTaint, lFalse),
				leakageAlert.generate(codeGen),
				lFalse);
	}
}
