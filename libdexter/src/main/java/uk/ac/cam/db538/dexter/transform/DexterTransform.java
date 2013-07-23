package uk.ac.cam.db538.dexter.transform;

import java.util.Map;

import lombok.val;
import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;

public class DexterTransform extends Transform {

	public DexterTransform() { }

	public DexterTransform(ProgressCallback progressCallback) {
		super(progressCallback);
	}

	private Map<DexInstruction_Invoke, CallDestinationType> methodCallClassification = null;
	
	@Override
	public DexCode doFirst(DexCode code) {
		code = super.doFirst(code);
		
		val classification = MethodCallClassifier.classifyMethodCalls(code);
		methodCallClassification = classification.getValB();
		code = classification.getValA();
		
		return code;
	}

	@Override
	public DexCodeElement doFirst(DexCodeElement element) {
		if (element instanceof DexInstruction_Const)
			return instrument_Const((DexInstruction_Const) element);
		else
			return element;
	}

	@Override
	public DexCode doLast(DexCode code) {
		methodCallClassification = null;
		return super.doLast(code);
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
