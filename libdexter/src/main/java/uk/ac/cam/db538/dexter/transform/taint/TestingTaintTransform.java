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
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_NewInstance;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Return;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexTaintRegister;
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
        return name.equals("LTestList;");
    }

    @Override
    public DexMethod doLast(DexMethod method) {
        if (isGivenUtilsMethod(method, NAME_ISTAINTED, PROTOTYPE_ISTAINTED_PRIMITIVE)) {
        	
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
            
        } else if (isGivenUtilsMethod(method, NAME_ISTAINTED, PROTOTYPE_ISTAINTED_REFERENCE)) {
        	
            DexCode oldCode = method.getMethodBody();
            DexSingleRegister paramReg = (DexSingleRegister) oldCode.getParameters().get(0).getRegister();
            DexTaintRegister paramRegTaint = paramReg.getTaintRegister();

            List<DexCodeElement> newInstructions = new ArrayList<DexCodeElement>();
            for (DexCodeElement insn : oldCode.getInstructionList())
                if (insn instanceof DexInstruction_Return) {
                	newInstructions.add(codeGen.taintClearVisited());
                	newInstructions.add(codeGen.getTaint(paramRegTaint, paramReg));
                    newInstructions.add(new DexInstruction_Return(paramRegTaint, false, oldCode.getHierarchy()));
                } else
                    newInstructions.add(insn);

            DexCode newCode = new DexCode(oldCode, new InstructionList(newInstructions));
            method = new DexMethod(method, newCode);

        } else if (isGivenUtilsMethod(method, NAME_TAINT, PROTOTYPE_TAINT_PRIMITIVE)) {

            DexCode oldCode = method.getMethodBody();
            DexRegister paramReg = oldCode.getParameters().get(0).getRegister();
            DexTaintRegister paramRegTaint = paramReg.getTaintRegister();
            
            List<DexCodeElement> newInstructions = new ArrayList<DexCodeElement>();
            for (DexCodeElement insn : oldCode.getInstructionList())
                if (insn instanceof DexInstruction_ArrayGet && ((DexInstruction_ArrayGet) insn).getRegTo().equals(paramRegTaint))
                    newInstructions.add(new DexInstruction_Const(paramRegTaint, 1, oldCode.getHierarchy()));
                else
                    newInstructions.add(insn);

            DexCode newCode = new DexCode(oldCode, new InstructionList(newInstructions));
            method = new DexMethod(method, newCode);
            
        } else if (isGivenUtilsMethod(method, NAME_TAINT, PROTOTYPE_TAINT_REFERENCE)) {

        	DexCode oldCode = method.getMethodBody();
            DexRegister paramReg = oldCode.getParameters().get(0).getRegister();
            DexTaintRegister paramRegTaint = paramReg.getTaintRegister();
            
            List<DexCodeElement> newInstructions = new ArrayList<DexCodeElement>();
            for (DexCodeElement insn : oldCode.getInstructionList())
                if (insn instanceof DexInstruction_NewInstance) { 
                	DexSingleRegister regTaint = codeGen.auxReg();
                	newInstructions.add(codeGen.taintClearVisited());
                	newInstructions.add(codeGen.constant(regTaint, 1));
                    newInstructions.add(codeGen.setTaint(regTaint, paramRegTaint));
                } else
                    newInstructions.add(insn);

            DexCode newCode = new DexCode(oldCode, new InstructionList(newInstructions));
            method = new DexMethod(method, newCode);
        	
        }
        
        return super.doLast(method);
    }

    private static final String TAINTUTILS_CLASS = "LTaintUtils;";
    private static final String NAME_ISTAINTED = "isTainted";
    private static final String PROTOTYPE_ISTAINTED_PRIMITIVE = "(I)Z";
    private static final String PROTOTYPE_ISTAINTED_REFERENCE = "(Ljava/lang/Object;)Z";
    private static final String NAME_TAINT = "taint";
    private static final String PROTOTYPE_TAINT_PRIMITIVE = "(I)I";
    private static final String PROTOTYPE_TAINT_REFERENCE = "(Ljava/lang/Object;)Ljava/lang/Object;";

    private boolean isGivenUtilsMethod(DexMethod method, String methodName, String methodPrototype) {
        return
            method.getParentClass().getClassDef().getType().getDescriptor().equals(TAINTUTILS_CLASS) &&
            method.getMethodDef().getMethodId().getName().equals(methodName) &&
            method.getMethodDef().getMethodId().getPrototype().getDescriptor().equals(methodPrototype) &&
            method.getMethodDef().isStatic() &&
            method.getMethodBody() != null;
    }
}
