package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.transform.MethodCall;

public class Sink_Log extends SimpleSink {

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
}
