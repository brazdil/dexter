package uk.ac.cam.db538.dexter.transform.taint;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.aux.DexterApplication;
import uk.ac.cam.db538.dexter.aux.FakeSignature;
import uk.ac.cam.db538.dexter.aux.InvokeTaintStore;
import uk.ac.cam.db538.dexter.aux.StaticTaintFields;
import uk.ac.cam.db538.dexter.aux.TaintConstants;
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
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexArrayType;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.transform.Transform;
import uk.ac.cam.db538.dexter.utils.Utils;
import uk.ac.cam.db538.dexter.utils.Utils.NameAcceptor;

public class AuxiliaryDex extends Dex {

	@Getter private final DexClass type_DexterApplication;
	
	@Getter private final DexMethod method_TaintConstants_QueryTaint;
	@Getter private final DexMethod method_TaintConstants_ServiceTaint;
	@Getter private final DexMethod method_TaintConstants_IsSourceTaint;
	@Getter private final DexMethod method_TaintConstants_IsSinkTaint;
	@Getter private final DexMethod method_TaintConstants_LogLeakage;
	
	@Getter private final DexMethod method_Call_SetInternalCall;
	@Getter private final DexMethod method_Call_IsInternalCall;
    @Getter private final DexStaticField field_CallPrimitiveParamTaint;
    @Getter private final DexStaticField field_CallReferenceParamTaint;
    @Getter private final DexStaticField field_CallPrimitiveResultTaint;
    @Getter private final DexStaticField field_CallReferenceResultTaint;

    @Getter private final DexClass type_StaticTaintFields;

    @Getter private final InterfaceDefinition anno_InternalMethod;

    @Getter private final DexClass type_InternalStructure;
    @Getter private final DexMethod method_InternalStructure_GetTaint;
    @Getter private final DexMethod method_InternalStructure_SetTaint;

    @Getter private final DexClass type_Taint;
    @Getter private final DexMethod method_Taint_Get;
    @Getter private final DexMethod method_Taint_Set;
    @Getter private final DexMethod method_Taint_GetExternal;
    @Getter private final DexMethod method_Taint_SetExternal;
    @Getter private final DexArrayType arraytype_Taint;

    @Getter private final DexClass type_TaintExternal;

    @Getter private final DexClass type_TaintInternal;
    @Getter private final DexMethod method_TaintInternal_ClearVisited;

    @Getter private final DexInstanceField field_TaintArray_TLength;

    @Getter private final DexClass type_TaintArrayPrimitive;
    @Getter private final DexInstanceField field_TaintArrayPrimitive_TArray;

    @Getter private final DexClass type_TaintArrayReference;
    @Getter private final DexInstanceField field_TaintArrayReference_TArray;

    @Getter private final DexMethod method_Assigner_SetConstructedSuperTaint;
    @Getter private final DexMethod method_Assigner_EraseConstructedSuperTaint;
    @Getter private final DexMethod method_Assigner_DefineExternal;
    @Getter private final DexMethod method_Assigner_NewExternal;
    @Getter private final DexMethod method_Assigner_NewExternal_Null;
    @Getter private final DexMethod method_Assigner_NewExternal_Undefined;
    @Getter private final DexMethod method_Assigner_NewInternal_Null;
    @Getter private final DexMethod method_Assigner_NewInternal_Undefined;
    @Getter private final DexMethod method_Assigner_NewArrayPrimitive;
    @Getter private final DexMethod method_Assigner_NewArrayReference;
    @Getter private final DexMethod method_Assigner_LookupExternal;
    @Getter private final DexMethod method_Assigner_LookupInternal;
    @Getter private final DexMethod method_Assigner_LookupUndecidable;
    @Getter private final DexMethod method_Assigner_LookupArrayPrimitive;
    @Getter private final DexMethod method_Assigner_LookupArrayReference;
    
    @Getter private final DexClass type_FakeSignature;
    @Getter private final DexMethod method_FakeSignature_Clinit;
    @Getter private final DexStaticField field_FakeSignature_PackageName;
    @Getter private final DexStaticField field_FakeSignature_Signatures;


