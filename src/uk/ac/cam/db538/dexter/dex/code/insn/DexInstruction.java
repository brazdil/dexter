package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.HashMap;
import java.util.Map;

import lombok.val;

import org.jf.dexlib.Code.Instruction;

import uk.ac.cam.db538.dexter.dex.DexParsingCache;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.type.UnknownTypeException;

public abstract class DexInstruction extends DexCodeElement {

  // INSTRUCTION INSTRUMENTATION

  public static class TaintRegisterMap {
    private final Map<DexRegister, DexRegister> RegisterMap;
    private final int IdOffset;

    public TaintRegisterMap(DexCode code) {
      RegisterMap = new HashMap<DexRegister, DexRegister>();

      // find the maximal register id in the code
      // this is strictly for GUI purposes
      // actual register allocation happens later
      int maxId = -1;
      for (val elem : code)
        if (elem instanceof DexInstruction)
          for (val reg : ((DexInstruction) elem).getReferencedRegisters())
            if (maxId < reg.getId())
              maxId = reg.getId();
      IdOffset = maxId + 1;
    }

    public DexRegister getTaintRegister(DexRegister reg) {
      val taintReg = RegisterMap.get(reg);
      if (taintReg == null) {
        val newReg = new DexRegister(reg.getId() + IdOffset);
        RegisterMap.put(reg, newReg);
        return newReg;
      } else
        return taintReg;
    }
  }

  public DexCodeElement[] instrument(TaintRegisterMap mapping) {
    return new DexCodeElement[] { this };
  }

  protected DexRegister[] getReferencedRegisters() {
    return new DexRegister[] { };
  }

  // INSTRUCTION PARSING

