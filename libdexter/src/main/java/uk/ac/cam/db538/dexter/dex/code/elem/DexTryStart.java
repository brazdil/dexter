package uk.ac.cam.db538.dexter.dex.code.elem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

public class DexTryStart extends DexCodeElement {

    @Getter private final DexTryEnd endMarker;
    @Getter private final List<DexCatch> catchHandlers;
    @Getter private final DexCatchAll catchAllHandler;

    public DexTryStart(DexTryEnd endMarker, DexCatchAll catchAllHandler, List<DexCatch> catchHandlers) {
        this.endMarker = endMarker;
        this.catchAllHandler = catchAllHandler;
        if (catchHandlers == null)
            this.catchHandlers = Collections.emptyList();
        else
            this.catchHandlers = Collections.unmodifiableList(new ArrayList<DexCatch>(catchHandlers));
    }

    public DexTryStart(DexTryStart toClone, DexTryEnd newEnd) {
        this(newEnd, toClone.catchAllHandler, toClone.catchHandlers);
    }

    @Override
    public String toString() {
        return "TRYSTART" + Integer.toString(this.endMarker.getId());
    }

    @Override
    public boolean cfgStartsBasicBlock() {
        return true;
    }
}
