package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public class TaintExternal implements Taint {

	private int taint;
	
	public TaintExternal() {
		this.taint = TaintConstants.TAINT_EMPTY;
	}
	
	public TaintExternal(int taint) {
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
	
	void define(int taint) {
		this.taint |= taint;
	}
}
