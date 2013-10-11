package uk.ac.cam.db538.dexter.aux;

import android.content.pm.Signature;

public class FakeSignature {

	public static final String PACKAGE_NAME;
	public static final Signature[] SIGNATURES;
	
	private FakeSignature() { }
	
	static {
		PACKAGE_NAME = null;
		SIGNATURES = null;
	}
}
