package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction12x;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_UnaryOp extends DexInstruction {
	private final DexStandardRegister regTo;
	private final DexStandardRegister regFrom;
	private final Opcode_UnaryOp insnOpcode;
	
	public DexInstruction_UnaryOp(DexStandardRegister to, DexStandardRegister from, Opcode_UnaryOp opcode, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		regTo = to;
		regFrom = from;
		insnOpcode = opcode;
		insnOpcode.checkRegisterType(regTo);
		insnOpcode.checkRegisterType(regFrom);
	}
	
	public static DexInstruction_UnaryOp parse(Instruction insn, CodeParserState parsingState) {
		final uk.ac.cam.db538.dexter.dex.code.insn.Opcode_UnaryOp opcode = Opcode_UnaryOp.convert(insn.opcode);
		if (insn instanceof Instruction12x && opcode != null) {
			final org.jf.dexlib.Code.Format.Instruction12x insnUnaryOp = (Instruction12x)insn;
			DexStandardRegister regTo;
			DexStandardRegister regFrom;
			if (opcode.getWidth() == RegisterWidth.SINGLE) {
				regTo = parsingState.getSingleRegister(insnUnaryOp.getRegisterA());
				regFrom = parsingState.getSingleRegister(insnUnaryOp.getRegisterB());
			} else {
				regTo = parsingState.getWideRegister(insnUnaryOp.getRegisterA());
				regFrom = parsingState.getWideRegister(insnUnaryOp.getRegisterB());
			}
			return new DexInstruction_UnaryOp(regTo, regFrom, opcode, parsingState.getHierarchy());
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return insnOpcode.getAssemblyName() + " " + regTo.toString() + ", " + regFrom.toString();
	}
	
	@Override
	public void instrument() {
//    val code = getMethodCode();
//    code.replace(this, new DexCodeElement[] {
//                   this,
//                   new DexInstruction_Move(code, state.getTaintRegister(regTo), state.getTaintRegister(regFrom), false)
//                 });
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regFrom);
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegFrom() {
		return this.regFrom;
	}
	
	@java.lang.SuppressWarnings("all")
	public Opcode_UnaryOp getInsnOpcode() {
		return this.insnOpcode;
	}
}