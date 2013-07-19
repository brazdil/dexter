package uk.ac.cam.db538.dexter.dex;

import org.jf.dexlib.DexFile;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.aux.InternalClassAnnotation;
import uk.ac.cam.db538.dexter.aux.InternalMethodAnnotation;
import uk.ac.cam.db538.dexter.aux.MethodCallHelper;
import uk.ac.cam.db538.dexter.aux.ObjectTaintStorage;
import uk.ac.cam.db538.dexter.aux.TaintConstants;

public class AuxiliaryDex extends Dex {
	private final DexMethod method_TaintGet;
	private final DexMethod method_TaintSet;
	private final DexMethod method_QueryTaint;
	private final DexMethod method_ServiceTaint;
	private final DexStaticField field_CallParamTaint;
	private final DexStaticField field_CallResultTaint;
	private final DexStaticField field_CallParamSemaphore;
	private final DexStaticField field_CallResultSemaphore;
	private final InterfaceDefinition anno_InternalClass;
	private final InterfaceDefinition anno_InternalMethod;
	
	public AuxiliaryDex(DexFile dexAux, RuntimeHierarchy hierarchy, ClassRenamer renamer) {
		super(dexAux, hierarchy, null, renamer);
		// ObjectTaintStorage class
		final uk.ac.cam.db538.dexter.dex.DexClass clsObjTaint = getDexClass(hierarchy, renamer, CLASS_OBJTAINT);
		this.method_TaintGet = findStaticMethodByName(clsObjTaint, "get");
		this.method_TaintSet = findStaticMethodByName(clsObjTaint, "set");
		// TaintConstants class
		final uk.ac.cam.db538.dexter.dex.DexClass clsTaintConsts = getDexClass(hierarchy, renamer, CLASS_TAINTCONSTANTS);
		this.method_QueryTaint = findStaticMethodByName(clsTaintConsts, "queryTaint");
		this.method_ServiceTaint = findStaticMethodByName(clsTaintConsts, "serviceTaint");
		// MethodCallHelper class
		final uk.ac.cam.db538.dexter.dex.DexClass clsMethodCallHelper = getDexClass(hierarchy, renamer, CLASS_METHODCALLHELPER);
		this.field_CallParamTaint = findStaticFieldByName(clsMethodCallHelper, "ARG");
		this.field_CallResultTaint = findStaticFieldByName(clsMethodCallHelper, "RES");
		this.field_CallParamSemaphore = findStaticFieldByName(clsMethodCallHelper, "S_ARG");
		this.field_CallResultSemaphore = findStaticFieldByName(clsMethodCallHelper, "S_RES");
		// Annotations
		this.anno_InternalClass = getAnnoDef(hierarchy, renamer, CLASS_INTERNALCLASS);
		this.anno_InternalMethod = getAnnoDef(hierarchy, renamer, CLASS_INTERNALMETHOD);
	}
	
	private static DexMethod findStaticMethodByName(DexClass clsDef, String name) {
		for (final uk.ac.cam.db538.dexter.dex.method.DexMethod method : clsDef.getMethods()) if (method.getMethodDef().getMethodId().getName().equals(name) && method.getMethodDef().isStatic()) return method;
		throw new Error("Failed to locate an auxiliary method");
	}
	
	private static DexStaticField findStaticFieldByName(DexClass clsDef, String name) {
		for (final uk.ac.cam.db538.dexter.dex.field.DexStaticField field : clsDef.getStaticFields()) if (field.getFieldDef().getFieldId().getName().equals(name)) return field;
		throw new Error("Failed to locate an auxiliary static field");
	}
	
	private DexClass getDexClass(RuntimeHierarchy hierarchy, ClassRenamer classRenamer, String className) {
		final uk.ac.cam.db538.dexter.hierarchy.ClassDefinition classDef = hierarchy.getClassDefinition(new DexClassType(classRenamer.applyRules(className)));
		for (final uk.ac.cam.db538.dexter.dex.DexClass cls : this.getClasses()) if (classDef.equals(cls.getClassDef())) return cls;
		throw new Error("Auxiliary class was not found");
	}
	
	private InterfaceDefinition getAnnoDef(RuntimeHierarchy hierarchy, ClassRenamer classRenamer, String className) {
		return hierarchy.getInterfaceDefinition(new DexClassType(classRenamer.applyRules(className)));
	}
	private static final String CLASS_OBJTAINT = DexClassType.jvm2dalvik(ObjectTaintStorage.class.getName());
	private static final String CLASS_METHODCALLHELPER = DexClassType.jvm2dalvik(MethodCallHelper.class.getName());
	private static final String CLASS_INTERNALCLASS = DexClassType.jvm2dalvik(InternalClassAnnotation.class.getName());
	private static final String CLASS_INTERNALMETHOD = DexClassType.jvm2dalvik(InternalMethodAnnotation.class.getName());
	private static final String CLASS_TAINTCONSTANTS = DexClassType.jvm2dalvik(TaintConstants.class.getName());
	
	@java.lang.SuppressWarnings("all")
	public DexMethod getMethod_TaintGet() {
		return this.method_TaintGet;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexMethod getMethod_TaintSet() {
		return this.method_TaintSet;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexMethod getMethod_QueryTaint() {
		return this.method_QueryTaint;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexMethod getMethod_ServiceTaint() {
		return this.method_ServiceTaint;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStaticField getField_CallParamTaint() {
		return this.field_CallParamTaint;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStaticField getField_CallResultTaint() {
		return this.field_CallResultTaint;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStaticField getField_CallParamSemaphore() {
		return this.field_CallParamSemaphore;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStaticField getField_CallResultSemaphore() {
		return this.field_CallResultSemaphore;
	}
	
	@java.lang.SuppressWarnings("all")
	public InterfaceDefinition getAnno_InternalClass() {
		return this.anno_InternalClass;
	}
	
	@java.lang.SuppressWarnings("all")
	public InterfaceDefinition getAnno_InternalMethod() {
		return this.anno_InternalMethod;
	}
}