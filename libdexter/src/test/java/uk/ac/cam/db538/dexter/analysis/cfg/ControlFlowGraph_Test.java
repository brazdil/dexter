package uk.ac.cam.db538.dexter.analysis.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_IfTest;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ReturnVoid;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_IfTest;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleOriginalRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;

public class ControlFlowGraph_Test {

  private DexCode createCode(DexCodeElement ... insns) {
	  return createCode(Arrays.asList(insns));
  }
	
  private DexCode createCode(List<DexCodeElement> insns) {
	  return new DexCode(new InstructionList(insns), null, null);
  }
	
  @Test
  public void testBlockRecognition_Empty() {
    ControlFlowGraph cfg = new ControlFlowGraph(createCode());
    CfgStartBlock start = cfg.getStartBlock();
    CfgExitBlock exit = cfg.getExitBlock();
    
    List<CfgBasicBlock> basicBlocks = cfg.getBasicBlocks();
    assertEquals(0, basicBlocks.size());

    assertEquals(1, start.getSuccessors().size());
    assertTrue(start.getSuccessors().contains(exit));
    assertEquals(1, exit.getPredecessors().size());
    assertTrue(exit.getPredecessors().contains(start));
  }

  @Test
  public void testBlockRecognition_SingleInsn() {
    DexCodeElement insnReturn = new DexInstruction_ReturnVoid(null);
    DexCode code = createCode(insnReturn);

    ControlFlowGraph cfg = new ControlFlowGraph(code);
    CfgStartBlock start = cfg.getStartBlock();
    CfgExitBlock exit = cfg.getExitBlock();

    // find successor of START
    assertEquals(1, start.getSuccessors().size());
    Object succ = start.getSuccessors().toArray()[0];
    assertTrue(succ instanceof CfgBasicBlock);

    // inspect block
    CfgBasicBlock block = (CfgBasicBlock) succ;
    InstructionList insns = block.getInstructions();
    assertEquals(1, insns.size());
    assertTrue(insns.get(0).equals(insnReturn));
    
    // check it points to EXIT
    assertEquals(1, block.getSuccessors().size());
    assertEquals(exit, block.getSuccessors().toArray()[0]);
  }

  @Test
  public void testBlockRecognition_MoreBlocks() {
    DexSingleRegister r0 = new DexSingleOriginalRegister(0);
    DexSingleRegister r1 = new DexSingleOriginalRegister(1);
    DexSingleRegister r2 = new DexSingleOriginalRegister(2);

    DexLabel i0 = new DexLabel(0);
    DexInstruction i1 = new DexInstruction_BinaryOp(r0, r1, r2, Opcode_BinaryOp.AddInt, null);
    DexLabel i2 = new DexLabel(0);
    DexInstruction i3 = new DexInstruction_ReturnVoid(null);
    DexInstruction i4 = new DexInstruction_IfTest(r0, r1, i0, Opcode_IfTest.eq, null);
    DexInstruction i5 = new DexInstruction_BinaryOp(r0, r1, r2, Opcode_BinaryOp.AddInt, null);
    DexInstruction i6 = new DexInstruction_IfTest(r0, r1, i2, Opcode_IfTest.eq, null);
    DexInstruction i7 = new DexInstruction_BinaryOp(r0, r1, r2, Opcode_BinaryOp.AddInt, null);
    DexInstruction i8 = new DexInstruction_ReturnVoid(null);
    
    DexCode code = createCode(i0, i1, i2, i3, i4, i5, i6, i7, i8);

    ControlFlowGraph cfg = new ControlFlowGraph(code);
    CfgStartBlock start = cfg.getStartBlock();
    CfgExitBlock exit = cfg.getExitBlock();

    assertEquals(5, cfg.getBasicBlocks().size());
    CfgBasicBlock b1 = cfg.getBasicBlocks().get(0);
    InstructionList b1Insns = b1.getInstructions();
    CfgBasicBlock b2 = cfg.getBasicBlocks().get(1);
    InstructionList b2Insns = b2.getInstructions();
    CfgBasicBlock b3 = cfg.getBasicBlocks().get(2);
    InstructionList b3Insns = b3.getInstructions();
    CfgBasicBlock b4 = cfg.getBasicBlocks().get(3);
    InstructionList b4Insns = b4.getInstructions();
    CfgBasicBlock b5 = cfg.getBasicBlocks().get(4);
    InstructionList b5Insns = b5.getInstructions();

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
