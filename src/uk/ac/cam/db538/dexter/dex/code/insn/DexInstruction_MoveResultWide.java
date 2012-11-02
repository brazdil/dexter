package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11x;

import uk.ac.cam.db538.dexter.dex.code.DexRegister;

import lombok.Getter;
import lombok.val;

public class DexInstruction_MoveResultWide extends DexInstruction {

  @Getter private final DexRegister RegTo1;
  @Getter private final DexRegister RegTo2;

  public DexInstruction_MoveResultWide(DexRegister to1, DexRegister to2) {
    RegTo1 = to1;
    RegTo2 = to2;
  }

  public DexInstruction_MoveResultWide(Instruction insn, ParsingState parsingState) throws InstructionParsingException {
    if (insn instanceof Instruction11x && insn.opcode == Opcode.MOVE_RESULT_WIDE) {

      val insnMove = (Instruction11x) insn;
      RegTo1 = parsingState.getRegister(insnMove.getRegisterA());
      RegTo2 = parsingState.getRegister(insnMove.getRegisterA() + 1);

    } else
      throw new InstructionParsingException("Unknown instruction format or opcode");
  }

  @Override
  public String getOriginalAssembly() {
    return "move-result-wide v" + RegTo1.getId();
  }
}