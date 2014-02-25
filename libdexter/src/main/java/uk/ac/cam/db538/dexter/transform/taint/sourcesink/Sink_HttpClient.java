package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public class Sink_HttpClient extends SimpleSink {

	public Sink_HttpClient(MethodCall methodCall, LeakageAlert leakageAlert) {
		super(methodCall, leakageAlert);
	}

	@Override
	protected boolean isApplicable() {
		return 
			isVirtualCall() &&
			classImplements("Lorg/apache/http/client/HttpClient;") &&
			methodIsCalled("execute");
	}

	@Override
	protected DexCodeElement genTaintDetails(DexSingleRegister regDetails, CodeGenerator codeGen) {
		for (int i = 0; i < numberOfParams(); i++) {
			if (paramIsOfType(i, "Lorg/apache/http/client/methods/HttpUriRequest;")) {
				DexRegister regRequest = methodCall.getInvoke().getArgumentRegisters().get(i);
				assert (regRequest instanceof DexSingleRegister);
				return codeGen.getRequestUriString(regDetails, (DexSingleRegister) regRequest);
			}
		}
		
		return super.genTaintDetails(regDetails, codeGen);
	}
	
}
