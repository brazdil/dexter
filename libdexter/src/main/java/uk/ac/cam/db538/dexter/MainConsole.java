package uk.ac.cam.db538.dexter;

import java.io.File;
import java.io.IOException;

import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.apk.Apk;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder;
import uk.ac.cam.db538.dexter.manifest.BinXmlUtil;
import uk.ac.cam.db538.dexter.manifest.Manifest;
import uk.ac.cam.db538.dexter.transform.Transform;
import uk.ac.cam.db538.dexter.transform.taint.AuxiliaryDex;
import uk.ac.cam.db538.dexter.transform.taint.TaintTransform;
import uk.ac.cam.db538.dexter.transform.taint.TestingTaintTransform;

import com.rx201.dx.translator.DexCodeGeneration;

public class MainConsole {

    public static void main(String[] args) throws IOException {
        DexCodeGeneration.DEBUG = false;
        DexCodeGeneration.INFO = true;
    	DexCodeGeneration.ADD_LINENO = true;
        DexClass.FAKE_SOURCE_FILE = true;
        
        if (args.length != 2 && args.length != 3) {
            System.err.println("usage: dexter <framework-dir> <apk-file> (<method-id>)");
            System.exit(1);
        }

        val apkFile = new File(args[1]);
        if (!apkFile.isFile()) {
            System.err.println("<apk-file> is not a file");
            System.exit(1);
        }
        
        val frameworkDir = new File(args[0]);
        if (!frameworkDir.isDirectory()) {
            System.err.println("<framework-dir> is not a directory");
            System.exit(1);
        }
        File frameworkCache = new File(frameworkDir, "hierarchy.cache");

        String methodId;
        if (args.length == 3)
        	methodId = args[2];
        else
        	methodId = null;

        HierarchyBuilder hierarchyBuilder = null;
		try {
			if (frameworkCache.exists()) {
				System.out.println("Loading framework from cache");
				hierarchyBuilder = HierarchyBuilder.deserialize(frameworkCache);
			}
		} catch (Exception e) {}
		if (hierarchyBuilder == null) {
			System.out.println("Scanning framework");
			hierarchyBuilder = new HierarchyBuilder();
		    hierarchyBuilder.importFrameworkFolder(frameworkDir);
			hierarchyBuilder.serialize(frameworkCache);
		}

        System.out.println("Scanning application");
        val fileApp = new DexFile(apkFile);
        val fileAux = new DexFile("dexter_aux/build/libs/dexter_aux.dex");
        Manifest manifest = Apk.getManifest(apkFile);

        System.out.println("Importing aux");
        hierarchyBuilder.importDex(fileAux, false);

        System.out.println("Building hierarchy");
        val buildData = hierarchyBuilder.buildAgainstApp(fileApp, fileAux);
        val hierarchy = buildData.getValA();
        val renamerAux = buildData.getValB();

        System.out.println("Parsing application");
        AuxiliaryDex dexAux = new AuxiliaryDex(fileAux, hierarchy, renamerAux);
        Dex dexApp = new Dex(fileApp, hierarchy, dexAux, manifest);

        Transform transform;
        if (apkFile.getName().equals("test.apk"))
        	transform = new TestingTaintTransform();
        else
        	transform = new TaintTransform();
        dexApp.setTransform(transform);
        
        if (methodId == null) {
        
        	System.out.println("Recompiling application");
        	val newDex = dexApp.writeToFile();

        	System.out.println("Generating new apk");
        	Apk.produceAPK(apkFile, new File(apkFile.getAbsolutePath() + "_new.apk"), manifest, newDex);
        	
        } else
       	
        	dexApp.dumpMethod(methodId);


        System.out.println("DONE");
    }
}
