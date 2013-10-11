package uk.ac.cam.db538.dexter.dex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.CodeItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.Util.ByteArrayAnnotatedOutput;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.apk.Manifest;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.transform.Transform;
import uk.ac.cam.db538.dexter.transform.taint.AuxiliaryDex;
import uk.ac.cam.db538.dexter.utils.Utils;

import com.android.dx.ssa.Optimizer;
import com.rx201.dx.translator.DexCodeGeneration;

public class Dex {

    @Getter final RuntimeHierarchy hierarchy;
    @Getter final AuxiliaryDex auxiliaryDex;
    @Getter final Manifest manifest;
    @Getter private List<DexClass> classes;

    private final ProgressCallback progressCallback;
    private Transform transform;

    /*
     * Creates an empty Dex
     */
    public Dex(RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, Manifest manifest, ProgressCallback progressCallback) {
        this.hierarchy = hierarchy;
        this.auxiliaryDex = dexAux;
        this.manifest = manifest;
        this.progressCallback = progressCallback;
        this.classes = Collections.emptyList();
    }

    /*
     * Creates a new Dex and parses all classes inside the given DexFile
     */
    public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, Manifest manifest, ProgressCallback progressCallback) {
        this(hierarchy, dexAux, manifest, progressCallback);

        val dexClsInfos = dex.ClassDefsSection.getItems();
        int clsCount = dexClsInfos.size();
        int i = 0;
        progressUpdate(0, clsCount);
        List<DexClass> classes = new ArrayList<DexClass>(clsCount);
        for (val dexClsInfo : dexClsInfos) {
            classes.add(new DexClass(this, dexClsInfo));
            progressUpdate(++i, clsCount);
        }
        
        sortClassesByName(classes);
        this.classes = Utils.finalList(classes);
    }
    
    private void sortClassesByName(List<DexClass> classes) {
        Collections.sort(classes, new Comparator<DexClass>() {
			@Override
			public int compare(DexClass o1, DexClass o2) {
				if (o1.equals(o2))
					return 0;
				
				// StaticTaintFields class must be compiled as the last
				if (transform != null) {
					if (Dex.this.transform.handleLast(o1))
						return 1;
					else if (Dex.this.transform.handleLast(o2))
						return -1;
				}
					
				String d1 = o1.getClassDef().getType().getDescriptor();
				String d2 = o2.getClassDef().getType().getDescriptor();
				return d1.compareTo(d2);
			}
		});
    }

    public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, Manifest manifest) {
        this(dex, hierarchy, dexAux, manifest, (ProgressCallback) null);
    }

    /*
     * This constructor applies a descriptor renamer on the classes parsed from given DexFile
     */
    public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, Manifest manifest, ClassRenamer renamer, ProgressCallback progressCallback) {
        this(dex, setRenamer(hierarchy, renamer), dexAux, manifest, progressCallback);
        unsetRenamer(hierarchy);
    }

    public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, Manifest manifest, ClassRenamer renamer) {
        this(dex, hierarchy, dexAux, manifest, renamer, null);
    }

    private static RuntimeHierarchy setRenamer(RuntimeHierarchy hierarchy, ClassRenamer renamer) {
        hierarchy.getTypeCache().setClassRenamer(renamer);
        return hierarchy;
    }

    private static void unsetRenamer(RuntimeHierarchy hierarchy) {
        hierarchy.getTypeCache().setClassRenamer(null);
    }

    public DexTypeCache getTypeCache() {
        return hierarchy.getTypeCache();
    }

    private void progressUpdate(int finished, int outOf) {
        if (this.progressCallback != null)
            this.progressCallback.update(finished, outOf);
    }

    public DexClass getClass(BaseClassDefinition classDef) {
        for (DexClass clazz : classes)
            if (clazz.getClassDef().equals(classDef))
                return clazz;
        return null;
    }

    public byte[] writeToFile() {
        if (transform != null)
        	transform.doFirst(this.manifest);

        val outFile = new DexFile();
        val out = new ByteArrayAnnotatedOutput();

        int i = 0;
        int count = classes.size();
        progressUpdate(0, count);
        val asmCache = new DexAssemblingCache(outFile, hierarchy);
        for (val cls : classes) {
            
            //Apply transform
            if (transform != null)
                transform.doClass(cls);
            
            cls.writeToFile(outFile, asmCache);
            progressUpdate(++i, count);
            
            //Free method code memory, assuming they will never be used again.
            cls.replaceMethods(Collections.<DexMethod> emptyList());
            
        }
        
        if (transform != null)
        	transform.doLast(this.manifest);
        
        // Apply jumbo-instruction fix requires ReferencedItem being
        // placed first, after which the code needs to be placed again
        // because jumbo instruction is wider.
        // The second pass shoudn't change ReferencedItem's placement
        // (because they are ordered deterministically by its content)
        // otherwise we'll be in trouble.
        outFile.place();
        fixInstructions(outFile);
        outFile.place();
        outFile.writeTo(out);

        val bytes = out.toByteArray();

        DexFile.calcSignature(bytes);
        DexFile.calcChecksum(bytes);

        return bytes;
    }

    private void fixInstructions(DexFile outFile) {
        for (CodeItem codeItem : outFile.CodeItemsSection.getItems()) {
            codeItem.fixInstructions(true, true);
        }
    }

    public void addClasses(Collection<DexClass> cls) {
    	List<DexClass> newClasses = new ArrayList<DexClass>(this.classes.size() + cls.size());
        newClasses.addAll(this.classes);
        newClasses.addAll(cls);
        sortClassesByName(newClasses);
        this.classes = Utils.finalList(newClasses);
    }

    public void setTransform(Transform transform) {
        if (this.transform != null) 
            throw new RuntimeException("Cannot change existing transform");
        
        this.transform = transform;
        transform.prepare(this);
    }
    
    /*
     * Expects format Lpackage/class;->method(prototype)returntype
     */
    public void dumpMethod(String methodStr) {
    	
    	int arrowIndex = methodStr.indexOf("->");
    	assert (arrowIndex > 0);
    	
    	String className = methodStr.substring(0, arrowIndex);
    	BaseClassDefinition clazzDef = hierarchy.getClassDefinition(DexClassType.parse(className, getTypeCache()));
    	DexClass clazz = this.getClass(clazzDef);
    	
    	DexMethod method;
    	
    	method = findMethodByDefStr(methodStr, clazz);
    	dumpMethod(method);
    	
    	transform.doClass(clazz);
    	
    	method = findMethodByDefStr(methodStr, clazz);
    	dumpMethod(method);
    	
    	DexCodeGeneration codeGen = new DexCodeGeneration(method);
    	Optimizer.DEBUG_SSA_DUMP = true;
    	codeGen.processMethod(method.getMethodBody());
    }
    
    public DexMethod findMethodByDefStr(String methodStr, DexClass clazz) {
    	for (DexMethod method : clazz.getMethods())
    		if (method.getMethodDef().toString().equals(methodStr))
    			return method;
    	
    	throw new RuntimeException("Method " + methodStr + " not found");
    }
    
    private void dumpMethod(DexMethod method) {
		System.err.println("METHOD: " + method.getMethodDef().toString());
		method.getMethodBody().getInstructionList().dump();
		System.err.println("END METHOD");
		System.err.println();
		return;
    }
}
