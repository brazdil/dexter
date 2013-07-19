package uk.ac.cam.db538.dexter.dex.code.elem;

import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class DexCatch extends DexCodeElement {
	private final int id;
	private final DexClassType exceptionType;
	
	public DexCatch(int id, DexClassType exceptionType, RuntimeHierarchy hierarchy) {
		
		this.id = id;
		this.exceptionType = exceptionType;
		// check that it is a Throwable class
		final uk.ac.cam.db538.dexter.dex.type.DexClassType throwableType = DexClassType.parse("Ljava/lang/Throwable;", hierarchy.getTypeCache());
		final uk.ac.cam.db538.dexter.hierarchy.ClassDefinition throwableDef = hierarchy.getClassDefinition(throwableType);
		final uk.ac.cam.db538.dexter.hierarchy.ClassDefinition classDef = hierarchy.getClassDefinition(this.exceptionType);
		if (!classDef.isChildOf(throwableDef)) throw new IllegalArgumentException("Given class does not extend Throwable");
	}
	
	@Override
	public String toString() {
		return "CATCH" + Integer.toString(id);
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
	public DexClassType getExceptionType() {
		return this.exceptionType;
	}
}