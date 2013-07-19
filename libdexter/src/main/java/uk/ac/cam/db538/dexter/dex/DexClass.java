package uk.ac.cam.db538.dexter.dex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.AnnotationDirectoryItem.FieldAnnotation;
import org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotation;
import org.jf.dexlib.AnnotationDirectoryItem.ParameterAnnotation;
import org.jf.dexlib.AnnotationItem;
import org.jf.dexlib.AnnotationSetItem;
import org.jf.dexlib.ClassDataItem;
import org.jf.dexlib.ClassDataItem.EncodedField;
import org.jf.dexlib.ClassDataItem.EncodedMethod;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.ClassDefItem.StaticFieldInitializer;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.EncodedValue.EncodedValue;
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.ClassDefinition;

public class DexClass {
	private final Dex parentFile;
	private final BaseClassDefinition classDef;
	private final List<DexMethod> _methods;
	private final List<DexMethod> methods;
	private final List<DexInstanceField> _instanceFields;
	private final List<DexInstanceField> instanceFields;
	private final List<DexStaticField> _staticFields;
	private final List<DexStaticField> staticFields;
	private final List<DexAnnotation> _annotations;
	private final List<DexAnnotation> annotations;
	private final String sourceFile;
	
	public DexClass(Dex parent, BaseClassDefinition classDef, String sourceFile) {
		
		this.parentFile = parent;
		this.classDef = classDef;
		this._methods = new ArrayList<DexMethod>();
		this.methods = Collections.unmodifiableList(this._methods);
		this._instanceFields = new ArrayList<DexInstanceField>();
		this.instanceFields = Collections.unmodifiableList(this._instanceFields);
		this._staticFields = new ArrayList<DexStaticField>();
		this.staticFields = Collections.unmodifiableList(this._staticFields);
		this._annotations = new ArrayList<DexAnnotation>();
		this.annotations = Collections.unmodifiableList(this._annotations);
		this.sourceFile = sourceFile;
	}
	
	public DexClass(Dex parent, ClassDefItem clsItem) {
		this(parent, init_FindClassDefinition(parent, clsItem), DexUtils.parseString(clsItem.getSourceFile()));
		final org.jf.dexlib.AnnotationDirectoryItem annotationDirectory = clsItem.getAnnotations();
		this._annotations.addAll(init_ParseAnnotations(parent, annotationDirectory));
		final org.jf.dexlib.ClassDataItem clsData = clsItem.getClassData();
		if (clsData != null) {
			// static fields
			for (final org.jf.dexlib.ClassDataItem.EncodedField sfieldItem : clsData.getStaticFields()) this._staticFields.add(new DexStaticField(this, clsItem, sfieldItem, annotationDirectory));
			// instance fields
			for (final org.jf.dexlib.ClassDataItem.EncodedField ifieldItem : clsData.getInstanceFields()) this._instanceFields.add(new DexInstanceField(this, ifieldItem, annotationDirectory));
			// methods
			for (final org.jf.dexlib.ClassDataItem.EncodedMethod methodItem : clsData.getDirectMethods()) this._methods.add(new DexMethod(this, methodItem, annotationDirectory));
		}
	}
	
	private static BaseClassDefinition init_FindClassDefinition(Dex parent, ClassDefItem clsItem) {
		final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = parent.getHierarchy();
		final uk.ac.cam.db538.dexter.dex.type.DexClassType clsType = DexClassType.parse(clsItem.getClassType().getTypeDescriptor(), hierarchy.getTypeCache());
		return hierarchy.getBaseClassDefinition(clsType);
	}
	
	private static List<DexAnnotation> init_ParseAnnotations(Dex parent, AnnotationDirectoryItem annoDir) {
		if (annoDir == null) return Collections.emptyList(); else return DexAnnotation.parseAll(annoDir.getClassAnnotations(), parent.getTypeCache());
	}
	
	public List<DexClassType> getInterfaceTypes() {
		if (classDef instanceof ClassDefinition) {
			final java.util.Set<uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition> ifaceDefs = ((ClassDefinition)classDef).getInterfaces();
			if (ifaceDefs.isEmpty()) return Collections.emptyList();
			final java.util.ArrayList<uk.ac.cam.db538.dexter.dex.type.DexClassType> list = new ArrayList<DexClassType>(ifaceDefs.size());
			for (final uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition ifaceDef : ifaceDefs) list.add(ifaceDef.getType());
			return list;
		} else return Collections.emptyList();
	}
	
	public void addAnnotation(DexAnnotation anno) {
		this._annotations.add(anno);
	}
	
	public void instrument(DexInstrumentationCache cache) {
//		System.out.println("Instrumenting class " + this.classDef.getType().getPrettyName());
//	  
//		for (val method : this._methods)
//			method.instrument(cache);
//
//		this.addAnnotation(new DexAnnotation(
//			parentFile.getAuxiliaryDex().getAnno_InternalClass().getType(),
//			AnnotationVisibility.RUNTIME));
	}
	
