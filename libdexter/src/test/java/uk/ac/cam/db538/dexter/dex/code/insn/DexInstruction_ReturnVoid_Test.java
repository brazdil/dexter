package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction10x;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_ReturnVoid_Test extends HierarchyTest {

    @Test
    public void testParse() {
        Utils.parseAndCompare(
            new Instruction10x(Opcode.RETURN_VOID),
            "return-void",
            this.hierarchy);
    }
}