  private static DexInstruction parseInstruction(Instruction insn, ParsingState parsingState) throws InstructionParsingException, UnknownTypeException {
    switch (insn.opcode) {

    case NOP:
      return new DexInstruction_Nop(insn, parsingState);

    case MOVE:
    case MOVE_OBJECT:
    case MOVE_FROM16:
    case MOVE_OBJECT_FROM16:
    case MOVE_16:
    case MOVE_OBJECT_16:
      return new DexInstruction_Move(insn, parsingState);

    case MOVE_WIDE:
    case MOVE_WIDE_FROM16:
    case MOVE_WIDE_16:
      return new DexInstruction_MoveWide(insn, parsingState);

    case MOVE_RESULT:
    case MOVE_RESULT_OBJECT:
      return new DexInstruction_MoveResult(insn, parsingState);

    case MOVE_RESULT_WIDE:
      return new DexInstruction_MoveResultWide(insn, parsingState);

    case MOVE_EXCEPTION:
      return new DexInstruction_MoveException(insn, parsingState);

    case RETURN_VOID:
      return new DexInstruction_ReturnVoid();

    case RETURN:
    case RETURN_OBJECT:
      return new DexInstruction_Return(insn, parsingState);

    case RETURN_WIDE:
      return new DexInstruction_ReturnWide(insn, parsingState);

    case CONST_4:
    case CONST_16:
    case CONST:
    case CONST_HIGH16:
      return new DexInstruction_Const(insn, parsingState);

    case CONST_WIDE_16:
    case CONST_WIDE_32:
    case CONST_WIDE:
    case CONST_WIDE_HIGH16:
      return new DexInstruction_ConstWide(insn, parsingState);

    case CONST_STRING:
    case CONST_STRING_JUMBO:
      return new DexInstruction_ConstString(insn, parsingState);

    case CONST_CLASS:
      return new DexInstruction_ConstClass(insn, parsingState);

    case MONITOR_ENTER:
    case MONITOR_EXIT:
      return new DexInstruction_Monitor(insn, parsingState);

    case CHECK_CAST:
      return new DexInstruction_CheckCast(insn, parsingState);

    case INSTANCE_OF:
      return new DexInstruction_InstanceOf(insn, parsingState);

    case NEW_INSTANCE:
      return new DexInstruction_NewInstance(insn, parsingState);

    case NEW_ARRAY:
      return new DexInstruction_NewArray(insn, parsingState);

    case THROW:
      return new DexInstruction_Throw(insn, parsingState);

    case GOTO:
    case GOTO_16:
    case GOTO_32:
      return new DexInstruction_Goto(insn, parsingState);

    case IF_EQ:
    case IF_NE:
    case IF_LT:
    case IF_GE:
    case IF_GT:
    case IF_LE:
      return new DexInstruction_IfTest(insn, parsingState);

    case IF_EQZ:
    case IF_NEZ:
    case IF_LTZ:
    case IF_GEZ:
    case IF_GTZ:
    case IF_LEZ:
      return new DexInstruction_IfTestZero(insn, parsingState);

    case NEG_INT:
    case NOT_INT:
    case NEG_FLOAT:
      return new DexInstruction_UnaryOp(insn, parsingState);

    case NEG_LONG:
    case NOT_LONG:
    case NEG_DOUBLE:
      return new DexInstruction_UnaryOpWide(insn, parsingState);

    case INT_TO_FLOAT:
    case FLOAT_TO_INT:
    case INT_TO_BYTE:
    case INT_TO_CHAR:
    case INT_TO_SHORT:
      return new DexInstruction_Convert(insn, parsingState);

    case INT_TO_LONG:
    case INT_TO_DOUBLE:
    case FLOAT_TO_LONG:
    case FLOAT_TO_DOUBLE:
      return new DexInstruction_ConvertToWide(insn, parsingState);

    case LONG_TO_INT:
    case DOUBLE_TO_INT:
    case LONG_TO_FLOAT:
    case DOUBLE_TO_FLOAT:
      return new DexInstruction_ConvertFromWide(insn, parsingState);

    case LONG_TO_DOUBLE:
    case DOUBLE_TO_LONG:
      return new DexInstruction_ConvertWide(insn, parsingState);

    case ADD_INT:
    case SUB_INT:
    case MUL_INT:
    case DIV_INT:
    case REM_INT:
    case AND_INT:
    case OR_INT:
    case XOR_INT:
    case SHL_INT:
    case SHR_INT:
    case USHR_INT:
    case ADD_FLOAT:
    case SUB_FLOAT:
    case MUL_FLOAT:
    case DIV_FLOAT:
    case REM_FLOAT:
    case ADD_INT_2ADDR:
    case SUB_INT_2ADDR:
    case MUL_INT_2ADDR:
    case DIV_INT_2ADDR:
    case REM_INT_2ADDR:
    case AND_INT_2ADDR:
    case OR_INT_2ADDR:
    case XOR_INT_2ADDR:
    case SHL_INT_2ADDR:
    case SHR_INT_2ADDR:
    case USHR_INT_2ADDR:
    case ADD_FLOAT_2ADDR:
    case SUB_FLOAT_2ADDR:
    case MUL_FLOAT_2ADDR:
    case DIV_FLOAT_2ADDR:
    case REM_FLOAT_2ADDR:
      return new DexInstruction_BinaryOp(insn, parsingState);

    case ADD_LONG:
    case SUB_LONG:
    case MUL_LONG:
    case DIV_LONG:
    case REM_LONG:
    case AND_LONG:
    case OR_LONG:
    case XOR_LONG:
    case SHL_LONG:
    case SHR_LONG:
    case USHR_LONG:
    case ADD_DOUBLE:
    case SUB_DOUBLE:
    case MUL_DOUBLE:
    case DIV_DOUBLE:
    case REM_DOUBLE:
    case ADD_LONG_2ADDR:
    case SUB_LONG_2ADDR:
    case MUL_LONG_2ADDR:
    case DIV_LONG_2ADDR:
    case REM_LONG_2ADDR:
    case AND_LONG_2ADDR:
    case OR_LONG_2ADDR:
    case XOR_LONG_2ADDR:
    case SHL_LONG_2ADDR:
    case SHR_LONG_2ADDR:
    case USHR_LONG_2ADDR:
    case ADD_DOUBLE_2ADDR:
    case SUB_DOUBLE_2ADDR:
    case MUL_DOUBLE_2ADDR:
    case DIV_DOUBLE_2ADDR:
    case REM_DOUBLE_2ADDR:
      return new DexInstruction_BinaryOpWide(insn, parsingState);

    default:
      // TODO: throw exception
      return new DexInstruction_Unknown();
    }
  }

  public static DexCode parseMethodCode(Instruction[] instructions, DexParsingCache cache) throws UnknownTypeException, InstructionParsingException {
    // What happens here:
    // - each instruction is parsed
    //   - offset of each instruction is stored
    //   - labels created in jumping instructions are stored
    //     separately, together with desired offsets
    // - labels are placed in the right position inside
    //   the instruction list

    val parsingState = new ParsingState(cache);

    for (val insn : instructions) {
      val parsedInsn = parseInstruction(insn, parsingState);
      parsingState.addInstruction(insn.getSize(0), parsedInsn);
    }

    parsingState.placeLabels();
    return parsingState.getCode();
  }
}