	public void writeToFile(DexFile outFile, DexAssemblingCache cache) {
		System.out.println("Assembling class " + this.classDef.getType().getPrettyName());
		final java.util.List<uk.ac.cam.db538.dexter.dex.DexAnnotation> classAnnotations = this.getAnnotations();
		final org.jf.dexlib.TypeIdItem asmClassType = cache.getType(classDef.getType());
		final org.jf.dexlib.TypeIdItem asmSuperType = cache.getType(classDef.getSuperclass().getType());
		final int asmAccessFlags = DexUtils.assembleAccessFlags(classDef.getAccessFlags());
		final org.jf.dexlib.TypeListItem asmInterfaces = cache.getTypeList(getInterfaceTypes());
		final org.jf.dexlib.StringIdItem asmSourceFile = cache.getStringConstant(sourceFile);
		final java.util.ArrayList<org.jf.dexlib.AnnotationItem> asmClassAnnotations = new ArrayList<AnnotationItem>(classAnnotations.size());
		for (final uk.ac.cam.db538.dexter.dex.DexAnnotation anno : classAnnotations) asmClassAnnotations.add(anno.writeToFile(outFile, cache));
		final java.util.ArrayList<org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotation> asmMethodAnnotations = new ArrayList<MethodAnnotation>(_methods.size());
		for (final uk.ac.cam.db538.dexter.dex.method.DexMethod method : _methods) {
			final org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotation methodAnno = method.assembleAnnotations(outFile, cache);
			if (methodAnno != null) asmMethodAnnotations.add(methodAnno);
		}
		final java.util.ArrayList<org.jf.dexlib.AnnotationDirectoryItem.FieldAnnotation> asmFieldAnnotations = new ArrayList<FieldAnnotation>(_instanceFields.size() + _staticFields.size());
		for (final uk.ac.cam.db538.dexter.dex.field.DexInstanceField field : _instanceFields) {
			final org.jf.dexlib.AnnotationDirectoryItem.FieldAnnotation fieldAnno = field.assembleAnnotations(outFile, cache);
			if (fieldAnno != null) asmFieldAnnotations.add(fieldAnno);
		}
		for (final uk.ac.cam.db538.dexter.dex.field.DexStaticField field : _staticFields) {
			final org.jf.dexlib.AnnotationDirectoryItem.FieldAnnotation fieldAnno = field.assembleAnnotations(outFile, cache);
			if (fieldAnno != null) asmFieldAnnotations.add(fieldAnno);
		}
		final java.util.ArrayList<org.jf.dexlib.AnnotationDirectoryItem.ParameterAnnotation> asmParamAnnotations = new ArrayList<ParameterAnnotation>(_methods.size());
		for (final uk.ac.cam.db538.dexter.dex.method.DexMethod method : _methods) {
			final org.jf.dexlib.AnnotationDirectoryItem.ParameterAnnotation paramAnno = method.assembleParameterAnnotations(outFile, cache);
			if (paramAnno != null) asmParamAnnotations.add(paramAnno);
		}
		AnnotationSetItem asmClassAnnotationSet = null;
		if (asmClassAnnotations.size() > 0) asmClassAnnotationSet = AnnotationSetItem.internAnnotationSetItem(outFile, asmClassAnnotations);
		AnnotationDirectoryItem asmAnnotations = null;
		if (asmClassAnnotationSet != null || asmFieldAnnotations.size() != 0 || asmMethodAnnotations.size() != 0 || asmParamAnnotations.size() != 0) {
			asmAnnotations = AnnotationDirectoryItem.internAnnotationDirectoryItem(outFile, asmClassAnnotationSet, asmFieldAnnotations, asmMethodAnnotations, asmParamAnnotations);
		}
		final java.util.LinkedList<org.jf.dexlib.ClassDataItem.EncodedField> asmStaticFields = new LinkedList<EncodedField>();
		final java.util.LinkedList<org.jf.dexlib.ClassDataItem.EncodedField> asmInstanceFields = new LinkedList<EncodedField>();
		final java.util.LinkedList<org.jf.dexlib.ClassDataItem.EncodedMethod> asmDirectMethods = new LinkedList<EncodedMethod>();
		final java.util.LinkedList<org.jf.dexlib.ClassDataItem.EncodedMethod> asmVirtualMethods = new LinkedList<EncodedMethod>();
		final java.util.LinkedList<org.jf.dexlib.ClassDefItem.StaticFieldInitializer> staticFieldInitializers = new LinkedList<StaticFieldInitializer>();
		for (final uk.ac.cam.db538.dexter.dex.field.DexStaticField field : _staticFields) {
			EncodedField outField = field.writeToFile(outFile, cache);
			asmStaticFields.add(outField);
			EncodedValue initialValue = field.getInitialValue();
			if (initialValue != null) initialValue = DexUtils.cloneEncodedValue(initialValue, cache);
			staticFieldInitializers.add(new StaticFieldInitializer(initialValue, outField));
		}
		for (final uk.ac.cam.db538.dexter.dex.field.DexInstanceField field : _instanceFields) asmInstanceFields.add(field.writeToFile(outFile, cache));
		for (final uk.ac.cam.db538.dexter.dex.method.DexMethod method : _methods) {
			if (method.getMethodDef().isVirtual()) asmVirtualMethods.add(method.writeToFile(outFile, cache)); else asmDirectMethods.add(method.writeToFile(outFile, cache));
		}
		final org.jf.dexlib.ClassDataItem classData = ClassDataItem.internClassDataItem(outFile, asmStaticFields, asmInstanceFields, asmDirectMethods, asmVirtualMethods);
		ClassDefItem.internClassDefItem(outFile, asmClassType, asmAccessFlags, asmSuperType, asmInterfaces, asmSourceFile, asmAnnotations, classData, staticFieldInitializers);
	}
	
	@java.lang.SuppressWarnings("all")
	public Dex getParentFile() {
		return this.parentFile;
	}
	
	@java.lang.SuppressWarnings("all")
	public BaseClassDefinition getClassDef() {
		return this.classDef;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<DexMethod> getMethods() {
		return this.methods;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<DexInstanceField> getInstanceFields() {
		return this.instanceFields;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<DexStaticField> getStaticFields() {
		return this.staticFields;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<DexAnnotation> getAnnotations() {
		return this.annotations;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getSourceFile() {
		return this.sourceFile;
	}
}