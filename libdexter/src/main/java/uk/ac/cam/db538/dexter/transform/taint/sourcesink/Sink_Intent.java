package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.transform.MethodCall;

public class Sink_Intent extends SimpleSink {

	public Sink_Intent(MethodCall methodCall, LeakageAlert leakageAlert) {
		super(methodCall, leakageAlert);
	}

	@Override
	protected boolean isApplicable() {
		return 
			isVirtualCall() &&
			(
				classIsChildOf("Landroid/content/Context;") &&
				paramIsOfType(1, "Landroid/content/Intent;")
			) || (
				classIsChildOf("Landroid/content/IntentSender;") &&
				paramIsOfType(3, "Landroid/content/Intent;")
			);
	}
}
