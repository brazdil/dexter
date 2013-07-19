package uk.ac.cam.db538.dexter.dex.code.elem;

public class DexCatchAll extends DexCodeElement {
	private final int id;
	
	public DexCatchAll(int id) {
		
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "CATCHALL" + Integer.toString(id);
	}
	
	@Override
	public boolean cfgStartsBasicBlock() {
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public int getId() {
		return this.id;
	}
}