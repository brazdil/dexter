package uk.ac.cam.db538.dexter.aux.struct;

public final class TaintArrayReference extends TaintArray {

	public final Taint[] t_array;
	
	public TaintArrayReference(int length, int t_length) {
		super(t_length);
		this.t_array = new Taint[length];
	}

	@Override
	public int get() {
		// combine the taint of all elements and length
		int combinedTaint = t_length;
		for (int i = 0; i < t_array.length; i++)
			combinedTaint |= t_array[i].get();
		return combinedTaint;
	}

	@Override
	public void set(int taint) {
		// assign argument to all elements, not length (not modifiable)
		for (int i = 0; i < t_array.length; i++)
			t_array[i].set(taint);
	}
}
