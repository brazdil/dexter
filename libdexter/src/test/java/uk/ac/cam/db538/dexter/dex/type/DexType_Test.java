package uk.ac.cam.db538.dexter.dex.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DexType_Test {

  @Test(expected=UnknownTypeException.class)
  public void testIncorrectType() throws UnknownTypeException {
    DexType.parse("X", new DexTypeCache());
  }

  @Test
  public void testVoid() throws UnknownTypeException {
    DexType type = DexType.parse("V", new DexTypeCache());
    assertTrue(type instanceof DexVoid);
    
    assertEquals("V", DexType.jvm2dalvik("void"));
  }

  @Test
  public void testByte() throws UnknownTypeException {
    DexType type = DexType.parse("B", new DexTypeCache());
    assertTrue(type instanceof DexByte);
    assertEquals(1, ((DexRegisterType) type).getRegisters());
    
    assertEquals("B", DexType.jvm2dalvik("byte"));
  }

  @Test
  public void testBoolean() throws UnknownTypeException {
    DexType type = DexType.parse("Z", new DexTypeCache());
    assertTrue(type instanceof DexBoolean);
    assertEquals(1, ((DexRegisterType) type).getRegisters());
    
    assertEquals("Z", DexType.jvm2dalvik("boolean"));
  }

  @Test
  public void testShort() throws UnknownTypeException {
    DexType type = DexType.parse("S", new DexTypeCache());
    assertTrue(type instanceof DexShort);
    assertEquals(1, ((DexRegisterType) type).getRegisters());
    
    assertEquals("S", DexType.jvm2dalvik("short"));
  }

  @Test
  public void testChar() throws UnknownTypeException {
    DexType type = DexType.parse("C", new DexTypeCache());
    assertTrue(type instanceof DexChar);
    assertEquals(1, ((DexRegisterType) type).getRegisters());
    
    assertEquals("C", DexType.jvm2dalvik("char"));
}

  @Test
  public void testInteger() throws UnknownTypeException {
    DexType type = DexType.parse("I", new DexTypeCache());
    assertTrue(type instanceof DexInteger);
    assertEquals(1, ((DexRegisterType) type).getRegisters());
    
    assertEquals("I", DexType.jvm2dalvik("int"));
}

  @Test
  public void testLong() throws UnknownTypeException {
    DexType type = DexType.parse("J", new DexTypeCache());
    assertTrue(type instanceof DexLong);
    assertEquals(2, ((DexRegisterType) type).getRegisters());
    
    assertEquals("J", DexType.jvm2dalvik("long"));
}

  @Test
  public void testFloat() throws UnknownTypeException {
    DexType type = DexType.parse("F", new DexTypeCache());
    assertTrue(type instanceof DexFloat);
    assertEquals(1, ((DexRegisterType) type).getRegisters());
    
    assertEquals("F", DexType.jvm2dalvik("float"));
}

  @Test
  public void testDouble() throws UnknownTypeException {
    DexType type = DexType.parse("D", new DexTypeCache());
    assertTrue(type instanceof DexDouble);
    assertEquals(2, ((DexRegisterType) type).getRegisters());
    
    assertEquals("D", DexType.jvm2dalvik("double"));
}

  @Test
  public void testClassType() throws UnknownTypeException {
    DexType type = DexType.parse("Ljava.lang.String;", new DexTypeCache());
    assertTrue(type instanceof DexClassType);
    assertEquals(1, ((DexRegisterType) type).getRegisters());

    DexClassType classType = (DexClassType) type;
    assertEquals("java.lang.String", classType.getPrettyName());
    assertEquals("java.lang", classType.getPackageName());
    assertEquals("String", classType.getShortName());
    
    assertEquals("Ljava/lang/String;", DexType.jvm2dalvik("Ljava.lang.String;"));
    assertEquals("Ljava/lang/String;", DexType.jvm2dalvik("java.lang.String"));
  }

  @Test(expected=UnknownTypeException.class)
  public void testClassType_MissingSemicolon() throws UnknownTypeException {
    DexType.parse("Ljava.lang.String", new DexTypeCache());
  }

  @Test
  public void testClassType_DefaultPackage() throws UnknownTypeException {
    DexType type = DexType.parse("LTestClass;", new DexTypeCache());
    assertTrue(type instanceof DexClassType);

    DexClassType classType = (DexClassType) type;
    assertEquals("TestClass", classType.getPrettyName());
    assertNull(classType.getPackageName());
    assertEquals("TestClass", classType.getShortName());
  }

  @Test
  public void testClassType_Cache() throws UnknownTypeException {
    DexTypeCache cache = new DexTypeCache();
    DexType type1 = DexType.parse("Ljava.lang.String;", cache);
    DexType type2 = DexType.parse("Ljava.lang.String;", cache);
    assertTrue(type1 == type2);
  }

  @Test
  public void testArrayType() throws UnknownTypeException {
    DexType type = DexType.parse("[I", new DexTypeCache());
    assertTrue(type instanceof DexArrayType);
    assertTrue(((DexArrayType) type).getElementType() instanceof DexInteger);
    assertEquals(1, ((DexRegisterType) type).getRegisters());

    assertEquals("[I", DexType.jvm2dalvik("[I"));
    assertEquals("[[I", DexType.jvm2dalvik("[[I"));
    assertEquals("[Ljava/lang/String;", DexType.jvm2dalvik("[Ljava.lang.String;"));
  }

  @Test
  public void testArrayType_Cache() throws UnknownTypeException {
    DexTypeCache cache = new DexTypeCache();
    DexType type1 = DexType.parse("[I", cache);
    DexType type2 = DexType.parse("[I", cache);
    assertTrue(type1 == type2);
  }

  @Test(expected=UnknownTypeException.class)
  public void testArrayType_UnknownElementType() throws UnknownTypeException {
    DexType.parse("[X", new DexTypeCache());
  }
}
