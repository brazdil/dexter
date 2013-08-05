package uk.ac.cam.db538.dexter.transform.taint;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.aux.InvokeTaintStore;
import uk.ac.cam.db538.dexter.aux.anno.InternalClass;
import uk.ac.cam.db538.dexter.aux.anno.InternalMethod;
import uk.ac.cam.db538.dexter.aux.struct.Assigner;
import uk.ac.cam.db538.dexter.aux.struct.InternalDataStructure;
import uk.ac.cam.db538.dexter.aux.struct.Taint;
import uk.ac.cam.db538.dexter.aux.struct.TaintArray;
import uk.ac.cam.db538.dexter.aux.struct.TaintArrayPrimitive;
import uk.ac.cam.db538.dexter.aux.struct.TaintArrayReference;
import uk.ac.cam.db538.dexter.aux.struct.TaintExternal;
import uk.ac.cam.db538.dexter.aux.struct.TaintInternal;
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

	@Getter private final DexStaticField field_CallParamTaint;
	@Getter private final DexStaticField field_CallResultTaint;
	
	@Getter private final InterfaceDefinition anno_InternalClass;
	@Getter private final InterfaceDefinition anno_InternalMethod;
	
	@Getter private final DexClass type_InternalStructure;
	
	@Getter private final DexClass type_Taint;
	@Getter private final DexMethod method_Taint_Get;
	@Getter private final DexMethod method_Taint_Set;
	
	@Getter private final DexClass type_TaintExternal;
	@Getter private final DexMethod method_TaintExternal_Constructor;
	
	@Getter private final DexClass type_TaintInternal;
	@Getter private final DexMethod method_TaintInternal_ClearVisited;
	
	@Getter private final DexInstanceField field_TaintArray_TLength;
	
	@Getter private final DexClass type_TaintArrayPrimitive;
	@Getter private final DexClass type_TaintArrayReference;

	@Getter private final DexMethod method_Assigner_NewExternal;
	@Getter private final DexMethod method_Assigner_NewInternal;
	@Getter private final DexMethod method_Assigner_NewArrayPrimitive;
	@Getter private final DexMethod method_Assigner_NewArrayReference;
	@Getter private final DexMethod method_Assigner_LookupExternal;
	@Getter private final DexMethod method_Assigner_LookupInternal;
	@Getter private final DexMethod method_Assigner_LookupUndecidable;
	@Getter private final DexMethod method_Assigner_LookupArrayPrimitive;
	@Getter private final DexMethod method_Assigner_LookupArrayReference;

	public AuxiliaryDex(DexFile dexAux, RuntimeHierarchy hierarchy, ClassRenamer renamer) {
		super(dexAux, hierarchy, null, renamer);
		
		// InvokeTaintStore class
		val clsInvokeTaintStore = getDexClass(InvokeTaintStore.class, hierarchy, renamer);
		this.field_CallParamTaint = findStaticFieldByName(clsInvokeTaintStore, "ARGS");
		this.field_CallResultTaint = findStaticFieldByName(clsInvokeTaintStore, "RES");
		
		// Annotations
		this.anno_InternalClass = getIfaceDef(InternalClass.class, hierarchy, renamer);
		this.anno_InternalMethod = getIfaceDef(InternalMethod.class, hierarchy, renamer);

		// InternalStructure interface
		this.type_InternalStructure = getDexClass(InternalDataStructure.class, hierarchy, renamer);
		
		// Taint types
		this.type_Taint = getDexClass(Taint.class, hierarchy, renamer);
		this.method_Taint_Get = findInstanceMethodByName(type_Taint, "get");
		this.method_Taint_Set = findInstanceMethodByName(type_Taint, "set");
		
		this.type_TaintExternal = getDexClass(TaintExternal.class, hierarchy, renamer);
		this.method_TaintExternal_Constructor = findInstanceMethodByName(type_TaintExternal, "<init>");
		
		this.type_TaintInternal = getDexClass(TaintInternal.class, hierarchy, renamer);
		this.method_TaintInternal_ClearVisited = findStaticMethodByName(type_TaintInternal, "clearVisited");

		val clsTaintArray = getDexClass(TaintArray.class, hierarchy, renamer);
		this.field_TaintArray_TLength = findInstanceFieldByName(clsTaintArray, "t_length");
		
		this.type_TaintArrayPrimitive = getDexClass(TaintArrayPrimitive.class, hierarchy, renamer);
		this.type_TaintArrayReference = getDexClass(TaintArrayReference.class, hierarchy, renamer);
		
		// Assigner
		val clsAssigner = getDexClass(Assigner.class, hierarchy, renamer);
		this.method_Assigner_NewExternal = findStaticMethodByName(clsAssigner, "newExternal");
		this.method_Assigner_NewInternal = findStaticMethodByName(clsAssigner, "newInternal");
		this.method_Assigner_NewArrayPrimitive = findStaticMethodByName(clsAssigner, "newArrayPrimitive");
		this.method_Assigner_NewArrayReference = findStaticMethodByName(clsAssigner, "newArrayReference");
		this.method_Assigner_LookupExternal = findStaticMethodByName(clsAssigner, "lookupExternal");
		this.method_Assigner_LookupInternal = findStaticMethodByName(clsAssigner, "lookupInternal");
		this.method_Assigner_LookupUndecidable = findStaticMethodByName(clsAssigner, "lookupUndecidable");
		this.method_Assigner_LookupArrayPrimitive = findStaticMethodByName(clsAssigner, "lookupArrayPrimitive");
		this.method_Assigner_LookupArrayReference = findStaticMethodByName(clsAssigner, "lookupArrayReference");
	}
	
	private static DexMethod findStaticMethodByName(DexClass clsDef, String name) {
		for (val method : clsDef.getMethods())
			if (method.getMethodDef().getMethodId().getName().equals(name) &&
				method.getMethodDef().isStatic())
				return method;
		throw new Error("Failed to locate an auxiliary method " + name);
	}
	
	private static DexMethod findInstanceMethodByName(DexClass clsDef, String name) {
		for (val method : clsDef.getMethods())
			if (method.getMethodDef().getMethodId().getName().equals(name) &&
				!method.getMethodDef().isStatic())
				return method;
		throw new Error("Failed to locate an auxiliary method" + name);
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

	private DexClass getDexClass(Class<?> clazz, RuntimeHierarchy hierarchy, ClassRenamer classRenamer) {
		val className = DexClassType.jvm2dalvik(clazz.getName());
		val classDef = hierarchy.getBaseClassDefinition(new DexClassType(classRenamer.applyRules(className)));
		for (val cls : this.getClasses())
			if (classDef.equals(cls.getClassDef()))
				return cls;
		throw new Error("Auxiliary class " + className + " was not found");
	}
	
	private InterfaceDefinition getIfaceDef(Class<?> clazz, RuntimeHierarchy hierarchy, ClassRenamer classRenamer) {
		val className = DexClassType.jvm2dalvik(clazz.getName());
		return hierarchy.getInterfaceDefinition(new DexClassType(classRenamer.applyRules(className)));
	}
}
