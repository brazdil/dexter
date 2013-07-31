package uk.ac.cam.db538.dexter.aux.struct;

/*
 * Every internal class must implement this interface.
 * Both its name and the names of the methods are 
 * to be automatically changed to avoid collisions.
 */
public interface InternalDataStructure {
	public int getTaint();
	public void setTaint(int taint);
}
