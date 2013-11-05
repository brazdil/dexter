package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public class TaintExternal implements Taint {

	private int taint;
	private Object object;
	
	TaintExternal() {
		this.object = null;
		this.taint = TaintConstants.EMPTY.value;
	}
	
	TaintExternal(int taint) {
		this.object = null;
		this.taint = taint;
	}
	
	public int get() { 
		return this.taint; 
	}
	
	public void set(int taint) {
		this.taint |= taint;
	}

	public int getExternal() { 
		return this.taint; 
	}
	
	public void setExternal(int taint) {
		this.taint |= taint;
	}
	
	void define(Object object, int taint) {
		assert(this.object == null);
		assert(object != null);
		
		this.object = object;
		this.taint |= taint;
	}
	
	public boolean belongsTo(Object other) {
		return object == other;
	}
}
