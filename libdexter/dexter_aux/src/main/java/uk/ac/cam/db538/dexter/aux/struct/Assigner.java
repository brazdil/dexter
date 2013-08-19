package uk.ac.cam.db538.dexter.aux.struct;

public final class Assigner {

	private Assigner() { }

	public static final TaintExternal newExternal(Object obj, int initialTaint) {
		TaintExternal tobj = new TaintExternal(initialTaint);
		if (obj != null)
			Cache.insert(obj, tobj);
		return tobj;
	}

	public static final TaintInternal newInternal(Object obj) {
		if (obj == null)
			die("Cannot create internal taint for NULL");

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

	public static final TaintExternal lookupExternal(Object obj) {
		if (obj == null)
			die("Cannot lookup taint of NULL");
		
		TaintExternal tobj = (TaintExternal) Cache.get(obj);
		if (tobj == null) {
			tobj = new TaintExternal();
			Cache.insert(obj, tobj);
		}
		return tobj;
	}
	
	public static final TaintInternal lookupInternal(Object obj) {
		if (obj == null)
			die("Cannot lookup taint of NULL");
		
		TaintInternal tobj = (TaintInternal) Cache.get(obj);
		if (tobj == null)
			die("Internal object is not initialized");
		return tobj;
	}

	public static final Taint lookupUndecidable(Object obj) {
		if (obj instanceof InternalDataStructure)
			return lookupInternal((InternalDataStructure) obj);
		else
			return lookupExternal(obj);
	}
	
	public static final TaintArrayPrimitive lookupArrayPrimitive(Object obj) {
		TaintArrayPrimitive tobj = (TaintArrayPrimitive) Cache.get(obj);
		if (tobj == null)
			die("Array of primitives is not initialized");
		return tobj;
	}
	
	public static final TaintArrayReference lookupArrayReference(Object obj) {
		TaintArrayReference tobj = (TaintArrayReference) Cache.get(obj);
		if (tobj == null)
			die("Array of references is not initialized");
		return tobj;
	}
	
	private static void die(String msg) {
		System.err.println(msg);
		System.exit(1);
	}

//	private static final boolean isImmutable(Object obj) {
//      LIST OF IMMUTABLES IN TaintConstants CLASS
//		return
//			obj instanceof String ||
//			obj instanceof Integer ||
//			obj instanceof Boolean ||
//			obj instanceof Byte ||
//			obj instanceof Character ||
//			obj instanceof Double ||
//			obj instanceof Float ||
//			obj instanceof Long ||
//			obj instanceof Short ||
//			obj instanceof Void ||
//			obj instanceof BigDecimal ||
//			obj instanceof BigInteger;
//	}
}
