package uk.ac.cam.db538.dexter.hierarchy.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.DexFile.NoClassesDexException;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.ClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyException;
import uk.ac.cam.db538.dexter.hierarchy.InstanceFieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;
import uk.ac.cam.db538.dexter.utils.Pair;

public class HierarchyBuilder implements Serializable {
	private ClassDefinition root;
	private final DexTypeCache typeCache;
	private final Map<DexClassType, ClassVariants> definedClasses;
	
	public HierarchyBuilder() {
		
		root = null;
		typeCache = new DexTypeCache();
		definedClasses = new HashMap<DexClassType, ClassVariants>();
	}
	
	public boolean hasClass(DexClassType clsType) {
		return definedClasses.containsKey(clsType);
	}
	
	public void importDex(File file, boolean isInternal) throws IOException {
		// parse the file
		DexFile dex;
		try {
			dex = new DexFile(file, false, true);
		} catch (NoClassesDexException e) {
			// file does not contain classes.dex
			return;
		}
		importDex(dex, isInternal);
		// explicitly dispose of the object
		dex = null;
		System.gc();
	}
	
	public void importDex(DexFile dex, boolean isInternal) {
		// recursively scan classes
		for (final org.jf.dexlib.ClassDefItem cls : dex.ClassDefsSection.getItems()) scanClass(new DexClassScanner(cls, typeCache), isInternal);
	}
	
	private void scanClass(DexClassScanner clsScanner, boolean isInternal) {
		final uk.ac.cam.db538.dexter.dex.type.DexClassType clsType = DexClassType.parse(clsScanner.getClassDescriptor(), typeCache);
		final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassData baseclsData = new ClassData();
		if (clsScanner.isInterface()) baseclsData.classDef = new InterfaceDefinition(clsType, clsScanner.getAccessFlags(), isInternal); else {
			final uk.ac.cam.db538.dexter.hierarchy.ClassDefinition clsDef = new ClassDefinition(clsType, clsScanner.getAccessFlags(), isInternal);
			baseclsData.classDef = clsDef;
			scanInstanceFields(clsScanner, clsDef);
			scanSuperclass(clsScanner, clsDef, baseclsData, isInternal);
			baseclsData.interfaces = clsScanner.getInterfaces();
		}
		scanMethods(clsScanner, baseclsData.classDef);
		scanStaticFields(clsScanner, baseclsData.classDef);
		// store data
		ClassVariants clsVariants = definedClasses.get(clsType);
		if (clsVariants == null) {
			clsVariants = new ClassVariants();
			definedClasses.put(clsType, clsVariants);
		}
		clsVariants.setVariant(baseclsData, isInternal);
	}
	
	private void foundRoot(ClassDefinition clsInfo, boolean isInternal) {
		// check only one root exists
		if (root != null) throw new HierarchyException("More than one hierarchy root found (" + root.getType().getPrettyName() + " vs. " + clsInfo.getType().getPrettyName() + ")"); else if (isInternal) throw new HierarchyException("Hierarchy root cannot be internal"); else root = clsInfo;
	}
	
	private void scanMethods(DexClassScanner clsScanner, BaseClassDefinition baseclsDef) {
		for (final uk.ac.cam.db538.dexter.hierarchy.builder.DexMethodScanner methodScanner : clsScanner.getMethodScanners()) {
			baseclsDef.addDeclaredMethod(new MethodDefinition(baseclsDef, methodScanner.getMethodId(), methodScanner.getAccessFlags()));
		}
	}
	
	private void scanStaticFields(DexClassScanner clsScanner, BaseClassDefinition baseclsDef) {
		for (final uk.ac.cam.db538.dexter.hierarchy.builder.DexFieldScanner fieldScanner : clsScanner.getStaticFieldScanners()) baseclsDef.addDeclaredStaticField(new StaticFieldDefinition(baseclsDef, fieldScanner.getFieldId(), fieldScanner.getAccessFlags()));
	}
	
	private void scanInstanceFields(DexClassScanner clsScanner, ClassDefinition clsDef) {
		for (final uk.ac.cam.db538.dexter.hierarchy.builder.DexFieldScanner fieldScanner : clsScanner.getInstanceFieldScanners()) clsDef.addDeclaredInstanceField(new InstanceFieldDefinition(clsDef, fieldScanner.getFieldId(), fieldScanner.getAccessFlags()));
	}
	
	private void scanSuperclass(DexClassScanner clsScanner, ClassDefinition clsDef, ClassData baseclsData, boolean isInternal) {
		// acquire superclass info
		final java.lang.String typeDescriptor = clsScanner.getSuperclassDescriptor();
		if (typeDescriptor != null) baseclsData.superclass = DexClassType.parse(typeDescriptor, typeCache); else foundRoot(clsDef, isInternal);
	}
	
