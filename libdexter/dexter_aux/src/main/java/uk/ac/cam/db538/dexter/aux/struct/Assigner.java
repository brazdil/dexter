package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.RuntimeUtils;
import uk.ac.cam.db538.dexter.aux.TaintConstants;

public final class Assigner {

	private Assigner() { }

	public static final TaintExternal newExternal(Object obj, int initialTaint) {
		if (TaintConstants.isImmutable(obj))
			return new TaintImmutable(initialTaint);
		else {
			TaintExternal tobj = new TaintExternal(initialTaint);
			Cache.insert(obj, tobj);
			return tobj;
		}
	}

	public static final TaintInternal newInternal(Object obj) {
		if (obj == null)
			RuntimeUtils.die("Cannot create internal taint for NULL");

		Taint t_super = Cache.get(obj);
		if (t_super == null) {
			System.err.println("Internal object is not initialized");
			System.exit(1);
		}
		
		if (!(obj instanceof InternalDataStructure)) {
			System.err.println("Given object is not internal");
			System.exit(1);
		}
		
		TaintInternal tobj = new TaintInternal((InternalDataStructure) obj, t_super);
		Cache.set(obj, tobj);
		return tobj;
	}

	public static final TaintInternal newInternal_NULL(int taint) {
		Taint t_super = newExternal(null, taint);
		return new TaintInternal(null, t_super);
	}
	
	public static final TaintArrayPrimitive newArrayPrimitive(Object obj, int length, int lengthTaint) {
		TaintArrayPrimitive tobj = new TaintArrayPrimitive(length, lengthTaint);
		if (obj != null)
			Cache.insert(obj, tobj);
		return tobj;
	}
	
	public static final TaintArrayReference newArrayReference(Object obj, int length, int lengthTaint) {
		TaintArrayReference tobj = new TaintArrayReference(length, lengthTaint);
		if (obj != null)
			Cache.insert(obj, tobj);
		return tobj;
	}

	public static final TaintExternal lookupExternal(Object obj, int taint) {
		System.out.println("lookupExternal");
		System.out.println(obj.getClass().getName());
		
		if (TaintConstants.isImmutable(obj)) {
			System.out.println("immutable");
			return new TaintImmutable(taint);
		}
		
		TaintExternal tobj = (TaintExternal) Cache.get(obj);
		if (tobj == null) {
			System.out.println("new external");
			tobj = new TaintExternal(taint);
			Cache.insert(obj, tobj);
		} else if (taint != TaintConstants.TAINT_EMPTY) {
			System.out.println("old + assigning");
			tobj.set(taint);
		}
		
		return tobj;
	}
	
	public static final TaintInternal lookupInternal(Object obj, int taint) {
		if (obj == null)
			RuntimeUtils.die("Cannot lookup internal taint of NULL");
		
		TaintInternal tobj = (TaintInternal) Cache.get(obj);
		if (tobj == null)
			RuntimeUtils.die("Internal object is not initialized");
		
		if (taint != TaintConstants.TAINT_EMPTY) {
			TaintInternal.clearVisited();
			tobj.set(taint);
		}
		return tobj;
	}

	public static final Taint lookupUndecidable(Object obj, int taint) {
		if (obj instanceof InternalDataStructure)
			return lookupInternal((InternalDataStructure) obj, taint);
		// TODO: arrays!
		else
			return lookupExternal(obj, taint);
	}
	
	public static final TaintArrayPrimitive lookupArrayPrimitive(Object obj) {
		TaintArrayPrimitive tobj = (TaintArrayPrimitive) Cache.get(obj);
		if (tobj == null)
			RuntimeUtils.die("Array of primitives is not initialized");
		return tobj;
	}
	
	public static final TaintArrayReference lookupArrayReference(Object obj) {
		TaintArrayReference tobj = (TaintArrayReference) Cache.get(obj);
		if (tobj == null)
			RuntimeUtils.die("Array of references is not initialized");
		return tobj;
	}
}
