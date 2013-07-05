package uk.ac.cam.db538.dexter.hierarchy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.val;

import org.jf.dexlib.ClassDataItem.EncodedMethod;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.DexFile.NoClassesDexException;
import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;

public class HierarchyBuilder {

	private boolean foundRoot = false;
	private final DexTypeCache typeCache;
	
	private final Map<DexClassType, BaseClassDefinition> definedClasses;
	
	private final Map<BaseClassDefinition, DexClassType> superclasses;
	private final Map<BaseClassDefinition, Set<MethodData>> methods;
	private final Map<BaseClassDefinition, Set<FieldData>> staticFields;

	private final Map<ClassDefinition, Set<DexClassType>> interfaces;
	private final Map<ClassDefinition, Set<FieldData>> instanceFields;
	
	public HierarchyBuilder(DexTypeCache cache) {
		typeCache = cache;
		
		definedClasses = new HashMap<DexClassType, BaseClassDefinition>();
		
		superclasses = new HashMap<BaseClassDefinition, DexClassType>();
		methods = new HashMap<BaseClassDefinition, Set<MethodData>>();
		staticFields = new HashMap<BaseClassDefinition, Set<FieldData>>();

		interfaces = new HashMap<ClassDefinition, Set<DexClassType>>();
		instanceFields = new HashMap<ClassDefinition, Set<FieldData>>();
	}

	public void scanDexFolder(File dir, HierarchyScanCallback callback) throws IOException {
		String[] files = dir.list(FILTER_DEX_ODEX_JAR);
		
		if (callback != null) callback.onFolderScanStarted(dir, files.length);
		
		for (String filename : files)
			scanDex(new File(dir, filename), callback);
		
		if (callback != null) callback.onFolderScanFinished(dir, files.length);
	}
	
	public void scanDex(File file, HierarchyScanCallback callback) throws IOException {
		if (callback != null) callback.onFileScanStarted(file);
		
		// parse the file
		DexFile dex;
		try {
			dex = new DexFile(file, false, true);
		} catch (NoClassesDexException e) {
			// file does not contain classes.dex
			if (callback != null) callback.onFileScanFinished(file);
			return;
		}

		// recursively scan classes
		for (val cls : dex.ClassDefsSection.getItems())
			scanClass(cls);
		
		// explicitly dispose of the object
		dex = null;
		System.gc();

		if (callback != null) callback.onFileScanFinished(file);
	}
	
