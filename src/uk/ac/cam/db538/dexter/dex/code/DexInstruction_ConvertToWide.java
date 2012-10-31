package uk.ac.cam.db538.dexter.dex.code;

import lombok.Getter;

public class DexInstruction_ConvertToWide extends DexInstruction {

  public static enum Opcode {
    IntToLong("int-to-long"),
    IntToDouble("int-to-double"),
    FloatToLong("float-to-long"),
    FloatToDouble("float-to-double");

    @Getter private final String AssemblyName;

    private Opcode(String assemblyName) {
      AssemblyName = assemblyName;
    }

    public static Opcode convert(org.jf.dexlib.Code.Opcode opcode) {
      switch (opcode) {
      case INT_TO_LONG:
        return IntToLong;
      case INT_TO_DOUBLE:
        return IntToDouble;
      case FLOAT_TO_LONG:
        return FloatToLong;
      case FLOAT_TO_DOUBLE:
        return FloatToDouble;
      default:
        return null;
      }
    }

    public static org.jf.dexlib.Code.Opcode convert(Opcode opcode) {
      switch (opcode) {
      case IntToLong:
        return org.jf.dexlib.Code.Opcode.INT_TO_LONG;
      case IntToDouble:
        return org.jf.dexlib.Code.Opcode.INT_TO_DOUBLE;
      case FloatToLong:
        return org.jf.dexlib.Code.Opcode.FLOAT_TO_LONG;
      case FloatToDouble:
        return org.jf.dexlib.Code.Opcode.FLOAT_TO_DOUBLE;
      default:
        return null;
      }
    }
  }

  @Getter private final DexRegister RegTo1;
  @Getter private final DexRegister RegTo2;
  @Getter private final DexRegister RegFrom;
  @Getter private final Opcode InsnOpcode;

  public DexInstruction_ConvertToWide(DexRegister to1, DexRegister to2, DexRegister from, Opcode opcode) {
    RegTo1 = to1;
    RegTo2 = to2;
    RegFrom = from;
    InsnOpcode = opcode;
  }

  @Override
  public String getOriginalAssembly() {
    return InsnOpcode.getAssemblyName() + " v" + RegTo1.getId() + ", v" + RegFrom.getId();
  }

  @Override
  public DexInstruction[] instrument(TaintRegisterMap mapping) {
    return new DexInstruction[] { this };
  }
}