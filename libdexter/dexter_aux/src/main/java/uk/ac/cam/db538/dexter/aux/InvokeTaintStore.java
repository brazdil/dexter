package uk.ac.cam.db538.dexter.aux;

import uk.ac.cam.db538.dexter.aux.struct.Taint;


public class InvokeTaintStore {

	public static ThreadLocalArguments ARGS = new ThreadLocalArguments();
	public static ThreadLocalPrimitiveResult RES_PRIM = new ThreadLocalPrimitiveResult();
	public static ThreadLocalReferenceResult RES_REF = new ThreadLocalReferenceResult();
	
	public static class ThreadLocalArguments extends ThreadLocal<int[]> {

		@Override
		protected int[] initialValue() {
			return new int[256];
		}
		
	}

	public static class ThreadLocalPrimitiveResult extends ThreadLocal<Integer> {

		@Override
		protected Integer initialValue() {
			return 0;
		}
		
	}

	public static class ThreadLocalReferenceResult extends ThreadLocal<Taint> {

		@Override
		protected Taint initialValue() {
			return null;
		}
		
	}
}
