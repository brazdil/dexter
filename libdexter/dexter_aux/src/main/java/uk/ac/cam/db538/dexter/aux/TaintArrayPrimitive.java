package uk.ac.cam.db538.dexter.aux;

public final class TaintArrayPrimitive extends TaintArray {

	public final int[] t_array;
	
	public TaintArrayPrimitive(int length, int t_length) {
		super(t_length);
		this.t_array = new int[length];
	}

	@Override
	public int get() {
		// combine the taint of all elements and length
		int combinedTaint = t_length;
		for (int i = 0; i < t_array.length; i++)
			combinedTaint |= t_array[i];
		return combinedTaint;
	}

	@Override
	public void set(int taint) {
		// assign argument to all elements, not length (not modifiable)
		for (int i = 0; i < t_array.length; i++)
			t_array[i] |= taint;
	}
}
