package uk.ac.cam.db538.dexter.aux.struct;

public abstract class TaintArray implements Taint {

	public final int t_length;
	
	public TaintArray(int t_length) { 
		this.t_length = t_length;
	}
}
