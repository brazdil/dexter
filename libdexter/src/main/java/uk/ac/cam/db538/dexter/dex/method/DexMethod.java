package uk.ac.cam.db538.dexter.dex.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotation;
import org.jf.dexlib.AnnotationDirectoryItem.ParameterAnnotation;
import org.jf.dexlib.AnnotationItem;
import org.jf.dexlib.AnnotationSetItem;
import org.jf.dexlib.AnnotationSetRefList;
import org.jf.dexlib.ClassDataItem.EncodedMethod;
import org.jf.dexlib.CodeItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.MethodIdItem;
import com.rx201.dx.translator.DexCodeGeneration;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexAnnotation;
import uk.ac.cam.db538.dexter.dex.DexAssemblingCache;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.DexUtils;
import uk.ac.cam.db538.dexter.dex.code.CodeParser;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.utils.Cache;

public class DexMethod {
	private final DexClass parentClass;
	private final MethodDefinition methodDef;
	private final DexCode methodBody;
	private final List<DexAnnotation> annotations;
	private final List<List<DexAnnotation>> paramAnnotations;
	
	public DexMethod(DexClass parent, MethodDefinition methodDef, DexCode methodBody) {
		
		this.parentClass = parent;
		this.methodDef = methodDef;
		this.methodBody = methodBody;
		this.annotations = new ArrayList<DexAnnotation>();
		this.paramAnnotations = new ArrayList<List<DexAnnotation>>();
	}
	
	public DexMethod(DexClass parentClass, EncodedMethod methodInfo, AnnotationDirectoryItem annoDir) {
		
		this.parentClass = parentClass;
		this.methodDef = init_FindMethodDefinition(parentClass, methodInfo);
		this.methodBody = init_ParseMethodBody(parentClass, this.methodDef, methodInfo);
		this.annotations = new ArrayList<DexAnnotation>(init_ParseAnnotations(getParentFile(), methodInfo, annoDir));
		this.paramAnnotations = init_ParseParamAnnotations(getParentFile(), methodInfo, annoDir);
	}
	
	private static MethodDefinition init_FindMethodDefinition(DexClass parentClass, EncodedMethod methodItem) {
		final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = parentClass.getParentFile().getHierarchy();
		final uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition classDef = parentClass.getClassDef();
		final java.lang.String name = methodItem.method.getMethodName().getStringValue();
		final uk.ac.cam.db538.dexter.dex.type.DexPrototype prototype = DexPrototype.parse(methodItem.method.getPrototype(), hierarchy.getTypeCache());
		final uk.ac.cam.db538.dexter.dex.type.DexMethodId methodId = DexMethodId.parseMethodId(name, prototype, hierarchy.getTypeCache());
		return classDef.getMethod(methodId);
	}
	
	private static DexCode init_ParseMethodBody(DexClass parent, MethodDefinition methodDef, EncodedMethod methodInfo) {
		if (methodInfo.codeItem == null) return null; else return CodeParser.parse(methodDef, methodInfo.codeItem, parent.getParentFile().getHierarchy());
	}
	
	private static List<DexAnnotation> init_ParseAnnotations(Dex dex, EncodedMethod methodInfo, AnnotationDirectoryItem annoDir) {
		if (annoDir == null) return Collections.emptyList(); else return DexAnnotation.parseAll(annoDir.getMethodAnnotations(methodInfo.method), dex.getTypeCache());
	}
	
	private static List<List<DexAnnotation>> init_ParseParamAnnotations(Dex dex, EncodedMethod methodInfo, AnnotationDirectoryItem annoDir) {
		if (annoDir == null) return Collections.emptyList(); else return DexAnnotation.parseAll(annoDir.getParameterAnnotations(methodInfo.method), dex.getTypeCache());
	}
	
	public Dex getParentFile() {
		return parentClass.getParentFile();
	}
	
	public EncodedMethod writeToFile(DexFile outFile, DexAssemblingCache cache) {
		final org.jf.dexlib.TypeIdItem classType = cache.getType(parentClass.getClassDef().getType());
		final org.jf.dexlib.StringIdItem methodName = cache.getStringConstant(methodDef.getMethodId().getName());
		final org.jf.dexlib.ProtoIdItem methodPrototype = cache.getPrototype(methodDef.getMethodId().getPrototype());
		final org.jf.dexlib.MethodIdItem methodItem = MethodIdItem.internMethodIdItem(outFile, classType, methodPrototype, methodName);
		CodeItem codeItem;
		if (methodBody != null) {
			DexCodeGeneration codeGenerator = new DexCodeGeneration(this);
			codeItem = codeGenerator.generateCodeItem(outFile);
		} else {
			codeItem = null;
		}
		return new EncodedMethod(methodItem, DexUtils.assembleAccessFlags(methodDef.getAccessFlags()), codeItem);
	}
	// ASSEMBLING
	private AnnotationSetItem assembleAnnotationSetItem(DexFile outFile, DexAssemblingCache cache, Collection<DexAnnotation> annoCollections) {
		final java.util.ArrayList<org.jf.dexlib.AnnotationItem> annoList = new ArrayList<AnnotationItem>(annoCollections.size());
		for (final uk.ac.cam.db538.dexter.dex.DexAnnotation anno : annoCollections) annoList.add(anno.writeToFile(outFile, cache));
		return AnnotationSetItem.internAnnotationSetItem(outFile, annoList);
	}
	
	public MethodAnnotation assembleAnnotations(DexFile outFile, DexAssemblingCache cache) {
		if (annotations.size() == 0) return null;
		final org.jf.dexlib.AnnotationSetItem annoSet = assembleAnnotationSetItem(outFile, cache, annotations);
		final org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotation methodAnno = new MethodAnnotation(cache.getMethod(methodDef), annoSet);
		return methodAnno;
	}
	
	public ParameterAnnotation assembleParameterAnnotations(DexFile outFile, DexAssemblingCache cache) {
		if (paramAnnotations.size() == 0) return null;
		List<AnnotationSetItem> annoList = new ArrayList<AnnotationSetItem>();
		for (final java.util.List<uk.ac.cam.db538.dexter.dex.DexAnnotation> anno : paramAnnotations) annoList.add(assembleAnnotationSetItem(outFile, cache, anno));
		final org.jf.dexlib.AnnotationSetRefList annoSetRefList = AnnotationSetRefList.internAnnotationSetRefList(outFile, annoList);
		final org.jf.dexlib.AnnotationDirectoryItem.ParameterAnnotation paramAnno = new ParameterAnnotation(cache.getMethod(methodDef), annoSetRefList);
		return paramAnno;
	}
	
	public static Cache<MethodDefinition, MethodIdItem> createAssemblingCache(final DexAssemblingCache cache, final DexFile outFile) {
		return new Cache<MethodDefinition, MethodIdItem>(){
			
			
			@Override
			protected MethodIdItem createNewEntry(MethodDefinition key) {
				return MethodIdItem.internMethodIdItem(outFile, cache.getType(key.getParentClass().getType()), cache.getPrototype(key.getMethodId().getPrototype()), cache.getStringConstant(key.getMethodId().getName()));
			}
		};
	}
	
	@java.lang.SuppressWarnings("all")
	public DexClass getParentClass() {
		return this.parentClass;
	}
	
	@java.lang.SuppressWarnings("all")
	public MethodDefinition getMethodDef() {
		return this.methodDef;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexCode getMethodBody() {
		return this.methodBody;
	}
}