package uk.ac.cam.db538.dexter.dex.code.insn;

public class InstructionOffsetException extends RuntimeException {
	private static final long serialVersionUID = -5210852859056051200L;
	private DexInstruction problematicInstruction;
	
	public InstructionOffsetException(DexInstruction insn) {
		
		this.problematicInstruction = insn;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexInstruction getProblematicInstruction() {
		return this.problematicInstruction;
	}
}