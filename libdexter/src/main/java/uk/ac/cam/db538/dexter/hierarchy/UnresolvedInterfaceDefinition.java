package uk.ac.cam.db538.dexter.hierarchy;

import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.type.DexClassType;

public class UnresolvedInterfaceDefinition extends InterfaceDefinition {

    private static final long serialVersionUID = 1L;

    public UnresolvedInterfaceDefinition(DexClassType type) {
        super(type, AccessFlags.INTERFACE.getValue(), false);
    }

}