	public RuntimeHierarchy build() {
		final java.util.HashMap<uk.ac.cam.db538.dexter.dex.type.DexClassType, uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition> classList = new HashMap<DexClassType, BaseClassDefinition>();
		for (final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassVariants classDefPair : definedClasses.values()) {
			final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassData clsData = classDefPair.getClassData();
			final uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition baseCls = clsData.classDef;
			// connect to parent and vice versa
			final uk.ac.cam.db538.dexter.dex.type.DexClassType sclsType = (baseCls instanceof ClassDefinition) ? clsData.superclass : root.getType();
			if (sclsType != null) {
				final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassVariants sclsVariants = definedClasses.get(sclsType);
				if (sclsVariants == null) throw new HierarchyException("Class " + baseCls.getType().getPrettyName() + " is missing its parent " + sclsType.getPrettyName()); else baseCls.setSuperclassLink(sclsVariants.getClassData().classDef);
			}
			// proper classes only (not interfaces)
			if (baseCls instanceof ClassDefinition) {
				final uk.ac.cam.db538.dexter.hierarchy.ClassDefinition properCls = (ClassDefinition)baseCls;
				// connect to interfaces
				final java.util.Collection<uk.ac.cam.db538.dexter.dex.type.DexClassType> ifaces = clsData.interfaces;
				if (ifaces != null) {
					for (final uk.ac.cam.db538.dexter.dex.type.DexClassType ifaceType : ifaces) {
						final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassVariants ifaceInfo_Pair = definedClasses.get(ifaceType);
						if (ifaceInfo_Pair == null || !(ifaceInfo_Pair.getClassData().classDef instanceof InterfaceDefinition)) throw new HierarchyException("Class " + baseCls.getType().getPrettyName() + " is missing its interface " + ifaceType.getPrettyName()); else properCls.addImplementedInterface((InterfaceDefinition)ifaceInfo_Pair.getClassData().classDef);
					}
				}
			}
			classList.put(baseCls.getType(), baseCls);
		}
		if (root == null) throw new HierarchyException("Hierarchy is missing a root");
		return new RuntimeHierarchy(classList, root, typeCache);
	}
	
	public void removeInternalClasses() {
		final java.util.ArrayList<java.util.Map.Entry<uk.ac.cam.db538.dexter.dex.type.DexClassType, uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassVariants>> classEntries = new ArrayList<Entry<DexClassType, ClassVariants>>(definedClasses.entrySet());
		for (final java.util.Map.Entry<uk.ac.cam.db538.dexter.dex.type.DexClassType, uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassVariants> classEntry : classEntries) {
			final uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder.ClassVariants classPair = classEntry.getValue();
			classPair.deleteInternal();
			if (classPair.isEmpty()) definedClasses.remove(classEntry.getKey());
		}
	}
	private static final FilenameFilter FILTER_DEX_ODEX_JAR = new FilenameFilter(){
		
		
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".dex") || name.endsWith(".odex") || name.endsWith(".jar");
		}
	};
	
	private static class ClassVariants implements Serializable {
		private static final long serialVersionUID = 1L;
		private ClassData internal;
		private ClassData external;
		
		public ClassVariants() {
			
			this.internal = this.external = null;
		}
		
		public ClassData getClassData() {
			// prefer internal
			if (internal != null) return internal; else if (external != null) return external; else throw new HierarchyException("No class data available");
		}
		
		public void setVariant(ClassData cls, boolean isInternal) {
			if (isInternal) {
				if (this.internal != null) throw new HierarchyException("Multiple definitions of internal class " + this.internal.classDef.getType().getPrettyName());
				this.internal = cls;
			} else {
				if (this.external != null) throw new HierarchyException("Multiple definitions of external class " + this.external.classDef.getType().getPrettyName());
				this.external = cls;
			}
		}
		
		public void deleteInternal() {
			internal = null;
		}
		
		public boolean isEmpty() {
			return (external == null) && (internal == null);
		}
	}
	
	private static class ClassData implements Serializable {
		
		private static final long serialVersionUID = 1L;
		BaseClassDefinition classDef = null;
		DexClassType superclass = null;
		Collection<DexClassType> interfaces = null;
	}
	// SERIALIZATION
	private static final long serialVersionUID = 1L;
	
	public void serialize(File outputFile) throws IOException {
		final java.io.FileOutputStream fos = new FileOutputStream(outputFile);
		try {
			final java.io.ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			try {
				oos.writeObject(this);
			} finally {
				oos.close();
			}
		} finally {
			fos.close();
		}
	}
	
	public static HierarchyBuilder deserialize(File inputFile) throws IOException {
		final java.io.FileInputStream fis = new FileInputStream(inputFile);
		try {
			return deserialize(fis);
		} finally {
			fis.close();
		}
	}
	
	public static HierarchyBuilder deserialize(InputStream is) throws IOException {
		final java.io.ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
		try {
			Object hierarchy;
			try {
				hierarchy = ois.readObject();
			} catch (ClassNotFoundException ex) {
				throw new HierarchyException(ex);
			}
			if (hierarchy instanceof HierarchyBuilder) return (HierarchyBuilder)hierarchy; else throw new HierarchyException("Input file does not contain an instance of HierarchyBuilder");
		} finally {
			ois.close();
		}
	}
	// USEFUL SHORTCUTS
	public void importFrameworkFolder(File dir) throws IOException {
		String[] files = dir.list(FILTER_DEX_ODEX_JAR);
		for (String filename : files) importDex(new File(dir, filename), false);
	}
	
	public ClassRenamer importAuxiliaryDex(DexFile dexAux) {
		final uk.ac.cam.db538.dexter.dex.type.ClassRenamer classRenamer = new ClassRenamer(dexAux, this);
		typeCache.setClassRenamer(classRenamer);
		importDex(dexAux, true);
		typeCache.setClassRenamer(null);
		return classRenamer;
	}
	
	public Pair<RuntimeHierarchy, ClassRenamer> buildAgainstApp(DexFile dexApp, DexFile dexAux) {
		try {
			importDex(dexApp, true);
			final uk.ac.cam.db538.dexter.dex.type.ClassRenamer classRenamer = importAuxiliaryDex(dexAux);
			final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy runtimeHierarchy = build();
			return Pair.create(runtimeHierarchy, classRenamer);
		} finally {
			removeInternalClasses();
		}
	}
	
	@java.lang.SuppressWarnings("all")
	public DexTypeCache getTypeCache() {
		return this.typeCache;
	}
}