package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public final class TaintArrayPrimitive extends TaintArray {

	public final int[] t_array;

	TaintArrayPrimitive(int length, int t_length) {
		this(length, t_length, TaintConstants.TAINT_EMPTY);
	}

	TaintArrayPrimitive(int length, int t_length, int t_elem) {
		super(t_length);
		this.t_array = new int[length];

		for (int i = 0; i < length; i++)
			this.t_array[i] = t_elem;
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
	public int getExternal() {
		return get();
	}

	@Override
	public void set(int taint) {
		// assign argument to all elements, not length (not modifiable)
		for (int i = 0; i < t_array.length; i++)
			t_array[i] |= taint;
	}

	@Override
	public void setExternal(int taint) {
		set(taint);
	}
}
