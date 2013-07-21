package uk.ac.cam.db538.dexter.dex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.CodeItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.Util.ByteArrayAnnotatedOutput;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class Dex {

  @Getter final RuntimeHierarchy hierarchy;
  @Getter final AuxiliaryDex auxiliaryDex;

  private final Set<DexClass> _classes;
  @Getter private final Set<DexClass> classes;

  private final ProgressCallback progressCallback;

//  @Getter private DexClass externalStaticFieldTaint_Class;
//  @Getter private DexMethodWithCode externalStaticFieldTaint_Clinit;

  /*
   * Creates an empty Dex
   */
  public Dex(RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, ProgressCallback progressCallback) {
    this.hierarchy = hierarchy;
    this.auxiliaryDex = dexAux;
    this.progressCallback = progressCallback;
    
    this._classes = new HashSet<DexClass>();
    this.classes = Collections.unmodifiableSet(this._classes);
  }

  /*
   * Creates a new Dex and parses all classes inside the given DexFile
   */
  public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, ProgressCallback progressCallback) {
	this(hierarchy, dexAux, progressCallback);

    val dexClsInfos = dex.ClassDefsSection.getItems();
    int clsCount = dexClsInfos.size();
    int i = 0;
    progressUpdate(0, clsCount);
    for (val dexClsInfo : dexClsInfos) {
        this._classes.add(new DexClass(this, dexClsInfo));
        progressUpdate(++i, clsCount);
    }
  }

  public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux) {
    this(dex, hierarchy, dexAux, (ProgressCallback) null);
  }

  /*
   * This constructor applies a descriptor renamer on the classes parsed from given DexFile
   */
  public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, ClassRenamer renamer, ProgressCallback progressCallback) {
	  this(dex, setRenamer(hierarchy, renamer), dexAux, progressCallback);
	  unsetRenamer(hierarchy);
  }

  public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, ClassRenamer renamer) {
      this(dex, hierarchy, dexAux, renamer, null);
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

//  private List<DexClass> generateExtraClasses() {
//    val parsingCache = getTypeCache();
//
//    externalStaticFieldTaint_Class = new DexClass(
//      this,
//      generateClassType(),
//      DexClassType.parse("Ljava/lang/Object;", parsingCache),
//      EnumSet.of(AccessFlags.PUBLIC),
//      null,
//      null,
//      null,
//      null);
//
//    val clinitCode = new DexCode();
//    clinitCode.add(new DexInstruction_ReturnVoid(clinitCode));
//
//    externalStaticFieldTaint_Clinit = new DexDirectMethod(
//      externalStaticFieldTaint_Class,
//      "<clinit>",
//      EnumSet.of(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR),
//      new DexPrototype(DexVoid.parse("V", parsingCache), null),
//      clinitCode,
//      null, null);
//    externalStaticFieldTaint_Class.addMethod(externalStaticFieldTaint_Clinit);
//
//    return Arrays.asList(new DexClass[] { externalStaticFieldTaint_Class });
//  }

  public void instrument(boolean debug) {
//    val cache = new DexInstrumentationCache(this, debug);
//
//    val extraClassesLinked = parseExtraClasses();
//    val extraClassesGenerated = generateExtraClasses();
//
//    for (val cls : classes)
//      cls.instrument(cache);
//
//    classes.addAll(extraClassesLinked);
//    classes.addAll(extraClassesGenerated);
//
//    return cache.getWarnings();
  }

  public byte[] writeToFile() {
    val outFile = new DexFile();
    val out = new ByteArrayAnnotatedOutput();

    int i = 0;
    int count = _classes.size();
    progressUpdate(0, count);
    val asmCache = new DexAssemblingCache(outFile, hierarchy);
    for (val cls : _classes) {
      cls.writeToFile(outFile, asmCache);
      progressUpdate(++i, count);
    }

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

}
