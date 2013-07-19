package uk.ac.cam.db538.dexter.dex.code.elem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DexTryStart extends DexCodeElement {
	private final int id;
	private final DexTryEnd endMarker;
	private final List<DexCatch> catchHandlers;
	private final DexCatchAll catchAllHandler;
	
	public DexTryStart(int id, DexTryEnd endMarker, DexCatchAll catchAllHandler, List<DexCatch> catchHandlers) {
		
		this.id = id;
		this.endMarker = endMarker;
		this.catchAllHandler = catchAllHandler;
		if (catchHandlers == null) this.catchHandlers = Collections.emptyList(); else this.catchHandlers = Collections.unmodifiableList(new ArrayList<DexCatch>(catchHandlers));
	}
	
	@Override
	public String toString() {
		return "TRYSTART" + Integer.toString(this.id);
	}
	
	@Override
	public boolean cfgStartsBasicBlock() {
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public int getId() {
		return this.id;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexTryEnd getEndMarker() {
		return this.endMarker;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<DexCatch> getCatchHandlers() {
		return this.catchHandlers;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexCatchAll getCatchAllHandler() {
		return this.catchAllHandler;
	}
}