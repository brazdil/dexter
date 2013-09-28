package uk.ac.cam.db538.dexter.hierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;

public class InterfaceDefinition extends BaseClassDefinition {

    private static final long serialVersionUID = 1L;

    final List<BaseClassDefinition> _implementors;
    @Getter private final List<BaseClassDefinition> implementors;

    public InterfaceDefinition(DexClassType classType, int accessFlags, boolean isInternal) {
        super(classType, accessFlags, isInternal);

        this._implementors = new ArrayList<BaseClassDefinition>();
        this.implementors = Collections.unmodifiableList(this._implementors);

        assert this.isInterface();
    }

    @Override
    boolean hasInternalNonAbstractChildren() {
        if (super.hasInternalNonAbstractChildren())
            return true;

        for (BaseClassDefinition implementor : implementors)
            if (implementor.hasInternalNonAbstractChildren())
                return true;

        return false;
    }
}
