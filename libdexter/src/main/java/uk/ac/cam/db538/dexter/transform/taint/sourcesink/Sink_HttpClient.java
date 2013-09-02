package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.transform.MethodCall;

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
}
