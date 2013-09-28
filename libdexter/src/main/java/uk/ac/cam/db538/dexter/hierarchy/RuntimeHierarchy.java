package uk.ac.cam.db538.dexter.hierarchy;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import uk.ac.cam.db538.dexter.dex.type.DexArrayType;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;

public class RuntimeHierarchy {

    @Getter private final DexTypeCache typeCache;
    private final Map<DexClassType, BaseClassDefinition> definedClasses;
    private final Map<DexClassType, BaseClassDefinition> unresolvedClassList;
    private final Map<DexClassType, BaseClassDefinition> unresolvedInterfaceList;
    @Getter private final ClassDefinition root;

    public RuntimeHierarchy(Map<DexClassType, BaseClassDefinition> definedClasses, 
            HashMap<DexClassType, BaseClassDefinition> unresolvedClassList, 
            HashMap<DexClassType, BaseClassDefinition> unresolvedInterfaceList, 
            ClassDefinition root, DexTypeCache typeCache) {
        this.definedClasses = definedClasses;
        this.root = root;
        this.typeCache = typeCache;
        this.unresolvedClassList = unresolvedClassList;
        this.unresolvedInterfaceList = unresolvedInterfaceList;
    }

    private UnresolvedClassDefinition createUnresolvedClass(DexClassType clsType) {
        UnresolvedClassDefinition result = new UnresolvedClassDefinition(clsType);
        result.setSuperclassLink(root);
        return result;
    }
    
    private UnresolvedInterfaceDefinition createUnresolvedInterface(DexClassType clsType) {
        UnresolvedInterfaceDefinition result = new UnresolvedInterfaceDefinition(clsType);
        result.setSuperclassLink(root);
        return result;
    }
    
    private BaseClassDefinition getBaseInterfaceDefinition(DexReferenceType refType) {
        return getBaseClassDefinition(refType, true);
    }
    
    public BaseClassDefinition getBaseClassDefinition(DexReferenceType refType) {
        return getBaseClassDefinition(refType, false);
    }
    
    private BaseClassDefinition getBaseClassDefinition(DexReferenceType refType, boolean isInterface) {
        if (refType instanceof DexClassType) {
            DexClassType clsType = (DexClassType)refType;
            BaseClassDefinition result = definedClasses.get(clsType);
            if (result == null) {
                if (isInterface) {
                    if (unresolvedInterfaceList.containsKey(clsType))
                        result = unresolvedInterfaceList.get(clsType);
                    else
                        result = createUnresolvedInterface(clsType);
                    System.err.println("Access unresolved interface " + clsType.getPrettyName());
                } else {
                    if (unresolvedClassList.containsKey(clsType)) 
                        result = unresolvedClassList.get(clsType);
                    else
                        result = createUnresolvedClass(clsType);
                    System.err.println("Access unresolved class " + clsType.getPrettyName());
                }
            }
            if (result == null)
                throw new NoClassDefFoundError("Cannot find " + refType.getPrettyName());
            else
                return result;
        } else if (refType instanceof DexArrayType)
            return root;
        else
            throw new Error();
    }

    public ClassDefinition getClassDefinition(DexReferenceType refType) {
        val baseClass = getBaseClassDefinition(refType);
        if (baseClass instanceof ClassDefinition)
            return (ClassDefinition) baseClass;
        else
            throw new HierarchyException("Type " + refType.getPrettyName() + " is not a proper class");
    }

    public InterfaceDefinition getInterfaceDefinition(DexReferenceType refType) {
        val baseClass = getBaseInterfaceDefinition(refType);
        if (baseClass instanceof InterfaceDefinition)
            return (InterfaceDefinition) baseClass;
        else
            throw new HierarchyException("Type " + refType.getPrettyName() + " is not an interface class");
    }

    public static enum TypeClassification {
        PRIMITIVE,
        REF_INTERNAL,
        REF_EXTERNAL,
        REF_UNDECIDABLE,
        ARRAY_PRIMITIVE,
        ARRAY_REFERENCE
    }

    public TypeClassification classifyType(DexRegisterType type) {
        if (type instanceof DexPrimitiveType)
            return TypeClassification.PRIMITIVE;

        else if (type instanceof DexArrayType) {
            if (((DexArrayType) type).getElementType() instanceof DexPrimitiveType)
                return TypeClassification.ARRAY_PRIMITIVE;
            else
                return TypeClassification.ARRAY_REFERENCE;
        }

        else {
            val classDef = getBaseClassDefinition((DexReferenceType) type);
            
            if (classDef.isInternal() && !classDef.isAnnotation())
                return TypeClassification.REF_INTERNAL;
            else if (classDef.hasInternalNonAbstractChildren())
                return TypeClassification.REF_UNDECIDABLE;
            else
                return TypeClassification.REF_EXTERNAL;
        }
    }
}
