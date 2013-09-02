package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public final class TaintArrayReference extends TaintArray {

	public final Taint[] t_array;
	
	TaintArrayReference(int length, int t_length) {
		super(t_length);
		this.t_array = new Taint[length];
		
		for (int i = 0; i < length; i++)
			this.t_array[i] = new TaintImmutable(TaintConstants.EMPTY.value);
	}

	TaintArrayReference(Object[] array, int taint) {
		super(taint);
		this.t_array = new Taint[array.length];

		for (int i = 0; i < array.length; i++)
			this.t_array[i] = Assigner.lookupUndecidable(array[i], taint);
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
	public int getExternal() {
		// combine the taint of all elements and length
		int combinedTaint = t_length;
		for (int i = 0; i < t_array.length; i++)
			combinedTaint |= t_array[i].getExternal();
		return combinedTaint;
	}

	@Override
	public void set(int taint) {
		// assign argument to all elements, not length (not modifiable)
		for (int i = 0; i < t_array.length; i++)
			t_array[i].set(taint);
	}

	@Override
	public void setExternal(int taint) {
		// assign argument to all elements, not length (not modifiable)
		for (int i = 0; i < t_array.length; i++)
			t_array[i].setExternal(taint);
	}
}
