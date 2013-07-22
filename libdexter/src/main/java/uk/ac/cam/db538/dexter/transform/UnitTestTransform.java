package uk.ac.cam.db538.dexter.transform;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;

public class UnitTestTransform extends DexterTransform {

	public UnitTestTransform() {
	}

	public UnitTestTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	@Override
	public DexMethod doFirst(DexMethod method) {
		if (isTaintCheckMethod(method))
			System.out.println("FOUND IT!!!");
		
		return super.doFirst(method);
	}
		
	private static final String TAINTCHECK_CLASS = "Lcom/dextertest/tests/TaintChecker;";
	private static final String TAINTCHECK_METHOD = "isTainted";
	private static final String TAINTCHECK_PROTOTYPE = "(I)Z";

	private boolean isTaintCheckMethod(DexMethod method) {
		return
				method.getParentClass().getClassDef().getType().getDescriptor().equals(TAINTCHECK_CLASS) &&
				method.getMethodDef().getMethodId().getName().equals(TAINTCHECK_METHOD) &&
				method.getMethodDef().getMethodId().getPrototype().getDescriptor().equals(TAINTCHECK_PROTOTYPE) &&
				method.getMethodDef().isStatic();
	}

	
}
