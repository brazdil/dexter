package uk.ac.cam.db538.dexter.hierarchy;

import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;

public class UnresolvedClassDefinition extends ClassDefinition {

    private static final long serialVersionUID = 1L;

    public UnresolvedClassDefinition(DexClassType type) {
        super(type, 0, false);
    }

    public StaticFieldDefinition getAccessedStaticField(DexFieldId fieldId) {
        StaticFieldDefinition result = super.getAccessedStaticField(fieldId);
        if (result != null)
            return result;
        
        result = new StaticFieldDefinition(this, fieldId, AccessFlags.STATIC.getValue());
        addDeclaredStaticField(result);
        return result;
    }
}
