package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Sink_UrlConnection extends SimpleSink {

	public Sink_UrlConnection(MethodCall methodCall, LeakageAlert leakageAlert) {
		super(methodCall, leakageAlert);
	}

	@Override
	protected boolean isApplicable() {
		return
			isVirtualCall() &&
			classIsChildOf("Ljava/net/URLConnection;") &&
			methodIsCalled("connect");
	}

	@Override
	protected DexCodeElement genTaintDetails(DexSingleRegister regDetails, CodeGenerator codeGen) {
		DexRegister regThis = methodCall.getInvoke().getArgumentRegisters().get(0);
		assert (regThis instanceof DexSingleRegister); 
		return codeGen.getUrlString(regDetails, (DexSingleRegister) regThis);
	}
	
}
