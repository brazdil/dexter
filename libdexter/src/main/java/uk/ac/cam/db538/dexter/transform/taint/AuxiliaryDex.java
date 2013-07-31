package uk.ac.cam.db538.dexter.transform.taint;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.aux.MethodCallHelper;
import uk.ac.cam.db538.dexter.aux.SafeHashMap;
import uk.ac.cam.db538.dexter.aux.TaintConstants;
import uk.ac.cam.db538.dexter.aux.anno.InternalClass;
import uk.ac.cam.db538.dexter.aux.anno.InternalMethod;
import uk.ac.cam.db538.dexter.aux.struct.Taint;
import uk.ac.cam.db538.dexter.aux.struct.TaintArray;
import uk.ac.cam.db538.dexter.aux.struct.TaintArrayPrimitive;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class AuxiliaryDex extends Dex {

//	@Getter private final DexMethod method_TaintGet;
//	@Getter private final DexMethod method_TaintSet;
//
//	@Getter private final DexMethod method_QueryTaint;
//	@Getter private final DexMethod method_ServiceTaint;
	
	@Getter private final DexStaticField field_CallParamTaint;
	@Getter private final DexStaticField field_CallResultTaint;
	
	@Getter private final InterfaceDefinition anno_InternalClass;
	@Getter private final InterfaceDefinition anno_InternalMethod;
	
	@Getter private final DexClass type_Taint;
	@Getter private final DexMethod method_Taint_Get;
	@Getter private final DexMethod method_Taint_Set;
	
	@Getter private final DexClass type_TaintArray;
	@Getter private final DexInstanceField field_TaintArray_TLength;
	
	@Getter private final DexClass type_TaintArrayPrimitive;
	@Getter private final DexMethod method_TaintArrayPrimitive_Constructor;
	@Getter private final DexInstanceField field_TaintArrayPrimitive_TArray;
	
	public AuxiliaryDex(DexFile dexAux, RuntimeHierarchy hierarchy, ClassRenamer renamer) {
		super(dexAux, hierarchy, null, renamer);
		
//		// ObjectTaintStorage class
//		val clsObjTaint = getDexClass(hierarchy, renamer, CLASS_OBJTAINT);
//
//		this.method_TaintGet = findStaticMethodByName(clsObjTaint, "get");
//		this.method_TaintSet = findStaticMethodByName(clsObjTaint, "set");
//
//		// TaintConstants class
//		val clsTaintConsts = getDexClass(hierarchy, renamer, CLASS_TAINTCONSTANTS);
//
//		this.method_QueryTaint = findStaticMethodByName(clsTaintConsts, "queryTaint");
//		this.method_ServiceTaint = findStaticMethodByName(clsTaintConsts, "serviceTaint");
		
		// MethodCallHelper class
		val clsMethodCallHelper = getDexClass(hierarchy, renamer, CLASS_METHODCALLHELPER);
		
		this.field_CallParamTaint = findStaticFieldByName(clsMethodCallHelper, "ARGS");
		this.field_CallResultTaint = findStaticFieldByName(clsMethodCallHelper, "RES");
		
		// Annotations
		this.anno_InternalClass = getIfaceDef(hierarchy, renamer, CLASS_INTERNALCLASS);
		this.anno_InternalMethod = getIfaceDef(hierarchy, renamer, CLASS_INTERNALMETHOD);
		
		// Taint types
		this.type_Taint = getDexClass(hierarchy, renamer, CLASS_TAINT);
		this.method_Taint_Get = findInstanceMethodByName(type_Taint, "get");
		this.method_Taint_Set = findInstanceMethodByName(type_Taint, "set");
		
		this.type_TaintArray = getDexClass(hierarchy, renamer, CLASS_TAINT_ARRAY);
		this.field_TaintArray_TLength = findInstanceFieldByName(type_TaintArray, "t_length");
		
		this.type_TaintArrayPrimitive = getDexClass(hierarchy, renamer, CLASS_TAINT_ARRAY_PRIMITIVE);
		this.method_TaintArrayPrimitive_Constructor = findInstanceMethodByName(type_TaintArrayPrimitive, "<init>");
		this.field_TaintArrayPrimitive_TArray = findInstanceFieldByName(type_TaintArrayPrimitive, "t_array");
	}
	
	private static DexMethod findStaticMethodByName(DexClass clsDef, String name) {
		for (val method : clsDef.getMethods())
			if (method.getMethodDef().getMethodId().getName().equals(name) &&
				method.getMethodDef().isStatic())
				return method;
		throw new Error("Failed to locate an auxiliary method");
	}
	
	private static DexMethod findInstanceMethodByName(DexClass clsDef, String name) {
		for (val method : clsDef.getMethods())
			if (method.getMethodDef().getMethodId().getName().equals(name) &&
				!method.getMethodDef().isStatic())
				return method;
		throw new Error("Failed to locate an auxiliary method");
	}

	private static DexStaticField findStaticFieldByName(DexClass clsDef, String name) {
		for (val field : clsDef.getStaticFields())
			if (field.getFieldDef().getFieldId().getName().equals(name))
				return field;
		throw new Error("Failed to locate an auxiliary static field");
	}

	private static DexInstanceField findInstanceFieldByName(DexClass clsDef, String name) {
		for (val field : clsDef.getInstanceFields())
			if (field.getFieldDef().getFieldId().getName().equals(name))
				return field;
		throw new Error("Failed to locate an auxiliary instance field");
	}

	private DexClass getDexClass(RuntimeHierarchy hierarchy, ClassRenamer classRenamer, String className) {
		val classDef = hierarchy.getBaseClassDefinition(new DexClassType(classRenamer.applyRules(className)));
		for (val cls : this.getClasses())
			if (classDef.equals(cls.getClassDef()))
				return cls;
		throw new Error("Auxiliary class was not found");
	}
	
	private InterfaceDefinition getIfaceDef(RuntimeHierarchy hierarchy, ClassRenamer classRenamer, String className) {
		return hierarchy.getInterfaceDefinition(new DexClassType(classRenamer.applyRules(className)));
	}

	private static final String CLASS_OBJTAINT = 
			DexClassType.jvm2dalvik(SafeHashMap.class.getName());
	private static final String CLASS_METHODCALLHELPER = 
			DexClassType.jvm2dalvik(MethodCallHelper.class.getName());
	private static final String CLASS_INTERNALCLASS = 
			DexClassType.jvm2dalvik(InternalClass.class.getName());
	private static final String CLASS_INTERNALMETHOD =
			DexClassType.jvm2dalvik(InternalMethod.class.getName());
	private static final String CLASS_TAINTCONSTANTS =
			DexClassType.jvm2dalvik(TaintConstants.class.getName());

	private static final String CLASS_TAINT =
			DexClassType.jvm2dalvik(Taint.class.getName());
	private static final String CLASS_TAINT_ARRAY =
			DexClassType.jvm2dalvik(TaintArray.class.getName());
	private static final String CLASS_TAINT_ARRAY_PRIMITIVE =
			DexClassType.jvm2dalvik(TaintArrayPrimitive.class.getName());
}
