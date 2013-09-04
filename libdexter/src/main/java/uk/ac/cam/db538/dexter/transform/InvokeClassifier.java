package uk.ac.cam.db538.dexter.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.val;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexEmpty;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryEnd;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_FilledNewArray;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.macro.DexMacro;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition.CallDestinationType;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;
import uk.ac.cam.db538.dexter.utils.Triple;

import com.rx201.dx.translator.DexCodeAnalyzer;
import com.rx201.dx.translator.RopType.Category;

public class InvokeClassifier {

    private InvokeClassifier() { }

    public static Triple<DexCode, ? extends Map<MethodCall, CallDestinationType>, ? extends Set<DexCodeElement>> classifyMethodCalls(DexCode code, DexCodeAnalyzer codeAnalysis, CodeGenerator codeGen) {
        Map<MethodCall, CallDestinationType> classification = new HashMap<MethodCall, CallDestinationType>();
        Set<DexCodeElement> extraInstructions = new HashSet<DexCodeElement>();

        // analyze each invoke instruction

        InstructionList oldInsns = code.getInstructionList();
        List<DexCodeElement> newInsns = new ArrayList<DexCodeElement>(oldInsns.size());

        for (val insn : oldInsns) {
            if (insn instanceof MethodCall) {
                MethodCall methodCall = (MethodCall) insn;
                DexInstruction_Invoke invokeInsn = methodCall.getInvoke();

                DexReferenceType calledClassType = invokeInsn.getClassType();
                Opcode_Invoke calledOpcode = invokeInsn.getCallType();

                // for -virtual and -interface calls, use DexCodeAnalyzer to
                // more precisely determine the type of the object the instruction
                // is invoked on

                if (calledOpcode == Opcode_Invoke.Virtual || calledOpcode == Opcode_Invoke.Interface) {
                    val analyzedInsn = codeAnalysis.reverseLookup(invokeInsn);
                    val thisArgReg = invokeInsn.getArgumentRegisters().get(0);
                    val calledClassRopType = analyzedInsn.getUsedRegisterType(thisArgReg);
                    if (calledClassRopType.category == Category.Reference)
                        calledClassType = calledClassRopType.type;
                }

                val calledClassDef = code.getHierarchy().getBaseClassDefinition(calledClassType);
                val destType = calledClassDef.getMethodDestinationType(invokeInsn.getMethodId(), calledOpcode);

                if (destType == CallDestinationType.Undecidable) {
                    // check the destination type dynamically
                    // and add the invoke in each branch as external/internal

                    MethodCall internalCall = methodCall.clone();
                    MethodCall externalCall = methodCall.clone();

                    DexSingleRegister regAnno = codeGen.auxReg();
                    DexLabel lExternal = codeGen.label();
                    DexLabel lEnd = codeGen.label();

                    DexMacro instrumentation = new DexMacro(
                        codeGen.getMethodAnnotation(regAnno, methodCall),
                        codeGen.ifZero(regAnno, lExternal),
                        internalCall,
                        codeGen.jump(lEnd),
                        lExternal,
                        externalCall,
                        lEnd);

                    newInsns.add(instrumentation);
                    classification.put(internalCall, CallDestinationType.Internal);
                    classification.put(externalCall, CallDestinationType.External);

                    // store all of the added instructions in order to skip their instrumentation later
                    extraInstructions.addAll(instrumentation.getInstructions());
                    extraInstructions.remove(internalCall);
                    extraInstructions.remove(externalCall);

                } else {
                    // if destination is decidable, store it
                    newInsns.add(insn);
                    classification.put(methodCall, destType);
                }
            } else
                newInsns.add(insn);
        }

        code = new DexCode(code, new InstructionList(newInsns));
        return Triple.create(code, classification, extraInstructions);
    }

    public static DexCode collapseCalls(DexCode code) {
        InstructionList oldInsns = code.getInstructionList();
        List<DexCodeElement> newInsns = new ArrayList<DexCodeElement>(oldInsns.size());
        
        Set<DexCodeElement> toSkip = new HashSet<DexCodeElement>();

        for (DexCodeElement insn : oldInsns) {
        	if (toSkip.contains(insn)) {
        		toSkip.remove(insn);
        		continue;
        	} else if (insn instanceof DexInstruction_MoveResult)
                continue;
            else if (insn instanceof DexInstruction_Invoke) {

                DexInstruction nextInstruction = code.getInstructionList().getNextProperInstruction(insn);
                if (!(nextInstruction instanceof DexInstruction_MoveResult))
                    nextInstruction = null;
                
                DexInstruction_Invoke insnInvoke = (DexInstruction_Invoke) insn;
                DexInstruction_MoveResult insnResult = (DexInstruction_MoveResult) nextInstruction;
                
                DexTryStart endingBlock = getEndingTryBlock(insnInvoke, insnResult, code);
                
                if (endingBlock == null)
                	newInsns.add(new MethodCall(insnInvoke, insnResult));
                else {
                	DexTryEnd clonedEnd = new DexTryEnd(endingBlock.getEndMarker().getId());
                	DexTryStart clonedStart = new DexTryStart(endingBlock, clonedEnd);
                	
                	newInsns.add(endingBlock.getEndMarker());
                	newInsns.add(new MethodCall(insnInvoke, insnResult, clonedStart));
                	
                	toSkip.add(endingBlock.getEndMarker());
                }

            } else if (insn instanceof DexInstruction_FilledNewArray) {

                DexInstruction nextInstruction = code.getInstructionList().getNextProperInstruction(insn);
                assert (nextInstruction instanceof DexInstruction_MoveResult);
                newInsns.add(new FilledArray((DexInstruction_FilledNewArray) insn, (DexInstruction_MoveResult) nextInstruction));

            } else
                newInsns.add(insn);
        }

        return new DexCode(code, new InstructionList(newInsns));
    }
    
    private static DexTryStart getEndingTryBlock(DexInstruction_Invoke invoke, DexInstruction_MoveResult moveResult, DexCode code) {
    	if (moveResult == null)
    		return null;
    	
    	DexTryStart tryStart = null;
    	for (DexCodeElement between : code.getInstructionList().getInstructionsBetween(invoke, moveResult)) {
    		if (between instanceof DexEmpty)
    			continue;
    		else if (between instanceof DexTryEnd) {
    			tryStart = code.getInstructionList().getSurroundingTryBlock(invoke);
    			assert tryStart.getEndMarker() == between;
    		} else
    			assert false;
    	}
    	
    	return tryStart;
    }

    public static DexCode expandCalls(DexCode code) {
        InstructionList oldInsns = code.getInstructionList();
        List<DexCodeElement> newInsns = new ArrayList<DexCodeElement>(oldInsns.size());

        for (DexCodeElement insn : oldInsns) {
            if (insn instanceof MethodCall)
                newInsns.add(((MethodCall) insn).expand());
            else if (insn instanceof FilledArray)
                newInsns.add(((FilledArray) insn).expand());
            else
                newInsns.add(insn);
        }

        return new DexCode(code, new InstructionList(newInsns));
    }
}
