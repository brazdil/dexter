package uk.ac.cam.db538.dexter.aux.struct;

/*
 * This is mainly a common parent for all taint-storing structures.
 * The two methods are meant to collect the taint of all referenced
 * data and combine it. However, they are only used with external 
 * method calls. Instructions manipulating data will access internal
 * fields of their taint structure directly.
 */
public interface Taint {
	public int get();
	public void set(int taint);
}