	private void scanClass(ClassDefItem cls) {
		val clsData = cls.getClassData();
		val clsType = DexClassType.parse(cls.getClassType().getTypeDescriptor(), typeCache);
		
		// check that class has not been defined before
		if (definedClasses.containsKey(clsType))
			throw new HierarchyException("Multiple definition of class " + clsType.getPrettyName());
		
		// examine access flags
		int iFlags = cls.getAccessFlags();
		List<AccessFlags> listFlags = Arrays.asList(AccessFlags.getAccessFlagsForClass(cls.getAccessFlags()));
		
		// acquire superclass info
		DexClassType superclsType = null;
		boolean isRoot = false;
		val superclsTypeItem = cls.getSuperclass();
		if (superclsTypeItem == null)
			isRoot = true;
		else
			superclsType = DexClassType.parse(superclsTypeItem.getTypeDescriptor(), typeCache);

		// check only one root exists
		if (isRoot) {
			if (foundRoot)
				throw new HierarchyException("More than one hierarchy root found");
			else
				foundRoot = true;
		}
		
		// create ClassInfo instance and store
		BaseClassDefinition clsInfo;
		if (listFlags.contains(AccessFlags.INTERFACE))
			clsInfo = new InterfaceDefinition(clsType, iFlags);
		else
			clsInfo = new ClassDefinition(clsType, cls.getAccessFlags(), isRoot);
		
		// examine methods
		Set<MethodData> clsMethods = new HashSet<MethodData>();
		if (clsData != null) {
			val methodItems = new ArrayList<EncodedMethod>(clsData.getDirectMethodCount() + clsData.getVirtualMethodCount());
			methodItems.addAll(clsData.getDirectMethods());
			methodItems.addAll(clsData.getVirtualMethods());
			for (val methodItem : methodItems) {
				val mId = DexMethodId.parseMethodId(methodItem.method, typeCache);
				clsMethods.add(new MethodData(mId, methodItem.accessFlags));
			}
		}
		methods.put(clsInfo, clsMethods);
		
		// examine static fields
		if (clsData != null) {
			val clsStaticFields = new HashSet<FieldData>();
			for (val fieldItem : clsData.getStaticFields())
				clsStaticFields.add(new FieldData(
						DexFieldId.parseFieldId(fieldItem.field, typeCache),
						fieldItem.accessFlags));
			staticFields.put(clsInfo, clsStaticFields);
		}
		
		// examine interfaces (proper classes only)
		if (clsInfo instanceof ClassDefinition && cls.getInterfaces() != null) {
			val clsInterfaces = new HashSet<DexClassType>();
			for (val ifaceTypeItem : cls.getInterfaces().getTypes())
				clsInterfaces.add(DexClassType.parse(ifaceTypeItem.getTypeDescriptor(), typeCache));
			interfaces.put((ClassDefinition) clsInfo, clsInterfaces);
		}
		
		// examine instance fields (proper classes only)
		if (clsInfo instanceof ClassDefinition && clsData != null) {
			val clsInstanceFields = new HashSet<FieldData>();
			for (val fieldItem : clsData.getInstanceFields())
				clsInstanceFields.add(new FieldData(
						DexFieldId.parseFieldId(fieldItem.field, typeCache),
						fieldItem.accessFlags));
			instanceFields.put((ClassDefinition) clsInfo, clsInstanceFields);
		}

		// store data
		superclasses.put(clsInfo, superclsType);
		definedClasses.put(clsType, clsInfo);
	}
	
	
	public RuntimeHierarchy build() {
		for (val baseCls : definedClasses.values()) {

			// connect to parent and vice versa
			val sclsType = superclasses.get(baseCls);
			if (sclsType != null) {
				val sclsInfo = definedClasses.get(sclsType);
				if (sclsInfo == null)
					throw new HierarchyException("Class " + baseCls.getClassType().getPrettyName() + " is missing its parent " + sclsType.getPrettyName());
				else
					baseCls.setSuperclassLink(sclsInfo);
			}

			// build methods and connect to the class
			for (val method : methods.get(baseCls)) {
				val methodInfo = new MethodDefinition(baseCls, method.methodId, method.accessFlags);
				baseCls.addDeclaredMethod(methodInfo);
			}
			
			// build static fields
			val sfields = staticFields.get(baseCls);
			if (sfields != null) {
				for (val sfield : sfields) {
					val sfieldInfo = new StaticFieldDefinition(baseCls, sfield.fieldId, sfield.accessFlags);
					baseCls.addDeclaredStaticField(sfieldInfo);
				}
			}

			// proper classes only (not interfaces)
			if (baseCls instanceof ClassDefinition) {
				val properCls = (ClassDefinition) baseCls;
				
				// connect to interfaces
				val ifaces = interfaces.get(properCls);
				if (ifaces != null) {
					for (val ifaceType : ifaces) {
						val ifaceInfo = definedClasses.get(ifaceType);
						if (ifaceInfo == null || !(ifaceInfo instanceof InterfaceDefinition))
							throw new HierarchyException("Class " + baseCls.getClassType().getPrettyName() + " is missing its interface " + ifaceType.getPrettyName());
						else
							properCls.addImplementedInterface((InterfaceDefinition) ifaceInfo);
					}
				}
				
				// build instance fields
				val ifields = instanceFields.get(properCls);
				if (ifields != null) {
					for (val ifield : ifields) {
						val ifieldInfo = new InstanceFieldDefinition(properCls, ifield.fieldId, ifield.accessFlags);
						properCls.addDeclaredInstanceField(ifieldInfo);
					}
				}
			}
		}
		
		return new RuntimeHierarchy(definedClasses);
	}
	
	private static final FilenameFilter FILTER_DEX_ODEX_JAR = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".dex") || name.endsWith(".odex") || name.endsWith(".jar"); 
		}
	};
	
	@AllArgsConstructor
	private static class MethodData {
		DexMethodId methodId;
		int accessFlags;
	}

	@AllArgsConstructor
	private static class FieldData {
		DexFieldId fieldId;
		int accessFlags;
	}
}