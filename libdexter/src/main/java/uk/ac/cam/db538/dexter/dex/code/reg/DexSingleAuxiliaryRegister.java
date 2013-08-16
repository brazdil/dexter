package uk.ac.cam.db538.dexter.dex.code.reg;

import lombok.Getter;

public class DexSingleAuxiliaryRegister extends DexSingleRegister {

    @Getter private final int id;

    public DexSingleAuxiliaryRegister(int id) {
        this.id = id;
    }

    @Override
    String getAsmId() {
        return Integer.toString(id);
    }

    @Override
    String getAsmPrefix() {
        return "a";
    }
}
