package uk.ac.cam.db538.dexter.transform;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryEnd;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleAuxiliaryRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.transform.TryBlockSplitter.TryBlockInfo;
import uk.ac.cam.db538.dexter.utils.Pair;

public class TryBlockSplitter_Test {

    private static int ID = 0;
    private static Random RAND = new Random();

    private DexTryStart genTryBlock() {
        DexTryEnd end = new DexTryEnd(ID++);
        DexTryStart start = new DexTryStart(end, null, null);
        return start;
    }

    private DexSingleRegister genRegAux() {
        return new DexSingleAuxiliaryRegister(ID++);
    }

    private DexCodeElement genConst(DexSingleRegister reg) {
        return new DexInstruction_Const(reg, RAND.nextInt(), null);
    }

    @Test()
    public void testCheckBlocksValid_Valid() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    genConst(regAux),
                    block1.getEndMarker(),
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block3,
                    genConst(regAux),
                    block3.getEndMarker(),
                    genConst(regAux),
                    block2.getEndMarker()));

        TryBlockSplitter.checkBlocksValid(TryBlockSplitter.getAllTryBlocks(insns));;
    }

    @Test(expected=AssertionError.class)
    public void testCheckBlocksValid_EndBeforeStart() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1.getEndMarker(),
                    genConst(regAux),
                    block1,
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block3,
                    genConst(regAux),
                    block3.getEndMarker(),
                    genConst(regAux),
                    block2.getEndMarker()));

        TryBlockSplitter.checkBlocksValid(TryBlockSplitter.getAllTryBlocks(insns));;
    }

    @Test(expected=AssertionError.class)
    public void testCheckBlocksValid_Overlapping() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    genConst(regAux),
                    block1.getEndMarker(),
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block3,
                    genConst(regAux),
                    block2.getEndMarker(),
                    genConst(regAux),
                    block3.getEndMarker()));

        TryBlockSplitter.checkBlocksValid(TryBlockSplitter.getAllTryBlocks(insns));;
    }

    private Pair<TryBlockInfo, TryBlockInfo> getNestedBlocks(InstructionList insns) {
        return TryBlockSplitter.getNestedBlocks(TryBlockSplitter.getAllTryBlocks(insns));
    }

    @Test()
    public void testGetNestedBlocks_None() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    genConst(regAux),
                    block1.getEndMarker(),
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block2.getEndMarker(),
                    genConst(regAux),
                    block3,
                    genConst(regAux),
                    block3.getEndMarker()));

        assertNull(getNestedBlocks(insns));
    }

    @Test()
    public void testGetNestedBlocks_One() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    genConst(regAux),
                    block1.getEndMarker(),
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block3,
                    genConst(regAux),
                    block3.getEndMarker(),
                    block2.getEndMarker()));

        Pair<TryBlockInfo, TryBlockInfo> nestedBlocks = getNestedBlocks(insns);

        assertEquals(block2, nestedBlocks.getValA().getValA());
        assertEquals(block3, nestedBlocks.getValB().getValA());
    }

    @Test()
    public void testGetNestedBlocks_Two() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();
        DexTryStart block4 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    block4,
                    genConst(regAux),
                    block4.getEndMarker(),
                    block1.getEndMarker(),
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block3,
                    genConst(regAux),
                    block3.getEndMarker(),
                    block2.getEndMarker()));

        Pair<TryBlockInfo, TryBlockInfo> nestedBlocks = getNestedBlocks(insns);

        // In this case, we assume it will return the first pair

        assertEquals(block1, nestedBlocks.getValA().getValA());
        assertEquals(block4, nestedBlocks.getValB().getValA());
    }

    @Test()
    public void testGetNestedBlocks_Multilevel() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();
        DexTryStart block3 = genTryBlock();
        DexTryStart block4 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    genConst(regAux),
                    block1.getEndMarker(),
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block3,
                    block4,
                    genConst(regAux),
                    block4.getEndMarker(),
                    block3.getEndMarker(),
                    block2.getEndMarker()));

        Pair<TryBlockInfo, TryBlockInfo> nestedBlocks = getNestedBlocks(insns);

        // It must return the inner-most pair

        assertEquals(block3, nestedBlocks.getValA().getValA());
        assertEquals(block4, nestedBlocks.getValB().getValA());
    }

    @Test()
    public void testFixNestedBlocks() {
        DexSingleRegister regAux = genRegAux();
        DexTryStart block1 = genTryBlock();
        DexTryStart block2 = genTryBlock();

        InstructionList insns = new InstructionList(Arrays.asList(
                    block1,
                    genConst(regAux),
                    block2,
                    genConst(regAux),
                    block2.getEndMarker(),
                    genConst(regAux),
                    block1.getEndMarker()));

        Pair<TryBlockInfo, TryBlockInfo> nestedBlocks = getNestedBlocks(insns);
        insns = TryBlockSplitter.fixNestedBlocks(insns, nestedBlocks.getValA(), nestedBlocks.getValB());

        assertEquals(9, insns.size());

        DexTryStart B1A_Start = (DexTryStart) insns.get(0);
        DexTryEnd B1A_End = (DexTryEnd) insns.get(2);
        DexTryStart B2_Start = (DexTryStart) insns.get(3);
        DexTryEnd B2_End = (DexTryEnd) insns.get(5);
        DexTryStart B1B_Start = (DexTryStart) insns.get(6);
        DexTryEnd B1B_End = (DexTryEnd) insns.get(8);

        assertEquals(B1A_End, B1A_Start.getEndMarker());
        assertEquals(B1B_End, B1B_Start.getEndMarker());
        assertEquals(B2_End, B2_Start.getEndMarker());
    }
}
