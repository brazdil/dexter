package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11n;
import org.jf.dexlib.Code.Format.Instruction21h;
import org.jf.dexlib.Code.Format.Instruction21s;
import org.jf.dexlib.Code.Format.Instruction31i;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_Const_Test extends HierarchyTest {

  @Test
  public void testParse_Const4() {
    Utils.parseAndCompare(
      new Instruction11n(Opcode.CONST_4, (byte) 13, (byte) 7),
      "const v13, #7",
      this.hierarchy);
    Utils.parseAndCompare(
      new Instruction11n(Opcode.CONST_4, (byte) 13, (byte) -8),
      "const v13, #-8",
      this.hierarchy);
  }

  @Test
  public void testParse_Const16() {
    Utils.parseAndCompare(
      new Instruction21s(Opcode.CONST_16, (short) 236, (short) 32082),
      "const v236, #32082",
      this.hierarchy);
    Utils.parseAndCompare(
      new Instruction21s(Opcode.CONST_16, (short) 236, (short) -32082),
      "const v236, #-32082",
      this.hierarchy);
  }

  @Test
  public void testParse_Const() {
    Utils.parseAndCompare(
      new Instruction31i(Opcode.CONST, (short) 237, 0x01ABCDEF),
      "const v237, #28036591",
      this.hierarchy);
    Utils.parseAndCompare(
      new Instruction31i(Opcode.CONST, (short) 237, 0xABCDEF01),
      "const v237, #-1412567295",
      this.hierarchy);
  }

  @Test
  public void testParse_ConstHigh16() {
    Utils.parseAndCompare(
      new Instruction21h(Opcode.CONST_HIGH16, (short) 238, (short)0x1234),
      "const v238, #305397760",
      this.hierarchy);
    Utils.parseAndCompare(
      new Instruction21h(Opcode.CONST_HIGH16, (short) 238, (short)0xABCD),
      "const v238, #-1412628480",
      this.hierarchy);
  }
}
