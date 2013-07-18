package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction12x;
import org.jf.dexlib.Code.Format.Instruction23x;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_BinaryOp_Test extends HierarchyTest {

  @Test
  public void testParse_BinaryOp() throws InstructionParseError {
    Utils.parseAndCompare(
      new Instruction[] {
        new Instruction23x(Opcode.ADD_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SUB_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.MUL_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.DIV_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.REM_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.AND_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.OR_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.XOR_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SHL_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SHR_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.USHR_INT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.ADD_FLOAT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SUB_FLOAT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.MUL_FLOAT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.DIV_FLOAT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.REM_FLOAT, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.ADD_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SUB_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.MUL_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.DIV_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.REM_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.AND_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.OR_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.XOR_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SHL_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SHR_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.USHR_LONG, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.ADD_DOUBLE, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.SUB_DOUBLE, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.MUL_DOUBLE, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.DIV_DOUBLE, (short) 234, (short) 235, (short) 236),
        new Instruction23x(Opcode.REM_DOUBLE, (short) 234, (short) 235, (short) 236)
      }, new String[] {
        "add-int v234, v235, v236",
        "sub-int v234, v235, v236",
        "mul-int v234, v235, v236",
        "div-int v234, v235, v236",
        "rem-int v234, v235, v236",
        "and-int v234, v235, v236",
        "or-int v234, v235, v236",
        "xor-int v234, v235, v236",
        "shl-int v234, v235, v236",
        "shr-int v234, v235, v236",
        "ushr-int v234, v235, v236",
        "add-float v234, v235, v236",
        "sub-float v234, v235, v236",
        "mul-float v234, v235, v236",
        "div-float v234, v235, v236",
        "rem-float v234, v235, v236",
        "add-long v234|v235, v235|v236, v236|v237",
        "sub-long v234|v235, v235|v236, v236|v237",
        "mul-long v234|v235, v235|v236, v236|v237",
        "div-long v234|v235, v235|v236, v236|v237",
        "rem-long v234|v235, v235|v236, v236|v237",
        "and-long v234|v235, v235|v236, v236|v237",
        "or-long v234|v235, v235|v236, v236|v237",
        "xor-long v234|v235, v235|v236, v236|v237",
        "shl-long v234|v235, v235|v236, v236|v237",
        "shr-long v234|v235, v235|v236, v236|v237",
        "ushr-long v234|v235, v235|v236, v236|v237",
        "add-double v234|v235, v235|v236, v236|v237",
        "sub-double v234|v235, v235|v236, v236|v237",
        "mul-double v234|v235, v235|v236, v236|v237",
        "div-double v234|v235, v235|v236, v236|v237",
        "rem-double v234|v235, v235|v236, v236|v237"
      },
      this.hierarchy);
  }

  @Test
  public void testParse_BinaryOp2addr() throws InstructionParseError {
    Utils.parseAndCompare(
      new Instruction[] {
        new Instruction12x(Opcode.ADD_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.SUB_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.MUL_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.DIV_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.REM_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.AND_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.OR_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.XOR_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.SHL_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.SHR_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.USHR_INT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.ADD_FLOAT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.SUB_FLOAT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.MUL_FLOAT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.DIV_FLOAT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.REM_FLOAT_2ADDR, (byte) 2, (byte) 10),
        new Instruction12x(Opcode.ADD_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.SUB_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.MUL_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.DIV_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.REM_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.AND_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.OR_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.XOR_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.SHL_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.SHR_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.USHR_LONG_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.ADD_DOUBLE_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.SUB_DOUBLE_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.MUL_DOUBLE_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.DIV_DOUBLE_2ADDR, (byte) 4, (byte) 14),
        new Instruction12x(Opcode.REM_DOUBLE_2ADDR, (byte) 4, (byte) 14)
      }, new String[] {
        "add-int v2, v2, v10",
        "sub-int v2, v2, v10",
        "mul-int v2, v2, v10",
        "div-int v2, v2, v10",
        "rem-int v2, v2, v10",
        "and-int v2, v2, v10",
        "or-int v2, v2, v10",
        "xor-int v2, v2, v10",
        "shl-int v2, v2, v10",
        "shr-int v2, v2, v10",
        "ushr-int v2, v2, v10",
        "add-float v2, v2, v10",
        "sub-float v2, v2, v10",
        "mul-float v2, v2, v10",
        "div-float v2, v2, v10",
        "rem-float v2, v2, v10",
        "add-long v4|v5, v4|v5, v14|v15",
        "sub-long v4|v5, v4|v5, v14|v15",
        "mul-long v4|v5, v4|v5, v14|v15",
        "div-long v4|v5, v4|v5, v14|v15",
        "rem-long v4|v5, v4|v5, v14|v15",
        "and-long v4|v5, v4|v5, v14|v15",
        "or-long v4|v5, v4|v5, v14|v15",
        "xor-long v4|v5, v4|v5, v14|v15",
        "shl-long v4|v5, v4|v5, v14|v15",
        "shr-long v4|v5, v4|v5, v14|v15",
        "ushr-long v4|v5, v4|v5, v14|v15",
        "add-double v4|v5, v4|v5, v14|v15",
        "sub-double v4|v5, v4|v5, v14|v15",
        "mul-double v4|v5, v4|v5, v14|v15",
        "div-double v4|v5, v4|v5, v14|v15",
        "rem-double v4|v5, v4|v5, v14|v15"
      },
      this.hierarchy);
  }
}
