package uk.ac.cam.db538.dexter.aux.struct;

import uk.ac.cam.db538.dexter.aux.RuntimeUtils;
import uk.ac.cam.db538.dexter.aux.TaintConstants;

public final class Assigner {

	private Assigner() { }

	public static final TaintExternal newExternal(Object obj, int initialTaint) {
		TaintExternal tobj = newExternal_Undefined(obj.getClass());
		defineExternal(obj, tobj, initialTaint);
		return tobj;
	}
	
	public static final TaintExternal newExternal_NULL(int initialTaint) {
		return new TaintImmutable(initialTaint);
	}

	public static final TaintExternal newExternal_Undefined(Class<?> objClass) {
		if (TaintConstants.isImmutableType(objClass))
			return new TaintImmutable();
		else 
			return new TaintExternal();
	}

	public static final TaintInternal newInternal_NULL(int taint) {
		Taint t_super = newExternal_NULL(taint);
		return new TaintInternal(null, t_super);
	}
	
	public static final TaintInternal newInternal_Undefined() {
		return new TaintInternal(null, null);
	}
	
	public static final void defineExternal(Object obj, TaintExternal tobj, int taint) {
		taint = TaintConstants.sinkTaint(obj, taint);
		tobj.define(taint);
		Cache.insert(obj, tobj);
		
		System.out.println("DEFe " + obj.getClass().getSimpleName() + " ~ " + System.identityHashCode(obj));
	}
	
	public static final void defineInternal(Object obj, TaintInternal tobj) {
		if (obj == null)
			RuntimeUtils.die("Cannot create internal taint for NULL");

		Taint t_super = Cache.get(obj);
		if (t_super == null)
			RuntimeUtils.die("Internal object is not initialized");
		
		if (!(obj instanceof InternalDataStructure))
			RuntimeUtils.die("Given object is not internal");
		
		tobj.define((InternalDataStructure) obj, t_super);
		Cache.set(obj, tobj);
		
		System.out.println("DEFi " + obj.getClass().getSimpleName() + " ~ " + System.identityHashCode(obj));
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
		taint = TaintConstants.sinkTaint(obj, taint);
		
		if (TaintConstants.isImmutable(obj))
			return new TaintImmutable(taint);
		
		TaintExternal tobj = (TaintExternal) Cache.get(obj);
		if (tobj == null) {
			tobj = new TaintExternal(taint);
			Cache.insert(obj, tobj);
		} else if (taint != TaintConstants.EMPTY.value)
			tobj.set(taint);
		
		return tobj;
	}
	
	public static final TaintInternal lookupInternal(Object obj, int taint) {
		if (obj == null)
			RuntimeUtils.die("Cannot lookup internal taint of NULL");
		
		taint = TaintConstants.sinkTaint(obj, taint);

		System.out.println("SEEKi " + obj.getClass().getSimpleName() + " ~ " + System.identityHashCode(obj));		
		
		TaintInternal tobj = (TaintInternal) Cache.get(obj);
		if (tobj == null)
			RuntimeUtils.die("Internal object is not initialized");
		
		if (taint != TaintConstants.EMPTY.value) {
			TaintInternal.clearVisited();
			tobj.set(taint);
		}
		return tobj;
	}

	public static final Taint lookupUndecidable(Object obj, int taint) {
		if (obj == null)
			return new TaintImmutable(taint);
		else if (obj instanceof InternalDataStructure)
			return lookupInternal((InternalDataStructure) obj, taint);
		else if (obj instanceof Object[])
			return lookupArrayReference(obj, taint);
		else if (obj.getClass().isArray())
			return lookupArrayPrimitive(obj, taint);
		else
			return lookupExternal(obj, taint);
	}
	
	public static final TaintArrayPrimitive lookupArrayPrimitive(Object obj, int taint) {
		if (obj == null)
			return new TaintArrayPrimitive(0, taint);
		
		TaintArrayPrimitive tobj = (TaintArrayPrimitive) Cache.get(obj);
		if (tobj == null) {
			int length;
			if (obj instanceof int[])
				length = ((int[]) obj).length;
			else if (obj instanceof boolean[])
				length = ((boolean[]) obj).length;
			else if (obj instanceof byte[])
				length = ((byte[]) obj).length;
			else if (obj instanceof char[])
				length = ((char[]) obj).length;
			else if (obj instanceof double[])
				length = ((double[]) obj).length;
			else if (obj instanceof float[])
				length = ((float[]) obj).length;
			else if (obj instanceof long[])
				length = ((long[]) obj).length;
			else if (obj instanceof short[])
				length = ((short[]) obj).length;
			else  {
				RuntimeUtils.die("Object is not of a primitive array type");
				/* will never get executed */ length = 0;
			}

			tobj = new TaintArrayPrimitive(length, taint, taint);
		} else
			tobj.set(taint);
		
		return tobj;
	}
	
	public static final TaintArrayReference lookupArrayReference(Object obj, int taint) {
		if (obj == null)
			return new TaintArrayReference(0, taint);
		
		TaintArrayReference tobj = (TaintArrayReference) Cache.get(obj);
		if (tobj == null)
			tobj = new TaintArrayReference((Object[]) obj, taint);
		else
			tobj.set(taint);
		
		return tobj;
	}
}
