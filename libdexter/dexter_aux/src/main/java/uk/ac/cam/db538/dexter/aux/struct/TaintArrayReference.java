package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public final class TaintArrayReference extends TaintArray {

	public final Taint[] t_array;
	
	TaintArrayReference(Object[] array, int taint) {
		super(array, taint);
		
		int length = (array == null) ? 0 : array.length;
		this.t_array = new Taint[length];

		for (int i = 0; i < length; i++)
			if (array[i] == null)
				this.t_array[i] = new TaintImmutable(TaintConstants.EMPTY.value);
			else
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
	
	public void update(int missedTaint) {
		Object[] array = (Object[]) this.array;
		
		for (int i = 0; i < t_array.length; i++) {
			Object elem = array[i];
			Taint t_elem = t_array[i];
			
			if (!t_elem.belongsTo(elem))
				t_array[i] = Assigner.lookupUndecidable(elem, missedTaint);
		}
	}
}
