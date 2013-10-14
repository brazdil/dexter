package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.aux.TaintConstants;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Source_GMaps_MyLocationOverlay extends SourceSinkDefinition {

	public Source_GMaps_MyLocationOverlay(MethodCall methodCall) {
		super(methodCall);
	}
	
	@Override
	protected boolean isApplicable() {
		return 
			isDirectCall() &&
			classIs("Lcom/google/android/maps/MyLocationOverlay;") &&
			methodIsCalled("<init>");
	}

	private DexSingleRegister auxTaint;
	
	@Override
	public DexCodeElement insertBefore(CodeGenerator codeGen) {
		auxTaint = codeGen.auxReg();
		return new DexMacro(
				codeGen.constant(auxTaint, TaintConstants.SOURCE_LOCATION),
				codeGen.setTaint(auxTaint, (DexSingleRegister) getParamRegister(0)));
	}
}
