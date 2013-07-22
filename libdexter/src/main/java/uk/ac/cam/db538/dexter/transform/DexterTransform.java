package uk.ac.cam.db538.dexter.transform;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;

public class DexterTransform extends Transform {

	public DexterTransform() { }

	public DexterTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	@Override
	public DexCodeElement doFirst(DexCodeElement element) {
		if (element instanceof DexInstruction_Const)
			return instrument_Const((DexInstruction_Const) element);
		else
			return element;
			
	}

	private DexCodeElement instrument_Const(DexInstruction_Const insn) {
		return new DexMacro(
				new DexInstruction_Const(
					insn.getRegTo().getTaintRegister(),
					0L,
					insn.getHierarchy()),
				insn);
	}
}
