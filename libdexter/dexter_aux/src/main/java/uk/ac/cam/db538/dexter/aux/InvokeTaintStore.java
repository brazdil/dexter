package uk.ac.cam.db538.dexter.aux;

import uk.ac.cam.db538.dexter.aux.struct.Taint;


public class InvokeTaintStore {

	public static ThreadLocalPrimitiveArguments ARGS_PRIM = new ThreadLocalPrimitiveArguments();
	public static ThreadLocalReferenceArguments ARGS_REF = new ThreadLocalReferenceArguments();
	public static ThreadLocalPrimitiveResult RES_PRIM = new ThreadLocalPrimitiveResult();
	public static ThreadLocalReferenceResult RES_REF = new ThreadLocalReferenceResult();
	
	private static ThreadLocalInternalCallFlag INTERNAL_CALL = new ThreadLocalInternalCallFlag(); 
	
	public static void setInternalCall() {
		INTERNAL_CALL.set(true);
	}
	
	public static boolean isInternalCall() {
		boolean flag = INTERNAL_CALL.get();
		if (flag)
			INTERNAL_CALL.set(false);
		return flag;
	}
	
	public static class ThreadLocalPrimitiveArguments extends ThreadLocal<int[]> {

		@Override
		protected int[] initialValue() {
			return new int[256];
		}

		@Override
		public void set(int[] value) {
			RuntimeUtils.die("Argument taint array cannot be modified");
		}
	}

	public static class ThreadLocalReferenceArguments extends ThreadLocal<Taint[]> {

		@Override
		protected Taint[] initialValue() {
			return new Taint[256];
		}
		
		@Override
		public void set(Taint[] value) {
			RuntimeUtils.die("Argument taint array cannot be modified");
		}
	}

	public static class ThreadLocalPrimitiveResult extends ThreadLocal<Integer> {

		@Override
		protected Integer initialValue() {
			return null;
		}
		
		@Override
		public Integer get() {
			Integer result = super.get();
			set(null);
			return result;
		}
	}

	public static class ThreadLocalReferenceResult extends ThreadLocal<Taint> {

		@Override
		protected Taint initialValue() {
			return null;
		}

		@Override
		public Taint get() {
			Taint result = super.get();
			set(null);
			return result;
		}
	}

	private static class ThreadLocalInternalCallFlag extends ThreadLocal<Boolean> {

		@Override
		protected Boolean initialValue() {
			return false;
		}
	}
}
