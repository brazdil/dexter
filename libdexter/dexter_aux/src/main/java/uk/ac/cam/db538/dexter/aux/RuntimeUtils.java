package uk.ac.cam.db538.dexter.aux;

public final class RuntimeUtils {

	private RuntimeUtils() { }

	public static void die(String msg) {
		System.err.println(msg);
		System.exit(1);
	}
}
