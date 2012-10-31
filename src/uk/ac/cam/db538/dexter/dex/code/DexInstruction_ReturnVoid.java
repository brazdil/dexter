package uk.ac.cam.db538.dexter.dex.code;

public class DexInstruction_ReturnVoid extends DexInstruction {

  @Override
  public String getOriginalAssembly() {
    return "return-void";
  }

  @Override
  public DexInstruction[] instrument(TaintRegisterMap mapping) {
    return new DexInstruction[] { this };
  }
}