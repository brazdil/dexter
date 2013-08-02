package uk.ac.cam.db538.dexter.hierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.val;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;

public class ClassDefinition extends BaseClassDefinition {

	private static final long serialVersionUID = 1L;
	
	private final List<InstanceFieldDefinition> _instanceFields;
	@Getter private final List<InstanceFieldDefinition> instanceFields;

	public ClassDefinition(DexClassType classType, int accessFlags, boolean isInternal) {
		super(classType, accessFlags, isInternal);

		this._instanceFields = new ArrayList<InstanceFieldDefinition>();
		this.instanceFields = Collections.unmodifiableList(this._instanceFields);
	}
	
	public void addDeclaredInstanceField(InstanceFieldDefinition field) {
		assert !field.isStatic();
		assert field.getParentClass() == this;
		
		this._instanceFields.add(field);
	}
	
	public InstanceFieldDefinition getInstanceField(DexFieldId fieldId) {
		for (val fieldDef : this.instanceFields)
			if (fieldDef.getFieldId().equals(fieldId))
				return fieldDef;
		return null;
	}
	
	public InstanceFieldDefinition getInstanceField(String fieldName) {
		for (val fieldDef : this.instanceFields)
			if (fieldDef.getFieldId().getName().equals(fieldName))
				return fieldDef;
		return null;
	}

	public InstanceFieldDefinition getAccessedInstanceField(DexFieldId fieldId) {
		// Application can access an instance field on class X, but
		// the field might actually be defined in one of X's parents
		// This method will return the definition of the field 
		// in itself or the closest parent

		return iterateThroughParents(fieldId, extractorInstanceField, acceptorAlwaysTrue, false);
	}
	
	private static final Extractor<DexFieldId, InstanceFieldDefinition> extractorInstanceField = new Extractor<DexFieldId, InstanceFieldDefinition>() {
		@Override
		public InstanceFieldDefinition extract(BaseClassDefinition clazz, DexFieldId fieldId) {
			if (clazz instanceof ClassDefinition) {
				return ((ClassDefinition) clazz).getInstanceField(fieldId);
			} else
				return null;
		}
	};
}
