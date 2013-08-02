package uk.ac.cam.db538.dexter.transform;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.transform.taint.AuxiliaryDex;

public abstract class Transform {
	
	private final ProgressCallback progressCallback;
	
	public Transform() {
		this(null);
	}
	
	public Transform(ProgressCallback progressCallback) {
		this.progressCallback = progressCallback;
	}
	
	private Dex dex;
	
	public final void apply(Dex dex) {
		this.dex = dex;
		
		progressUpdate(0, 1);
		
		doFirst(dex);
		
		int finished = 1;
		int count = dex.getClasses().size() + 2;
		progressUpdate(++finished, count);
		
		/*
		 * classes are mutable
		 * (need to be in order for the method and field objects 
		 *  to store a constant reference to their parent) 
		 */
		for (DexClass clazz : dex.getClasses()) {
			doFirst(clazz);

			clazz.replaceMethods(apply(clazz.getMethods()));
						
			doLast(clazz);
			
			progressUpdate(++finished, count);
		}
		
		doLast(dex);
		
		progressUpdate(count, count);
	}
	
	private List<DexMethod> apply(List<DexMethod> oldMethods) {
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
				for (DexCodeElement oldInsn : oldInstructions) {
					DexCodeElement newInsn = doLast(doFirst(oldInsn, newMethodBody, newMethod), newMethodBody, newMethod);
					newInstructions.add(newInsn);
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
	
	public void doFirst(Dex dex) { }
	public void doFirst(DexClass clazz) { }
	public DexMethod doFirst(DexMethod method) { return method; }
	public DexCode doFirst(DexCode code, DexMethod method) { return code; }
	public DexCodeElement doFirst(DexCodeElement element, DexCode code, DexMethod method) { return element; }
	
	public void doLast(Dex dex) { }
	public void doLast(DexClass clazz) { }
	public DexMethod doLast(DexMethod method) { return method; }
	public DexCode doLast(DexCode code, DexMethod method) { return code; }
	public DexCodeElement doLast(DexCodeElement element, DexCode code, DexMethod method) { return element; }
	
	private void progressUpdate(int finished, int outOf) {
		if (this.progressCallback != null)
			this.progressCallback.update(finished, outOf);
	}
	
	protected AuxiliaryDex getAuxiliaryDex() {
		return this.dex.getAuxiliaryDex();
	}
}
