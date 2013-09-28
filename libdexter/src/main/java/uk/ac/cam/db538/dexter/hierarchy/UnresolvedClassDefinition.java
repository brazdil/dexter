package uk.ac.cam.db538.dexter.hierarchy;

import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;

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
    
    public InstanceFieldDefinition getAccessedInstanceField(DexFieldId fieldId) {
        InstanceFieldDefinition result = super.getAccessedInstanceField(fieldId);
        if (result != null)
            return result;
        
        result = new InstanceFieldDefinition(this, fieldId, 0);
        addDeclaredInstanceField(result);
        return result;
    }
    
    public CallDestinationType getMethodDestinationType(DexMethodId methodId, Opcode_Invoke opcode) {
        return CallDestinationType.External;
    }    
}