    public AuxiliaryDex(DexFile dexAux, RuntimeHierarchy hierarchy, ClassRenamer renamer) {
        super(dexAux, hierarchy, null, null, null, renamer);

        this.type_DexterApplication = getDexClass(DexterApplication.class, hierarchy, renamer);
        
        // InvokeTaintStore class
        val clsTaintConstants = getDexClass(TaintConstants.class, hierarchy, renamer);
        this.method_TaintConstants_QueryTaint = findStaticMethodByName(clsTaintConstants, "queryTaint");
        this.method_TaintConstants_ServiceTaint = findStaticMethodByName(clsTaintConstants, "serviceTaint");
        this.method_TaintConstants_IsSourceTaint = findStaticMethodByName(clsTaintConstants, "isSourceTaint");
        this.method_TaintConstants_IsSinkTaint = findStaticMethodByName(clsTaintConstants, "isSinkTaint");
        this.method_TaintConstants_LogLeakage = findStaticMethodByName(clsTaintConstants, "logLeakage");

        // InvokeTaintStore class
        val clsInvokeTaintStore = getDexClass(InvokeTaintStore.class, hierarchy, renamer);
        this.method_Call_SetInternalCall = findStaticMethodByName(clsInvokeTaintStore, "setInternalCall");
        this.method_Call_IsInternalCall = findStaticMethodByName(clsInvokeTaintStore, "isInternalCall");
        this.field_CallPrimitiveParamTaint = findStaticFieldByName(clsInvokeTaintStore, "ARGS_PRIM");
        this.field_CallReferenceParamTaint = findStaticFieldByName(clsInvokeTaintStore, "ARGS_REF");
        this.field_CallPrimitiveResultTaint = findStaticFieldByName(clsInvokeTaintStore, "RES_PRIM");
        this.field_CallReferenceResultTaint = findStaticFieldByName(clsInvokeTaintStore, "RES_REF");

        this.type_StaticTaintFields = getDexClass(StaticTaintFields.class, hierarchy, renamer);

        // Annotations
        this.anno_InternalMethod = getIfaceDef(InternalMethod.class, hierarchy, renamer);

        // InternalStructure interface
        this.type_InternalStructure = getDexClass(InternalDataStructure.class, hierarchy, renamer);
        this.method_InternalStructure_GetTaint = renameUnique(findInstanceMethodByName(type_InternalStructure, "getTaint"));
        this.method_InternalStructure_SetTaint = renameUnique(findInstanceMethodByName(type_InternalStructure, "setTaint"));

        // Taint types
        this.type_Taint = getDexClass(Taint.class, hierarchy, renamer);
        this.arraytype_Taint = DexArrayType.parse("[" + type_Taint.getClassDef().getType().getDescriptor(), hierarchy.getTypeCache()); 
        this.method_Taint_Get = findInstanceMethodByName(type_Taint, "get");
        this.method_Taint_Set = findInstanceMethodByName(type_Taint, "set");
        this.method_Taint_GetExternal = findInstanceMethodByName(type_Taint, "getExternal");
        this.method_Taint_SetExternal = findInstanceMethodByName(type_Taint, "setExternal");

        this.type_TaintExternal = getDexClass(TaintExternal.class, hierarchy, renamer);

        this.type_TaintInternal = getDexClass(TaintInternal.class, hierarchy, renamer);
        this.method_TaintInternal_ClearVisited = findStaticMethodByName(type_TaintInternal, "clearVisited");

        val clsTaintArray = getDexClass(TaintArray.class, hierarchy, renamer);
        this.field_TaintArray_TLength = findInstanceFieldByName(clsTaintArray, "t_length");

        this.type_TaintArrayPrimitive = getDexClass(TaintArrayPrimitive.class, hierarchy, renamer);
        this.field_TaintArrayPrimitive_TArray = findInstanceFieldByName(type_TaintArrayPrimitive, "t_array");

        this.type_TaintArrayReference = getDexClass(TaintArrayReference.class, hierarchy, renamer);
        this.field_TaintArrayReference_TArray = findInstanceFieldByName(type_TaintArrayReference, "t_array");

        // Assigner
        val clsAssigner = getDexClass(Assigner.class, hierarchy, renamer);
        this.method_Assigner_SetConstructedSuperTaint = findStaticMethodByName(clsAssigner, "setConstructedSuperTaint");
        this.method_Assigner_EraseConstructedSuperTaint = findStaticMethodByName(clsAssigner, "eraseConstructedSuperTaint");
        this.method_Assigner_DefineExternal = findStaticMethodByName(clsAssigner, "defineExternal");
        this.method_Assigner_NewExternal = findStaticMethodByName(clsAssigner, "newExternal");
        this.method_Assigner_NewExternal_Null = findStaticMethodByName(clsAssigner, "newExternal_NULL");
        this.method_Assigner_NewExternal_Undefined = findStaticMethodByName(clsAssigner, "newExternal_Undefined");
        this.method_Assigner_NewInternal_Null = findStaticMethodByName(clsAssigner, "newInternal_NULL");
        this.method_Assigner_NewInternal_Undefined = findStaticMethodByName(clsAssigner, "newInternal_Undefined");
        this.method_Assigner_NewArrayPrimitive = findStaticMethodByName(clsAssigner, "newArrayPrimitive");
        this.method_Assigner_NewArrayReference = findStaticMethodByName(clsAssigner, "newArrayReference");
        this.method_Assigner_LookupExternal = findStaticMethodByName(clsAssigner, "lookupExternal");
        this.method_Assigner_LookupInternal = findStaticMethodByName(clsAssigner, "lookupInternal");
        this.method_Assigner_LookupUndecidable = findStaticMethodByName(clsAssigner, "lookupUndecidable");
        this.method_Assigner_LookupArrayPrimitive = findStaticMethodByName(clsAssigner, "lookupArrayPrimitive");
        this.method_Assigner_LookupArrayReference = findStaticMethodByName(clsAssigner, "lookupArrayReference");
        
        this.type_FakeSignature = getDexClass(FakeSignature.class, hierarchy, renamer);
        this.method_FakeSignature_Clinit = findStaticMethodByName(type_FakeSignature, "<clinit>");
        this.field_FakeSignature_PackageName = findStaticFieldByName(type_FakeSignature, "PACKAGE_NAME");
        this.field_FakeSignature_Signatures = findStaticFieldByName(type_FakeSignature, "SIGNATURES");
    }

