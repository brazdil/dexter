package uk.ac.cam.db538.dexter.transform.taint;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_ArrayGet;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Return;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;

public class TestingTaintTransform extends TaintTransform {

    public TestingTaintTransform() {
    }

    public TestingTaintTransform(ProgressCallback progressCallback) {
        super(progressCallback);
    }

    @Override
    public boolean exclude(DexClass clazz) {
        String name = clazz.getClassDef().getType().getDescriptor();
        return name.equals("Luk/ac/cam/db538/dexter/tests/TestList;");
    }

    @Override
    public DexMethod doLast(DexMethod method) {
        if (isGivenUtilsMethod(method, NAME_IS_TAINTED, PROTOTYPE_IS_TAINTED)) {
        	
            DexCode oldCode = method.getMethodBody();
            DexRegister paramReg = oldCode.getParameters().get(0).getRegister();

            List<DexCodeElement> newInstructions = new ArrayList<DexCodeElement>();
            for (DexCodeElement insn : oldCode.getInstructionList())
                if (insn instanceof DexInstruction_Return)
                    newInstructions.add(new DexInstruction_Return(paramReg.getTaintRegister(), false, oldCode.getHierarchy()));
                else
                    newInstructions.add(insn);

            DexCode newCode = new DexCode(oldCode, new InstructionList(newInstructions));
            method = new DexMethod(method, newCode);
            
        } else if (isGivenUtilsMethod(method, NAME_TAINT, PROTOTYPE_TAINT_PRIMITIVE)) {

            DexCode oldCode = method.getMethodBody();
            DexRegister paramReg = oldCode.getParameters().get(0).getRegister();
            DexRegister paramRegTaint = paramReg.getTaintRegister();
            
            List<DexCodeElement> newInstructions = new ArrayList<DexCodeElement>();
            for (DexCodeElement insn : oldCode.getInstructionList())
                if (insn instanceof DexInstruction_ArrayGet && ((DexInstruction_ArrayGet) insn).getRegTo().equals(paramRegTaint))
                    newInstructions.add(new DexInstruction_Const(paramRegTaint, 1, oldCode.getHierarchy()));
                else
                    newInstructions.add(insn);

            DexCode newCode = new DexCode(oldCode, new InstructionList(newInstructions));
            method = new DexMethod(method, newCode);
        }
        
        if (method.getMethodDef().getMethodId().getName().equals("propagate"))
        	if (method.getMethodDef().getParentClass().getType().getPrettyName().endsWith("Test_InstanceField_ArrayReference"))
        		method.getMethodBody().getInstructionList().dump();

        return super.doLast(method);
    }

    private static final String TAINTUTILS_CLASS = "Luk/ac/cam/db538/dexter/tests/TaintUtils;";
    private static final String NAME_IS_TAINTED = "isTainted";
    private static final String PROTOTYPE_IS_TAINTED = "(I)Z";
    private static final String NAME_TAINT = "taint";
    private static final String PROTOTYPE_TAINT_PRIMITIVE = "(I)I";
    private static final String PROTOTYPE_TAINT_REFERENCE = "(Ljava/lang/Object;)Ljava/lang/Object";

    private boolean isGivenUtilsMethod(DexMethod method, String methodName, String methodPrototype) {
        return
            method.getParentClass().getClassDef().getType().getDescriptor().equals(TAINTUTILS_CLASS) &&
            method.getMethodDef().getMethodId().getName().equals(methodName) &&
            method.getMethodDef().getMethodId().getPrototype().getDescriptor().equals(methodPrototype) &&
            method.getMethodDef().isStatic() &&
            method.getMethodBody() != null;
    }
}
