package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Source_Query extends SourceSinkDefinition {

	public Source_Query(MethodCall methodCall) {
		super(methodCall);
	}
	
	@Override
	protected boolean isApplicable() {
		return 
			isVirtualCall() &&
			classIsChildOf("Landroid/content/ContentResolver;") &&
			methodIsCalled("query") &&
			paramIsOfType(1, "Landroid/net/Uri;") &&
			returnTypeIs("Landroid/database/Cursor;") &&
			movesResult();
	}

	private DexSingleRegister auxUriTaint;
	
	@Override
	public DexCodeElement insertBefore(CodeGenerator codeGen) {
		auxUriTaint = codeGen.auxReg();
		return codeGen.getUriTaint(auxUriTaint, (DexSingleRegister) getParamRegister(1));		
	}

	@Override
	public DexCodeElement insertAfter(CodeGenerator codeGen) {
		return codeGen.setTaint(auxUriTaint, (DexSingleRegister) getResultRegister());
	}
}
