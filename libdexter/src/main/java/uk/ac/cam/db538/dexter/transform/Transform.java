package uk.ac.cam.db538.dexter.transform;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.DexUtils;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexEmpty;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.transform.taint.AuxiliaryDex;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public abstract class Transform {

    public Transform() {
    }

    protected Dex dex;

    public final void apply(Dex dex) {
        this.dex = dex;

        /*
         * classes are mutable
         * (need to be in order for the method and field objects
         *  to store a constant reference to their parent)
         */
        for (DexClass clazz : dex.getClasses()) {
            if (exclude(clazz))
                continue;

            doFirst(clazz);

            clazz.replaceMethods(apply(clazz.getMethods()));

            doLast(clazz);

        }

    }

    public boolean exclude(DexClass clazz) {
        return false;
    }

    public List<DexMethod> apply(List<DexMethod> oldMethods) {
        List<DexMethod> newMethods = new ArrayList<DexMethod>(oldMethods.size());
        for (DexMethod newMethod : oldMethods) {
            newMethod = doFirst(newMethod);

            DexCode oldMethodBody = newMethod.getMethodBody();
            DexCode newMethodBody = oldMethodBody;
            if (newMethodBody != null) {
                newMethodBody = doFirst(newMethodBody, newMethod);

                boolean instructionsChanged = false;
                InstructionList oldInstructions = newMethodBody.getInstructionList();
                List<DexCodeElement> newInstructions = new ArrayList<DexCodeElement>(oldInstructions.size());
                int line = 0;
                for (DexCodeElement oldInsn : oldInstructions) {
                    DexCodeElement newInsn = doLast(doFirst(oldInsn, newMethodBody, newMethod), newMethodBody, newMethod);
                    newInstructions.add(new DexMacro(new DexEmpty(line++), newInsn));
                    instructionsChanged |= (newInsn != oldInsn);
                }
                if (instructionsChanged)
                    newMethodBody = new DexCode(newMethodBody, new InstructionList(newInstructions));

                newMethodBody = doLast(newMethodBody, newMethod);
            }
            if (oldMethodBody != newMethodBody)
                newMethod = new DexMethod(newMethod, newMethodBody);

            newMethod = doLast(newMethod);

            newMethods.add(newMethod);
        }
        return newMethods;
    }

    public void doFirst(DexClass clazz) { }
    public DexMethod doFirst(DexMethod method) {
        return method;
    }
    public DexCode doFirst(DexCode code, DexMethod method) {
        return code;
    }
    public DexCodeElement doFirst(DexCodeElement element, DexCode code, DexMethod method) {
        return element;
    }

    public void doLast(DexClass clazz) { }
    public DexMethod doLast(DexMethod method) {
        return method;
    }
    public DexCode doLast(DexCode code, DexMethod method) {
        return code;
    }
    public DexCodeElement doLast(DexCodeElement element, DexCode code, DexMethod method) {
        return element;
    }

    public void prepare(Dex dex) {
        this.dex = dex;
    }

    public void doClass(DexClass cls) {
        if (exclude(cls))
            return;
        doFirst(cls);
        cls.replaceMethods(apply(cls.getMethods()));
        doLast(cls);
    }
    
    public boolean handleLast(DexClass cls) {
    	return false;
    }
}
