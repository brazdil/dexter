package uk.ac.cam.db538.dexter.aux.struct;

import java.io.Serializable;

public abstract class TaintArray implements Taint, Serializable {

	public final int t_length;
	protected final Object array; 
	
	public TaintArray(Object object, int t_length) {
		this.array = object;
		this.t_length = t_length;
	}
	
	public boolean belongsTo(Object other) {
		return array == other;
	}
}
