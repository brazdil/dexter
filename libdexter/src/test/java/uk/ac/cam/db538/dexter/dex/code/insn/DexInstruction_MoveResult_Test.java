package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11x;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_MoveResult_Test extends HierarchyTest {

  @Test
  public void testParse_Primitive() {
    Utils.parseAndCompare(new Instruction11x(Opcode.MOVE_RESULT, (short) 255),
                          "move-result v255",
                          this.hierarchy);
  }

  @Test
  public void testParse_Object() {
    Utils.parseAndCompare(new Instruction11x(Opcode.MOVE_RESULT_OBJECT, (short) 254),
                          "move-result-object v254",
                          this.hierarchy);
  }

  @Test
  public void testParse() {
    Utils.parseAndCompare(new Instruction11x(Opcode.MOVE_RESULT_WIDE, (short) 255),
                          "move-result-wide v255|v256",
                          this.hierarchy);
  }
}
