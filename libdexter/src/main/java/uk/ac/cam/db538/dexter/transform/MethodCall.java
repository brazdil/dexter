package uk.ac.cam.db538.dexter.transform;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;

public class MethodCall extends DexCodeElement {

	private final DexInstruction_Invoke insnInvoke;
	private final DexInstruction_MoveResult insnResult;
	
	public MethodCall(DexInstruction_Invoke invoke, DexInstruction_MoveResult result) {
		this.insnInvoke = invoke;
		this.insnResult = result;
	}
	
	public boolean hasResult() {
		return insnResult != null;
	}
	
	public DexInstruction_Invoke getInvoke() {
		return insnInvoke;
	}

	public DexInstruction_MoveResult getResult() {
		return insnResult;
	}
	
	public DexCodeElement expand() {
		if (hasResult())
			return new DexMacro(insnInvoke, insnResult);
		else
			return insnInvoke;
	}
	
	public MethodCall clone() {
		return new MethodCall(
			new DexInstruction_Invoke(insnInvoke),
			insnResult == null ? null : new DexInstruction_MoveResult(insnResult));
	}
}
