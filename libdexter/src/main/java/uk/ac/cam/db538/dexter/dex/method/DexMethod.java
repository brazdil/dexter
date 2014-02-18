package uk.ac.cam.db538.dexter.dex.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.val;

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
import uk.ac.cam.db538.dexter.utils.Utils;

public class DexMethod {

    @Getter private final DexClass parentClass;
    @Getter private final MethodDefinition methodDef;
    @Getter private final DexCode methodBody;
    @Getter private final long originalSize;

    private final List<DexAnnotation> annotations;
    private final List<List<DexAnnotation>> paramAnnotations;

    public DexMethod(DexClass parent, MethodDefinition methodDef, DexCode methodBody, List<? extends DexAnnotation> annotations, List<List<DexAnnotation>> paramAnnotations, long originalSize) {
        this.parentClass = parent;
        this.methodDef = methodDef;
        this.methodBody = methodBody;
        this.originalSize = originalSize;

        this.annotations = Utils.finalList(annotations);
        this.paramAnnotations = Utils.finalList(paramAnnotations);
    }

    public DexMethod(DexClass parent, MethodDefinition methodDef, DexCode methodBody, long originalSize) {
        this(parent, methodDef, methodBody, null, null, originalSize);
    }

    public DexMethod(DexClass parentClass, EncodedMethod methodInfo, AnnotationDirectoryItem annoDir) {
        this.parentClass = parentClass;
        this.methodDef = init_FindMethodDefinition(parentClass, methodInfo);
        this.methodBody = init_ParseMethodBody(parentClass, this.methodDef, methodInfo);
        
        if (this.methodBody == null)
        	this.originalSize = -1;
        else
        	this.originalSize = this.methodBody.getInstructionList().size();

        this.annotations = Utils.finalList(init_ParseAnnotations(getParentFile(), methodInfo, annoDir));
        this.paramAnnotations = Utils.finalList(init_ParseParamAnnotations(getParentFile(), methodInfo, annoDir));
    }

    public DexMethod(DexMethod toClone, DexCode newMethodBody) {
        this(toClone.parentClass, toClone.methodDef, newMethodBody, toClone.originalSize);
    }

    public DexMethod(DexMethod toClone, MethodDefinition newDef) {
        this(toClone.parentClass, newDef, toClone.methodBody, toClone.originalSize);
    }

    public DexMethod(DexMethod toClone, DexAnnotation addAnnotation) {
        this(toClone.parentClass,
             toClone.methodDef,
             toClone.methodBody,
             Utils.concat(toClone.annotations, addAnnotation),
             toClone.paramAnnotations,
             toClone.originalSize);
    }

    private static MethodDefinition init_FindMethodDefinition(DexClass parentClass, EncodedMethod methodItem) {
        val hierarchy = parentClass.getParentFile().getHierarchy();
        val classDef = parentClass.getClassDef();

        val name = methodItem.method.getMethodName().getStringValue();
        val prototype = DexPrototype.parse(methodItem.method.getPrototype(), hierarchy.getTypeCache());

        val methodId = DexMethodId.parseMethodId(name, prototype, hierarchy.getTypeCache());
        return classDef.getMethod(methodId);
    }

    private static DexCode init_ParseMethodBody(DexClass parent, MethodDefinition methodDef, EncodedMethod methodInfo) {
        if (methodInfo.codeItem == null)
            return null;
        else
            return CodeParser.parse(methodDef, methodInfo.codeItem, parent.getParentFile().getHierarchy());
    }

    private static List<DexAnnotation> init_ParseAnnotations(Dex dex, EncodedMethod methodInfo, AnnotationDirectoryItem annoDir) {
        if (annoDir == null)
            return Collections.emptyList();
        else
            return DexAnnotation.parseAll(annoDir.getMethodAnnotations(methodInfo.method), dex.getTypeCache());
    }

    private static List<List<DexAnnotation>> init_ParseParamAnnotations(Dex dex, EncodedMethod methodInfo, AnnotationDirectoryItem annoDir) {
        if (annoDir == null)
            return Collections.emptyList();
        else
            return DexAnnotation.parseAll(annoDir.getParameterAnnotations(methodInfo.method), dex.getTypeCache());
    }

    public Dex getParentFile() {
        return parentClass.getParentFile();
    }

    public EncodedMethod writeToFile(DexFile outFile, DexAssemblingCache cache) {
        val classType = cache.getType(parentClass.getClassDef().getType());
        val methodName = cache.getStringConstant(methodDef.getMethodId().getName());
        val methodPrototype = cache.getPrototype(methodDef.getMethodId().getPrototype());

        val methodItem = MethodIdItem.internMethodIdItem(outFile, classType, methodPrototype, methodName);
        CodeItem codeItem;
        if (methodBody != null) {
            DexCodeGeneration codeGenerator = new DexCodeGeneration(this);
            codeItem = codeGenerator.generateCodeItem(outFile);
        } else {
            codeItem = null;
        }

        return new EncodedMethod(methodItem, DexUtils.assembleAccessFlags(methodDef.getAccessFlags()), codeItem);
    }

    public void addAnnotation(DexAnnotation anno) {
        annotations.add(anno);
    }

    // ASSEMBLING

    private AnnotationSetItem assembleAnnotationSetItem(DexFile outFile, DexAssemblingCache cache, Collection<DexAnnotation> annoCollections) {
        val annoList = new ArrayList<AnnotationItem>(annoCollections.size());
        for (val anno : annoCollections)
            annoList.add(anno.writeToFile(outFile, cache));

        return AnnotationSetItem.internAnnotationSetItem(outFile, annoList);
    }

    public MethodAnnotation assembleAnnotations(DexFile outFile, DexAssemblingCache cache) {
        if (annotations.size() == 0)
            return null;
        val annoSet = assembleAnnotationSetItem(outFile, cache, annotations);
        val methodAnno = new MethodAnnotation(cache.getMethod(methodDef), annoSet);

        return methodAnno;
    }

    public ParameterAnnotation assembleParameterAnnotations(DexFile outFile, DexAssemblingCache cache) {
        if (paramAnnotations.size() == 0)
            return null;

        List<AnnotationSetItem> annoList = new ArrayList<AnnotationSetItem>();
        for (val anno : paramAnnotations)
            annoList.add(assembleAnnotationSetItem(outFile, cache, anno));

        val annoSetRefList = AnnotationSetRefList.internAnnotationSetRefList(outFile, annoList);
        val paramAnno = new ParameterAnnotation(cache.getMethod(methodDef), annoSetRefList);

        return paramAnno;
    }

    public static Cache<MethodDefinition, MethodIdItem> createAssemblingCache(final DexAssemblingCache cache, final DexFile outFile) {
        return new Cache<MethodDefinition, MethodIdItem>() {
            @Override
            protected MethodIdItem createNewEntry(MethodDefinition key) {
                return MethodIdItem.internMethodIdItem(
                           outFile,
                           cache.getType(key.getParentClass().getType()),
                           cache.getPrototype(key.getMethodId().getPrototype()),
                           cache.getStringConstant(key.getMethodId().getName()));
            }
        };
    }
}
