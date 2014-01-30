package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import uk.ac.cam.db538.dexter.transform.MethodCall;

public class Error_Proxy extends SourceSinkDefinition {

	public Error_Proxy(MethodCall methodCall) {
		super(methodCall);
	}
	
	@Override
	protected boolean isApplicable() {
		return 
			isStaticCall() &&
			classIsChildOf("Ljava/lang/reflect/Proxy;") &&
			methodIsCalled("newProxyInstance") &&
			returnTypeIs("Ljava/lang/Object;") &&
			movesResult();
	}

	@Override
	public void doBefore() {
		throw new RuntimeException("Cannot instrument code that generates dynamic code with java.lang.reflect.Proxy");
	}
	
}
