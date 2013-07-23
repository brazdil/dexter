package uk.ac.cam.db538.dexter.aux;


public class MethodCallHelper {

	public static ThreadLocalArguments ARGS = new ThreadLocalArguments();
	public static ThreadLocalResult RES = new ThreadLocalResult();
	
	public static class ThreadLocalArguments extends ThreadLocal<int[]> {

		@Override
		protected int[] initialValue() {
			return new int[256];
		}
		
	}

	public static class ThreadLocalResult extends ThreadLocal<Integer> {

		@Override
		protected Integer initialValue() {
			return 0;
		}
		
	}
}
