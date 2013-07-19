package uk.ac.cam.db538.dexter.dex.field;

import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.ClassDataItem.EncodedField;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.EncodedValue.EncodedValue;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.FieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;

public class DexStaticField extends DexField {
	private final StaticFieldDefinition fieldDef;
	private final EncodedValue initialValue;
	
	public DexStaticField(DexClass parentClass, StaticFieldDefinition fieldDef, EncodedValue initialValue) {
		super(parentClass);
		this.initialValue = initialValue;
		this.fieldDef = fieldDef;
	}
	
	public DexStaticField(DexClass parentClass, ClassDefItem classItem, EncodedField fieldItem, AnnotationDirectoryItem annoDir) {
		super(parentClass, fieldItem, annoDir);
		this.initialValue = init_ParseInitialValue(classItem, fieldItem);
		this.fieldDef = init_FindFieldDefinition(parentClass, fieldItem);
	}
	
	private static StaticFieldDefinition init_FindFieldDefinition(DexClass parentClass, EncodedField fieldItem) {
		final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = parentClass.getParentFile().getHierarchy();
		final uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition classDef = parentClass.getClassDef();
		final java.lang.String name = fieldItem.field.getFieldName().getStringValue();
		final uk.ac.cam.db538.dexter.dex.type.DexRegisterType type = DexRegisterType.parse(fieldItem.field.getFieldType().getTypeDescriptor(), hierarchy.getTypeCache());
		final uk.ac.cam.db538.dexter.dex.type.DexFieldId fieldId = DexFieldId.parseFieldId(name, type, hierarchy.getTypeCache());
		return classDef.getStaticField(fieldId);
	}
	
	private static EncodedValue init_ParseInitialValue(ClassDefItem classItem, EncodedField fieldItem) {
		// extract data
		if (classItem.getClassData() == null) return null;
		final org.jf.dexlib.EncodedArrayItem initValuesItem = classItem.getStaticFieldInitializers();
		final java.util.List<org.jf.dexlib.ClassDataItem.EncodedField> staticFields = classItem.getClassData().getStaticFields();
		if (initValuesItem == null || staticFields == null) return null;
		final org.jf.dexlib.EncodedValue.EncodedValue[] initValues = initValuesItem.getEncodedArray().values;
		// find the field in the staticFields array
		final int fieldIndex = staticFields.indexOf(fieldItem);
		if (fieldIndex < 0 || fieldIndex >= initValues.length) return null;
		// return the value
		return initValues[fieldIndex];
	}
	
	@Override
	protected FieldDefinition internal_GetFieldDef() {
		return this.fieldDef;
	}
	
	@java.lang.SuppressWarnings("all")
	public StaticFieldDefinition getFieldDef() {
		return this.fieldDef;
	}
	
	@java.lang.SuppressWarnings("all")
	public EncodedValue getInitialValue() {
		return this.initialValue;
	}
}