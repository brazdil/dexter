package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.aux.TaintConstants;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Source_Browser extends SourceSinkDefinition {

	public Source_Browser(MethodCall methodCall) {
		super(methodCall);
	}
	
	@Override
	protected boolean isApplicable() {
		return 
			isStaticCall() &&
			classIsChildOf("Landroid/provider/Browser;") &&
			paramIsOfType(0, "Landroid/content/ContentResolver;") &&
			!returnTypeIs("V") &&
			movesResult();
	}

	@Override
	public DexCodeElement insertAfter(CodeGenerator codeGen) {
		DexSingleRegister auxBrowserTaint = codeGen.auxReg();
		return new DexMacro(
				codeGen.constant(auxBrowserTaint, TaintConstants.TAINT_SOURCE_BROWSER),
				codeGen.setTaint(auxBrowserTaint, (DexSingleRegister) getResultRegister()));
	}
}