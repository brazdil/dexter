package uk.ac.cam.db538.dexter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.DexFileFromMemory;
import org.jf.dexlib.Util.ByteArrayAnnotatedOutput;
import uk.ac.cam.db538.dexter.dex.AuxiliaryDex;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder;
import com.rx201.dx.translator.DexCodeGeneration;

public class MainTest {
	
	
	private static void dumpAnnotation(File apkFile) {
		final org.jf.dexlib.Util.ByteArrayAnnotatedOutput out = new ByteArrayAnnotatedOutput();
		out.enableAnnotations(80, true);
		DexFile outFile;
		try {
			outFile = new DexFile(apkFile);
			outFile.place();
			outFile.writeTo(out);
			out.writeAnnotationsTo(new FileWriter("annot_original.txt"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
//  private static void writeToJar(Dex dex, File targetFile) {
//     final byte[] newDex = dex.writeToFile();
//
//     System.out.println("Creating JAR");
//     try {
//	     targetFile.delete();
//	     ZipFile jarFile = new ZipFile(targetFile);
//	     
//	     ZipParameters parameters = new ZipParameters();
//	     parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//	     parameters.setFileNameInZip("classes.dex");
//	     parameters.setSourceExternalStream(true);
//	
//	     jarFile.addStream(new ByteArrayInputStream(newDex), parameters);
//     } catch (ZipException e) {
//     }
//  }
	public static void main(String[] args) throws IOException {
		long epoch = System.currentTimeMillis();
		if (args.length != 2 && args.length != 3) {
			System.err.println("usage: dexter <framework-dir> <apk-file> [<destination-apk]>");
			System.exit(1);
		}
		final java.io.File apkFile = new File(args[1]);
		if (!apkFile.isFile()) {
			System.err.println("<apk-file> is not a file");
			System.exit(1);
		}
		File apkFile_new;
		if (args.length == 3) apkFile_new = new File(args[2]); else apkFile_new = new File(apkFile.getAbsolutePath() + "_new.apk");
		// dumpAnnotation(apkFile);
		// val apkFile_new = new File(apkFile.getAbsolutePath() + "_new.apk");
		final java.io.File frameworkDir = new File(args[0]);
		if (!frameworkDir.isDirectory()) {
			System.err.println("<framework-dir> is not a directory");
			System.exit(1);
		}
		// build runtime class hierarchy
		final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
		System.out.println("Scanning framework");
		hierarchyBuilder.importFrameworkFolder(frameworkDir);
		long hierarchyTime = System.currentTimeMillis() - epoch;
		System.out.println("Scanning application");
		final org.jf.dexlib.DexFile fileApp = new DexFile(apkFile);
		final org.jf.dexlib.DexFileFromMemory fileAux = new DexFileFromMemory(ClassLoader.getSystemResourceAsStream("merge-classes.dex"));
		System.out.println("Building hierarchy");
		final uk.ac.cam.db538.dexter.utils.Pair<uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy, uk.ac.cam.db538.dexter.dex.type.ClassRenamer> buildData = hierarchyBuilder.buildAgainstApp(fileApp, fileAux);
		final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = buildData.getValA();
		final uk.ac.cam.db538.dexter.dex.type.ClassRenamer renamerAux = buildData.getValB();
		System.out.println("Parsing application");
		AuxiliaryDex dexAux = new AuxiliaryDex(fileAux, hierarchy, renamerAux);
		Dex dexApp = new Dex(fileApp, hierarchy, dexAux);
		if (args.length == 3) {
			DexCodeGeneration.DEBUG = false;
//      System.out.println("Instrumenting application");
//      dex.instrument(false);
		} else {
//    	dex.instrument(false);
		}
		
//    writeToJar(dexApp, apkFile_new);
//    Apk.produceAPK(apkFile, apkFile_new, "ApplicationClass", dexApp.writeToFile());
		long analysisTime = DexCodeGeneration.totalAnalysisTime;
		long translationTime = DexCodeGeneration.totalCGTime;
		long compileTime = DexCodeGeneration.totalDxTime;
		long totalTime = System.currentTimeMillis() - epoch;
		System.out.println("===1=== Hierarchy:" + hierarchyTime + ", Analyze:" + analysisTime + ", Translate:" + translationTime + ", Compile:" + compileTime + ", Total:" + totalTime);
	}
}