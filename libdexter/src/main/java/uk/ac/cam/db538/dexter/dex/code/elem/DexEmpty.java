package uk.ac.cam.db538.dexter.dex.code.elem;

public class DexEmpty extends DexCodeElement {

	private int origLine;
	
	public DexEmpty() {
		origLine = -1;
	}
	
	public DexEmpty(int origLine) {
		this.origLine = origLine;
	}

	@Override
    public String toString() {
    	if (origLine < 0)
    		return "";
    	else
    		return "    # " + origLine;
    }
}
