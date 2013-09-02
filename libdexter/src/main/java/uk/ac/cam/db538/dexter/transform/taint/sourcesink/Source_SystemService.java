package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Source_SystemService extends SourceSinkDefinition {

	public Source_SystemService(MethodCall methodCall) {
		super(methodCall);
	}
	
	@Override
	protected boolean isApplicable() {
		return 
			isVirtualCall() &&
			classIsChildOf("Landroid/content/Context;") &&
			methodIsCalled("getSystemService") &&
			paramIsOfType(1, "Ljava/lang/String;") &&
			returnTypeIs("Ljava/lang/Object;") &&
			movesResult();
	}

	private DexSingleRegister auxServiceTaint;
	
	@Override
	public DexCodeElement insertBefore(CodeGenerator codeGen) {
		auxServiceTaint = codeGen.auxReg();
		return codeGen.getServiceTaint(auxServiceTaint, (DexSingleRegister) getParamRegister(1));		
	}

	@Override
	public DexCodeElement insertAfter(CodeGenerator codeGen) {
		return codeGen.setTaint(auxServiceTaint, (DexSingleRegister) getResultRegister());
	}
}
