package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction12x;
import org.jf.dexlib.Code.Format.Instruction23x;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_BinaryOp.Arg;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_BinaryOp extends DexInstruction {
	// CAREFUL: produce /addr2 instructions if target and first
	// registers are equal; for commutative instructions,
	// check the second as well
	private final DexRegister regTarget;
	private final DexRegister regSourceA;
	private final DexRegister regSourceB;
	private final Opcode_BinaryOp insnOpcode;
	
	private DexInstruction_BinaryOp(DexRegister target, DexRegister sourceA, DexRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		regTarget = target;
		regSourceA = sourceA;
		regSourceB = sourceB;
		insnOpcode = opcode;
		// checks that the opcode is allowed as well
		insnOpcode.checkRegisterType(target, Arg.RESULT);
		insnOpcode.checkRegisterType(sourceA, Arg.SOURCE_1);
		insnOpcode.checkRegisterType(sourceB, Arg.SOURCE_2);
	}
	
	public DexInstruction_BinaryOp(DexSingleRegister target, DexSingleRegister sourceA, DexSingleRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
		this((DexRegister)target, sourceA, sourceB, opcode, hierarchy);
	}
	
	public DexInstruction_BinaryOp(DexWideRegister target, DexWideRegister sourceA, DexWideRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
		this((DexRegister)target, sourceA, sourceB, opcode, hierarchy);
	}
	
	public DexInstruction_BinaryOp(DexTaintRegister target, DexTaintRegister sourceA, DexTaintRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
		this((DexRegister)target, sourceA, sourceB, opcode, hierarchy);
	}
	
	public static DexInstruction_BinaryOp parse(Instruction insn, CodeParserState parsingState) {
		final uk.ac.cam.db538.dexter.dex.code.insn.Opcode_BinaryOp opcode = Opcode_BinaryOp.convert(insn.opcode);
		int regA;
		int regB;
		int regC;
		if (insn instanceof Instruction23x && opcode != null) {
			final org.jf.dexlib.Code.Format.Instruction23x insnBinaryOp = (Instruction23x)insn;
			regA = insnBinaryOp.getRegisterA();
			regB = insnBinaryOp.getRegisterB();
			regC = insnBinaryOp.getRegisterC();
		} else if (insn instanceof Instruction12x && opcode != null) {
			final org.jf.dexlib.Code.Format.Instruction12x insnBinaryOp2addr = (Instruction12x)insn;
			regA = regB = insnBinaryOp2addr.getRegisterA();
			regC = insnBinaryOp2addr.getRegisterB();
		} else throw FORMAT_EXCEPTION;
		DexStandardRegister sregA;
		DexStandardRegister sregB;
		DexStandardRegister sregC;
		if (opcode.getWidthResult() == RegisterWidth.SINGLE) sregA = parsingState.getSingleRegister(regA); else sregA = parsingState.getWideRegister(regA);
		if (opcode.getWidthArgA() == RegisterWidth.SINGLE) sregB = parsingState.getSingleRegister(regB); else sregB = parsingState.getWideRegister(regB);
		if (opcode.getWidthArgB() == RegisterWidth.SINGLE) sregC = parsingState.getSingleRegister(regC); else sregC = parsingState.getWideRegister(regC);
		return new DexInstruction_BinaryOp(sregA, sregB, sregC, opcode, parsingState.getHierarchy());
	}
	
	@Override
	public String toString() {
		return insnOpcode.getAssemblyName() + " " + regTarget.toString() + ", " + regSourceA.toString() + ", " + regSourceB.toString();
	}
	
	@Override
	public void instrument() {
//    val code = getMethodCode();
//    val insnCombineTaint = new DexInstruction_BinaryOp(code, state.getTaintRegister(regTarget), state.getTaintRegister(regSourceA), state.getTaintRegister(regSourceB), Opcode_BinaryOp.OrInt);
//
//    if (insnOpcode == Opcode_BinaryOp.DivInt || insnOpcode == Opcode_BinaryOp.RemInt) {
//      val regException = new DexRegister();
//      val insnAssignTaintToException = new DexMacro_SetObjectTaint(code, regException, state.getTaintRegister(regSourceB));
//
//      code.replace(this, throwingInsn_GenerateSurroundingCatchBlock(
//                     new DexCodeElement[] { this, insnCombineTaint },
//                     new DexCodeElement[] { insnAssignTaintToException },
//                     regException));
//    } else
//      code.replace(this, new DexCodeElement[] { this, insnCombineTaint });
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTarget);
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regSourceA, regSourceB);
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	protected DexClassType[] throwsExceptions() {
		if (insnOpcode == Opcode_BinaryOp.DivInt || insnOpcode == Opcode_BinaryOp.RemInt || insnOpcode == Opcode_BinaryOp.DivLong || insnOpcode == Opcode_BinaryOp.RemLong) return this.hierarchy.getTypeCache().LIST_Error_ArithmeticException; else return null;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexRegister getRegTarget() {
		return this.regTarget;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexRegister getRegSourceA() {
		return this.regSourceA;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexRegister getRegSourceB() {
		return this.regSourceB;
	}
	
	@java.lang.SuppressWarnings("all")
	public Opcode_BinaryOp getInsnOpcode() {
		return this.insnOpcode;
	}
}