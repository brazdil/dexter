package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Instruction;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;

public class DexInstruction_Unknown extends DexInstruction {
	private final String opcode;
	
	public DexInstruction_Unknown(Instruction insn, CodeParserState parsingState) {
		super(parsingState.getHierarchy());
		opcode = insn.opcode.name();
	}
	
	@Override
	public String toString() {
		return "??? " + opcode;
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@java.lang.SuppressWarnings("all")
	public String getOpcode() {
		return this.opcode;
	}
}