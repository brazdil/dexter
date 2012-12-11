package uk.ac.cam.db538.dexter.dex.code.elem;

import java.util.Map;

import lombok.Getter;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.utils.Cache;

public class DexLabel extends DexCodeElement {

  @Getter private final long originalAbsoluteOffset;

  public DexLabel(DexCode methodCode, long originalAbsoluteOffset) {
    super(methodCode);

    this.originalAbsoluteOffset = originalAbsoluteOffset;
  }

  public DexLabel(DexCode methodCode) {
    super(methodCode);

    this.originalAbsoluteOffset = -1;
  }

  @Override
  public String getOriginalAssembly() {
    if (originalAbsoluteOffset >= 0)
      return "L" + originalAbsoluteOffset + ":";
    else
      return "L???:";
  }

  @Override
  public boolean cfgStartsBasicBlock() {
    return true;
  }

  public static Cache<Long, DexLabel> createCache(final DexCode code) {
    return new Cache<Long, DexLabel>() {
      @Override
      protected DexLabel createNewEntry(Long absoluteOffset) {
        return new DexLabel(code, absoluteOffset);
      }
    };
  }

  @Override
  protected DexCodeElement gcReplaceWithTemporaries(Map<DexRegister, DexRegister> mapping) {
    return this;
  }
}