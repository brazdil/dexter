package uk.ac.cam.db538.dexter.analysis.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_IfTest;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ReturnVoid;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_IfTest;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleOriginalRegister;

public class ControlFlowGraph_Test {

  private DexCode createCode(DexCodeElement ... insns) {
	  return createCode(Arrays.asList(insns));
  }
	
  private DexCode createCode(List<DexCodeElement> insns) {
	  return new DexCode(new InstructionList(insns), null, null);
  }
	
  @Test
  public void testBlockRecognition_Empty() {
    val cfg = new ControlFlowGraph(createCode());
    val start = cfg.getStartBlock();
    val exit = cfg.getExitBlock();
    
    val basicBlocks = cfg.getBasicBlocks();
    assertEquals(0, basicBlocks.size());

    assertEquals(1, start.getSuccessors().size());
    assertTrue(start.getSuccessors().contains(exit));
    assertEquals(1, exit.getPredecessors().size());
    assertTrue(exit.getPredecessors().contains(start));
  }

  @Test
  public void testBlockRecognition_SingleInsn() {
    val insnReturn = new DexInstruction_ReturnVoid(null);
    val code = createCode(insnReturn);

    val cfg = new ControlFlowGraph(code);
    val start = cfg.getStartBlock();
    val exit = cfg.getExitBlock();

    // find successor of START
    assertEquals(1, start.getSuccessors().size());
    val succ = start.getSuccessors().toArray()[0];
    assertTrue(succ instanceof CfgBasicBlock);

    // inspect block
    val block = (CfgBasicBlock) succ;
    val insns = block.getInstructions();
    assertEquals(1, insns.size());
    assertTrue(insns.get(0).equals(insnReturn));
    
    // check it points to EXIT
    assertEquals(1, block.getSuccessors().size());
    assertEquals(exit, block.getSuccessors().toArray()[0]);
  }

  @Test
  public void testBlockRecognition_MoreBlocks() {
    val r0 = new DexSingleOriginalRegister(0);
    val r1 = new DexSingleOriginalRegister(1);
    val r2 = new DexSingleOriginalRegister(2);

    val i0 = new DexLabel(0);
    val i1 = new DexInstruction_BinaryOp(r0, r1, r2, Opcode_BinaryOp.AddInt, null);
    val i2 = new DexLabel(0);
    val i3 = new DexInstruction_ReturnVoid(null);
    val i4 = new DexInstruction_IfTest(r0, r1, i0, Opcode_IfTest.eq, null);
    val i5 = new DexInstruction_BinaryOp(r0, r1, r2, Opcode_BinaryOp.AddInt, null);
    val i6 = new DexInstruction_IfTest(r0, r1, i2, Opcode_IfTest.eq, null);
    val i7 = new DexInstruction_BinaryOp(r0, r1, r2, Opcode_BinaryOp.AddInt, null);
    val i8 = new DexInstruction_ReturnVoid(null);
    
    val code = createCode(i0, i1, i2, i3, i4, i5, i6, i7, i8);

    val cfg = new ControlFlowGraph(code);
    val start = cfg.getStartBlock();
    val exit = cfg.getExitBlock();

    assertEquals(5, cfg.getBasicBlocks().size());
    val b1 = cfg.getBasicBlocks().get(0);
    val b1Insns = b1.getInstructions();
    val b2 = cfg.getBasicBlocks().get(1);
    val b2Insns = b2.getInstructions();
    val b3 = cfg.getBasicBlocks().get(2);
    val b3Insns = b3.getInstructions();
    val b4 = cfg.getBasicBlocks().get(3);
    val b4Insns = b4.getInstructions();
    val b5 = cfg.getBasicBlocks().get(4);
    val b5Insns = b5.getInstructions();

    // find successor of START
    // check that it contains only DexCodeStart
    assertEquals(1, start.getSuccessors().size());
    assertTrue(start.getSuccessors().contains(b1));
    
    // first block contains i0, i1
    assertEquals(2, b1Insns.size());
    assertEquals(i0, b1Insns.get(0));
    assertEquals(i1, b1Insns.get(1));
    assertEquals(1, b1.getSuccessors().size());
    assertTrue(b1.getSuccessors().contains(b2));

    // second block directly after the first one
    assertEquals(2, b2Insns.size());
    assertEquals(i2, b2Insns.get(0));
    assertEquals(i3, b2Insns.get(1));
    assertEquals(1, b2.getSuccessors().size());
    assertTrue(b2.getSuccessors().contains(exit));

    // third block doesn't have a predecessor
    assertEquals(0, b3.getPredecessors().size());
    assertEquals(1, b3Insns.size());
    assertEquals(i4, b3Insns.get(0));
    assertEquals(2, b3.getSuccessors().size());
    assertTrue(b3.getSuccessors().contains(b4));
    assertTrue(b3.getSuccessors().contains(b1));

    // fourth block is pretty normal
    assertEquals(1, b4.getPredecessors().size());
    assertEquals(2, b4Insns.size());
    assertEquals(i5, b4Insns.get(0));
    assertEquals(i6, b4Insns.get(1));
    assertEquals(2, b4.getSuccessors().size());
    assertTrue(b4.getSuccessors().contains(b5));
    assertTrue(b4.getSuccessors().contains(b2));

    // fifth block is connected to EXIT
    assertEquals(1, b5.getPredecessors().size());
    assertEquals(2, b5Insns.size());
    assertEquals(i7, b5Insns.get(0));
    assertEquals(i8, b5Insns.get(1));
    assertEquals(1, b5.getSuccessors().size());
    assertTrue(b5.getSuccessors().contains(exit));
  }
}
