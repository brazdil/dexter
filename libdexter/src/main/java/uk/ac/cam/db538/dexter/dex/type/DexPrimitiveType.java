package uk.ac.cam.db538.dexter.dex.type;

import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;

public abstract class DexPrimitiveType extends DexRegisterType {

    private static final long serialVersionUID = 1L;

    protected DexPrimitiveType() { }

    public StaticFieldDefinition getPrimitiveClassConstantField(RuntimeHierarchy hierarchy) {
        DexTypeCache cache = hierarchy.getTypeCache();
        DexClassType typePrimitiveClass = getPrimitiveClass(cache);
        DexClassType typeClass = DexClassType.parse("Ljava/lang/Class;", cache);
        BaseClassDefinition classDef = hierarchy.getBaseClassDefinition(typePrimitiveClass);
        DexFieldId fieldId = DexFieldId.parseFieldId("TYPE", typeClass, cache);

        return classDef.getStaticField(fieldId);
    }

    protected abstract DexClassType getPrimitiveClass(DexTypeCache cache);

    public static DexPrimitiveType parse(String typeDescriptor, DexTypeCache cache) {
        // try parsing the descriptor as a primitive of each given type

        try {
            return DexBoolean.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexByte.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexChar.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexDouble.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexFloat.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexInteger.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexLong.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        try {
            return DexShort.parse(typeDescriptor, cache);
        } catch (UnknownTypeException e) { }

        throw new UnknownTypeException(typeDescriptor);
    }

    public static String jvm2dalvik(String jvmName) {
        // try parsing the descriptor as a primitive of each given type

        try {
            return DexBoolean.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexByte.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexChar.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexDouble.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexFloat.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexInteger.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexLong.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        try {
            return DexShort.jvm2dalvik(jvmName);
        } catch (UnknownTypeException e) { }

        throw new UnknownTypeException(jvmName);
    }
}
