package uk.ac.cam.db538.dexter.dex.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DexTypeCache implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Map<String, DexClassType> cachedTypes_Class;
	private final Map<String, DexArrayType> cachedTypes_Array;
	private final Map<DexPrototype, DexPrototype> cachedPrototypes;
	private final Map<DexMethodId, DexMethodId> cachedMethodIds;
	private final Map<DexFieldId, DexFieldId> cachedFieldIds;
	// private final Map<String, String> descriptorReplacements;
	private final DexVoid cachedType_Void = new DexVoid();
	private final DexBoolean cachedType_Boolean = new DexBoolean();
	private final DexByte cachedType_Byte = new DexByte();
	private final DexChar cachedType_Char = new DexChar();
	private final DexDouble cachedType_Double = new DexDouble();
	private final DexFloat cachedType_Float = new DexFloat();
	private final DexInteger cachedType_Integer = new DexInteger();
	private final DexLong cachedType_Long = new DexLong();
	private final DexShort cachedType_Short = new DexShort();
	// class renamer
	private ClassRenamer classRenamer;
	// From com.android.dx.rop.code.Exceptions
	public final DexClassType TYPE_Throwable;
	public final DexClassType TYPE_ArithmeticException;
	public final DexClassType TYPE_ArrayIndexOutOfBoundsException;
	public final DexClassType TYPE_ArrayStoreException;
	public final DexClassType TYPE_ClassCastException;
	public final DexClassType TYPE_Error;
	public final DexClassType TYPE_IllegalMonitorStateException;
	public final DexClassType TYPE_NegativeArraySizeException;
	public final DexClassType TYPE_NullPointerException;
	public final DexClassType[] LIST_Throwable;
	public final DexClassType[] LIST_Error;
	public final DexClassType[] LIST_Error_ArithmeticException;
	public final DexClassType[] LIST_Error_ClassCastException;
	public final DexClassType[] LIST_Error_NegativeArraySizeException;
	public final DexClassType[] LIST_Error_NullPointerException;
	public final DexClassType[] LIST_Error_Null_ArrayIndexOutOfBounds;
	public final DexClassType[] LIST_Error_Null_ArrayIndex_ArrayStore;
	public final DexClassType[] LIST_Error_Null_IllegalMonitorStateException;
	
	public DexTypeCache() {
		
		// reasonable initial values, determined experimentally
		cachedTypes_Class = new HashMap<String, DexClassType>(20000);
		cachedTypes_Array = new HashMap<String, DexArrayType>(1024);
		cachedPrototypes = new HashMap<DexPrototype, DexPrototype>(32384);
		cachedMethodIds = new HashMap<DexMethodId, DexMethodId>(100000);
		cachedFieldIds = new HashMap<DexFieldId, DexFieldId>();
		classRenamer = null;
		TYPE_ArithmeticException = DexClassType.parse("Ljava/lang/ArithmeticException;", this);
		TYPE_ArrayIndexOutOfBoundsException = DexClassType.parse("Ljava/lang/ArrayIndexOutOfBoundsException;", this);
		TYPE_ArrayStoreException = DexClassType.parse("Ljava/lang/ArrayStoreException;", this);
		TYPE_ClassCastException = DexClassType.parse("Ljava/lang/ClassCastException;", this);
		TYPE_IllegalMonitorStateException = DexClassType.parse("Ljava/lang/IllegalMonitorStateException;", this);
		TYPE_NegativeArraySizeException = DexClassType.parse("Ljava/lang/NegativeArraySizeException;", this);
		TYPE_NullPointerException = DexClassType.parse("Ljava/lang/NullPointerException;", this);
		TYPE_Error = DexClassType.parse("Ljava/lang/Error;", this);
		TYPE_Throwable = DexClassType.parse("Ljava/lang/Throwable;", this);
		LIST_Error_ArithmeticException = new DexClassType[]{TYPE_Error, TYPE_ArithmeticException};
		LIST_Error_ClassCastException = new DexClassType[]{TYPE_Error, TYPE_ClassCastException};
		LIST_Error_NegativeArraySizeException = new DexClassType[]{TYPE_Error, TYPE_NegativeArraySizeException};
		LIST_Error_NullPointerException = new DexClassType[]{TYPE_Error, TYPE_NullPointerException};
		LIST_Error_Null_ArrayIndexOutOfBounds = new DexClassType[]{TYPE_Error, TYPE_NullPointerException, TYPE_ArrayIndexOutOfBoundsException};
		LIST_Error_Null_ArrayIndex_ArrayStore = new DexClassType[]{TYPE_Error, TYPE_NullPointerException, TYPE_ArrayIndexOutOfBoundsException, TYPE_ArrayStoreException};
		LIST_Error_Null_IllegalMonitorStateException = new DexClassType[]{TYPE_Error, TYPE_NullPointerException, TYPE_IllegalMonitorStateException};
		LIST_Error = new DexClassType[]{TYPE_Error};
		LIST_Throwable = new DexClassType[]{TYPE_Throwable};
	}
	
	DexClassType getCachedType_Class(String desc) {
		return cachedTypes_Class.get(desc);
	}
	
	void putCachedType_Class(String desc, DexClassType type) {
		cachedTypes_Class.put(desc, type);
	}
	
	DexArrayType getCachedType_Array(String desc) {
		return cachedTypes_Array.get(desc);
	}
	
	void putCachedType_Array(String desc, DexArrayType type) {
		cachedTypes_Array.put(desc, type);
	}
	
	DexPrototype getCachedPrototype(DexPrototype proto) {
		DexPrototype cached = cachedPrototypes.get(proto);
		if (cached == null) {
			cachedPrototypes.put(proto, proto);
			return proto;
		} else return cached;
	}
	
	DexMethodId getCachedMethodId(DexMethodId mid) {
		DexMethodId cached = cachedMethodIds.get(mid);
		if (cached == null) {
			cachedMethodIds.put(mid, mid);
			return mid;
		} else return cached;
	}
	
	DexFieldId getCachedFieldId(DexFieldId fid) {
		DexFieldId cached = cachedFieldIds.get(fid);
		if (cached == null) {
			cachedFieldIds.put(fid, fid);
			return fid;
		} else return cached;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexVoid getCachedType_Void() {
		return this.cachedType_Void;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexBoolean getCachedType_Boolean() {
		return this.cachedType_Boolean;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexByte getCachedType_Byte() {
		return this.cachedType_Byte;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexChar getCachedType_Char() {
		return this.cachedType_Char;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexDouble getCachedType_Double() {
		return this.cachedType_Double;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexFloat getCachedType_Float() {
		return this.cachedType_Float;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexInteger getCachedType_Integer() {
		return this.cachedType_Integer;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexLong getCachedType_Long() {
		return this.cachedType_Long;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexShort getCachedType_Short() {
		return this.cachedType_Short;
	}
	
	@java.lang.SuppressWarnings("all")
	public ClassRenamer getClassRenamer() {
		return this.classRenamer;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setClassRenamer(final ClassRenamer classRenamer) {
		this.classRenamer = classRenamer;
	}
}