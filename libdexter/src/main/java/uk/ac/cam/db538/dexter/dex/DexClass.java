package uk.ac.cam.db538.dexter.dex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.val;

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
import uk.ac.cam.db538.dexter.hierarchy.InstanceFieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;
import uk.ac.cam.db538.dexter.utils.Utils;

public class DexClass {

	@Getter private final Dex parentFile;
	@Getter private final BaseClassDefinition classDef;
  
	@Getter private List<DexMethod> methods;
    @Getter private List<DexInstanceField> instanceFields;
	@Getter private List<DexStaticField> staticFields;
	@Getter private List<DexAnnotation> annotations;
  
	@Getter private final String sourceFile;
  
	public DexClass(Dex parent, BaseClassDefinition classDef, String sourceFile) {
		this.parentFile = parent;
		this.classDef = classDef;

		replaceMethods(new ArrayList<DexMethod>());
		replaceInstanceFields(new ArrayList<DexInstanceField>());
		replaceStaticFields(new ArrayList<DexStaticField>());
		replaceAnnotations(new ArrayList<DexAnnotation>());
        
    	this.sourceFile = sourceFile;
	}
  
	public DexClass(Dex parent, ClassDefItem clsItem) {
		this(parent,
		     init_FindClassDefinition(parent, clsItem),
		     DexUtils.parseString(clsItem.getSourceFile()));

		val annotationDirectory = clsItem.getAnnotations();
		replaceAnnotations(init_ParseAnnotations(parent, annotationDirectory));
		
		val clsData = clsItem.getClassData();
		if (clsData != null) {
			
			// static fields
			List<DexStaticField> sfields = new ArrayList<DexStaticField>(clsData.getStaticFieldCount()); 
			int sfieldIndex = 0;
			for (val sfieldItem : clsData.getStaticFields())
				sfields.add(new DexStaticField(this, clsItem, sfieldItem, sfieldIndex++, annotationDirectory));
			replaceStaticFields(sfields);

			// instance fields
			List<DexInstanceField> ifields = new ArrayList<DexInstanceField>(clsData.getInstanceFieldCount()); 
			for (val ifieldItem : clsData.getInstanceFields())
				ifields.add(new DexInstanceField(this, ifieldItem, annotationDirectory));
			replaceInstanceFields(ifields);
			
			// methods
			List<DexMethod> methods = new ArrayList<DexMethod>(clsData.getDirectMethodCount() + clsData.getStaticFieldCount());
			for (val methodItem : clsData.getDirectMethods())
				methods.add(new DexMethod(this, methodItem, annotationDirectory));
			for (val methodItem : clsData.getVirtualMethods())
				methods.add(new DexMethod(this, methodItem, annotationDirectory));
			replaceMethods(methods);
		}
	}
	
	private static BaseClassDefinition init_FindClassDefinition(Dex parent, ClassDefItem clsItem) {
		val hierarchy = parent.getHierarchy();
		val clsType = DexClassType.parse(clsItem.getClassType().getTypeDescriptor(), 
		                                 hierarchy.getTypeCache());
		return hierarchy.getBaseClassDefinition(clsType); 
	}

	private static List<DexAnnotation> init_ParseAnnotations(Dex parent, AnnotationDirectoryItem annoDir) {
		if (annoDir == null)
			return Collections.emptyList();
		else
			return DexAnnotation.parseAll(annoDir.getClassAnnotations(), parent.getTypeCache());
	}
	
	public DexInstanceField getInstanceField(InstanceFieldDefinition fieldDef) {
		for (DexInstanceField field : instanceFields)
			if (field.getFieldDef().equals(fieldDef))
				return field;
		return null;
	}
	
	public DexStaticField getStaticField(StaticFieldDefinition fieldDef) {
		for (DexStaticField field : staticFields)
			if (field.getFieldDef().equals(fieldDef))
				return field;
		return null;
	}

