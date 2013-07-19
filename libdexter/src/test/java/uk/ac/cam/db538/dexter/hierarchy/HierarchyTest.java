package uk.ac.cam.db538.dexter.hierarchy;

import org.junit.Before;

import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder;

public class HierarchyTest {

	protected RuntimeHierarchy hierarchy;
	protected DexTypeCache typeCache;

	protected ClassDefinition classObject;
	protected ClassDefinition classInteger;
	protected ClassDefinition classLong;
	protected InterfaceDefinition classList;
	protected ClassDefinition classArrayList;
	protected ClassDefinition classLinkedList;
	protected InterfaceDefinition classMap;
	protected ClassDefinition classHashMap;
	protected ClassDefinition classThrowable;
	protected ClassDefinition classException;
	protected ClassDefinition classError;
	
	@Before
	public void setUp() throws Exception {
		hierarchy = HierarchyBuilder.deserialize(ClassLoader.getSystemResourceAsStream("hierarchy.dump")).build();
		typeCache = hierarchy.getTypeCache();
		
		DexClassType typeObject = DexClassType.parse("Ljava/lang/Object;", typeCache);
		classObject = hierarchy.getClassDefinition(typeObject);

		DexClassType typeInteger = DexClassType.parse("Ljava/lang/Integer;", typeCache);
		classInteger = hierarchy.getClassDefinition(typeInteger);

		DexClassType typeLong = DexClassType.parse("Ljava/lang/Long;", typeCache);
		classLong = hierarchy.getClassDefinition(typeLong);
		
		DexClassType typeList = DexClassType.parse("Ljava/util/List;", typeCache);
		classList = hierarchy.getInterfaceDefinition(typeList);

		DexClassType typeArrayList = DexClassType.parse("Ljava/util/ArrayList;", typeCache);
		classArrayList = hierarchy.getClassDefinition(typeArrayList);

		DexClassType typeLinkedList = DexClassType.parse("Ljava/util/LinkedList;", typeCache);
		classLinkedList = hierarchy.getClassDefinition(typeLinkedList);

		DexClassType typeMap = DexClassType.parse("Ljava/util/Map;", typeCache);
		classMap = hierarchy.getInterfaceDefinition(typeMap);

		DexClassType typeHashMap = DexClassType.parse("Ljava/util/HashMap;", typeCache);
		classHashMap = hierarchy.getClassDefinition(typeHashMap);

		DexClassType typeThrowable = DexClassType.parse("Ljava/lang/Throwable;", typeCache);
		classThrowable = hierarchy.getClassDefinition(typeThrowable);

		DexClassType typeException = DexClassType.parse("Ljava/lang/Exception;", typeCache);
		classException = hierarchy.getClassDefinition(typeException);

		DexClassType typeError = DexClassType.parse("Ljava/lang/Error;", typeCache);
		classError = hierarchy.getClassDefinition(typeError);
	}

}