    /*
     * Renames a method so that its name is unique in the whole hierarchy,
     * and updates all INVOKE methods referencing it. Be careful that
     * the class type must match precisely, i.e. won't fix an INVOKE
     * if it is called on a child or parent of the method.
     */
    private DexMethod renameUnique(final DexMethod oldMethod) {
        final MethodDefinition oldDef = oldMethod.getMethodDef();
        final DexReferenceType classType = oldDef.getParentClass().getType();
        final DexMethodId oldMid = oldDef.getMethodId();
        String oldName = oldMid.getName();
        final DexTypeCache cache = getTypeCache();

        // find first non-conflicting method name

        String newName = Utils.generateName(oldName, "", new NameAcceptor() {
            @Override
            public boolean accept(String name) {
                return !cache.methodNameExists(name);
            }
        });

        // create new method definition

        final DexMethodId newMid = DexMethodId.parseMethodId(newName, oldMid.getPrototype(), cache);
        final MethodDefinition newDef = new MethodDefinition(oldDef, newMid);
        final DexMethod newMethod = new DexMethod(oldMethod, newDef);

        // replace the MethodId in the method definition

        oldDef.getParentClass().replaceDeclaredMethod(oldDef, newDef);

        // apply transform that renames the method in the whole Dex

        Transform renameMethod = new Transform() {

            @Override
            public DexMethod doFirst(DexMethod method) {
                if (method == oldMethod)
                    return newMethod;
                else
                    return method;
            }

            @Override
            public DexCodeElement doFirst(DexCodeElement element, DexCode code, DexMethod method) {
                if (element instanceof DexInstruction_Invoke) {

                    DexInstruction_Invoke invoke = (DexInstruction_Invoke) element;
                    if (invoke.getClassType().equals(classType) && invoke.getMethodId().equals(oldMid))
                        return new DexInstruction_Invoke(invoke, newMid);

                }

                return element;
            }
        };
        renameMethod.apply(this);

        return newMethod;
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
        throw new Error("Failed to locate an auxiliary method " + name);
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