	public List<DexClassType> getInterfaceTypes() {
		val ifaceDefs = classDef.getInterfaces();
		if (ifaceDefs.isEmpty())
			return Collections.emptyList();

		val list = new ArrayList<DexClassType>(ifaceDefs.size());
		for (val ifaceDef : ifaceDefs)
			list.add(ifaceDef.getType());
		return list;
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
		val classAnnotations = this.getAnnotations();

		val asmClassType = cache.getType(classDef.getType());
		val asmSuperType = cache.getType(classDef.getSuperclass().getType());
		val asmAccessFlags = DexUtils.assembleAccessFlags(classDef.getAccessFlags());
		val asmInterfaces = cache.getTypeList(getInterfaceTypes());
		val asmSourceFile = cache.getStringConstant(sourceFile);

		val asmClassAnnotations = new ArrayList<AnnotationItem>(classAnnotations.size());
		for (val anno : classAnnotations)
			asmClassAnnotations.add(anno.writeToFile(outFile, cache));

		val asmMethodAnnotations = new ArrayList<MethodAnnotation>(methods.size());
		for (val method : methods) {
			val methodAnno = method.assembleAnnotations(outFile, cache);
			if (methodAnno != null)
				asmMethodAnnotations.add(methodAnno);
		}

		val asmFieldAnnotations = new ArrayList<FieldAnnotation>(instanceFields.size() + staticFields.size());
		for (val field : instanceFields) {
			val fieldAnno = field.assembleAnnotations(outFile, cache);
			if (fieldAnno != null)
				asmFieldAnnotations.add(fieldAnno);
		}
		for (val field : staticFields) {
			val fieldAnno = field.assembleAnnotations(outFile, cache);
			if (fieldAnno != null)
				asmFieldAnnotations.add(fieldAnno);
		}

		val asmParamAnnotations = new ArrayList<ParameterAnnotation>(methods.size());
		for (val method : methods) {
			val paramAnno = method.assembleParameterAnnotations(outFile, cache);
			if (paramAnno != null)
				asmParamAnnotations.add(paramAnno);
		}

		AnnotationSetItem asmClassAnnotationSet = null;
		if (asmClassAnnotations.size() > 0)
			asmClassAnnotationSet = AnnotationSetItem.internAnnotationSetItem(
                                    outFile,
                                    asmClassAnnotations);
    
		AnnotationDirectoryItem asmAnnotations = null;
		if (asmClassAnnotationSet!= null || asmFieldAnnotations.size() != 0 || 
				asmMethodAnnotations.size() != 0 || asmParamAnnotations.size() != 0) {
			asmAnnotations = AnnotationDirectoryItem.internAnnotationDirectoryItem(
                                                   outFile,
                                                   asmClassAnnotationSet,
                                                   asmFieldAnnotations,
                                                   asmMethodAnnotations,
                                                   asmParamAnnotations);
		}

		val asmStaticFields = new LinkedList<EncodedField>();
		val asmInstanceFields = new LinkedList<EncodedField>();
		val asmDirectMethods = new LinkedList<EncodedMethod>();
		val asmVirtualMethods = new LinkedList<EncodedMethod>();
		val staticFieldInitializers = new LinkedList<StaticFieldInitializer>();

		for (val field : staticFields) {
			EncodedField outField = field.writeToFile(outFile, cache);  
			asmStaticFields.add(outField);
        
			EncodedValue initialValue = field.getInitialValue();
			if (initialValue != null)
				initialValue = DexUtils.cloneEncodedValue(initialValue, cache);
			staticFieldInitializers.add(new StaticFieldInitializer(initialValue, outField));
		}
    
		for (val field : instanceFields)
			asmInstanceFields.add(field.writeToFile(outFile, cache));

		for (val method : methods) {
			if (method.getMethodDef().isVirtual())
				asmVirtualMethods.add(method.writeToFile(outFile, cache));
			else
				asmDirectMethods.add(method.writeToFile(outFile, cache));
		}

		val classData = ClassDataItem.internClassDataItem(
			  outFile,
			  asmStaticFields,
			  asmInstanceFields,
			  asmDirectMethods,
			  asmVirtualMethods);

		ClassDefItem.internClassDefItem(
				outFile, asmClassType, asmAccessFlags, asmSuperType,
				asmInterfaces, asmSourceFile, asmAnnotations,
				classData, staticFieldInitializers);
	}
	
	public void replaceMethods(List<? extends DexMethod> newMethods) {
		this.methods = Utils.finalList(newMethods);
	}

	public void replaceStaticFields(List<? extends DexStaticField> newStaticFields) {
		this.staticFields = Utils.finalList(newStaticFields);
	}

	public void replaceInstanceFields(List<? extends DexInstanceField> newInstanceFields) {
		this.instanceFields = Utils.finalList(newInstanceFields);
	}

	public void replaceAnnotations(List<? extends DexAnnotation> newAnnotations) {
		this.annotations = Utils.finalList(newAnnotations);
	}
}
