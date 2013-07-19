package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jf.dexlib.DexFile;
import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.ProtoIdItem;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction35c;
import org.jf.dexlib.Code.Format.Instruction3rc;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleOriginalRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideOriginalRegister;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_Invoke_Test extends HierarchyTest {

  @Test
  public void testParse_Invoke_Standard_RegisterParsing_Static() throws InstructionParseError {
    DexFile file = new DexFile();
    TypeIdItem classType = TypeIdItem.internTypeIdItem(file, "Lcom.test;");
    TypeIdItem returnType = TypeIdItem.internTypeIdItem(file, "V");
    TypeIdItem intType = TypeIdItem.internTypeIdItem(file, "I");
    StringIdItem methodName = StringIdItem.internStringIdItem(file, "myMethod");
    for (int i = 0; i <= 5; ++i) {
      List<TypeIdItem> paramsList = new LinkedList<TypeIdItem>();
      for (int j = 0; j < i; ++j)
        paramsList.add(intType);

      TypeListItem paramsItem = TypeListItem.internTypeListItem(file, paramsList);
      ProtoIdItem protoItem = ProtoIdItem.internProtoIdItem(file, returnType, paramsItem);
      MethodIdItem methodItem = MethodIdItem.internMethodIdItem(file, classType, protoItem, methodName);

      Utils.parseAndCompare(
        new Instruction35c(Opcode.INVOKE_STATIC, (byte) i, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, methodItem),
        (i == 0) ? "invoke-static com.test.myMethod()"
        : (i == 1) ? "invoke-static com.test.myMethod(v11)"
        : (i == 2) ? "invoke-static com.test.myMethod(v11, v12)"
        : (i == 3) ? "invoke-static com.test.myMethod(v11, v12, v13)"
        : (i == 4) ? "invoke-static com.test.myMethod(v11, v12, v13, v14)"
        : "invoke-static com.test.myMethod(v11, v12, v13, v14, v15)",
        this.hierarchy);
    }
  }

  @Test
  public void testParse_Invoke_Standard_RegisterParsing_NonStatic() throws InstructionParseError {
    DexFile file = new DexFile();
    TypeIdItem classType = TypeIdItem.internTypeIdItem(file, "Lcom.test;");
    TypeIdItem returnType = TypeIdItem.internTypeIdItem(file, "V");
    TypeIdItem intType = TypeIdItem.internTypeIdItem(file, "I");
    StringIdItem methodName = StringIdItem.internStringIdItem(file, "myMethod");
    for (int i = 0; i <= 4; ++i) {
      List<TypeIdItem> paramsList = new LinkedList<TypeIdItem>();
      for (int j = 0; j < i; ++j)
        paramsList.add(intType);

      TypeListItem paramsItem = TypeListItem.internTypeListItem(file, paramsList);
      ProtoIdItem protoItem = ProtoIdItem.internProtoIdItem(file, returnType, paramsItem);
      MethodIdItem methodItem = MethodIdItem.internMethodIdItem(file, classType, protoItem, methodName);

      Utils.parseAndCompare(
        new Instruction35c(Opcode.INVOKE_DIRECT, (byte) i + 1, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, methodItem),
        (i == 0) ? "invoke-direct com.test.myMethod{v11}()"
        : (i == 1) ? "invoke-direct com.test.myMethod{v11}(v12)"
        : (i == 2) ? "invoke-direct com.test.myMethod{v11}(v12, v13)"
        : (i == 3) ? "invoke-direct com.test.myMethod{v11}(v12, v13, v14)"
        : "invoke-direct com.test.myMethod{v11}(v12, v13, v14, v15)",
        this.hierarchy);
    }
  }

  @Test
  public void testParse_Invoke_Standard_CallTypes() throws InstructionParseError {
    DexFile file = new DexFile();
    TypeIdItem classType = TypeIdItem.internTypeIdItem(file, "Lcom.test;");
    TypeIdItem returnType = TypeIdItem.internTypeIdItem(file, "V");
    TypeIdItem intType = TypeIdItem.internTypeIdItem(file, "I");
    StringIdItem methodName = StringIdItem.internStringIdItem(file, "myMethod");

    List<TypeIdItem> paramsList = new LinkedList<TypeIdItem>();
    paramsList.add(intType);

    TypeListItem paramsItem = TypeListItem.internTypeListItem(file, paramsList);
    ProtoIdItem protoItem = ProtoIdItem.internProtoIdItem(file, returnType, paramsItem);
    MethodIdItem methodItem = MethodIdItem.internMethodIdItem(file, classType, protoItem, methodName);

    Utils.parseAndCompare(
      new Instruction[] {
        new Instruction35c(Opcode.INVOKE_STATIC, (byte) 1, (byte) 11, (byte) 0, (byte) 0, (byte) 0, (byte) 0, methodItem),
        new Instruction35c(Opcode.INVOKE_VIRTUAL, (byte) 2, (byte) 11, (byte) 12, (byte) 0, (byte) 0, (byte) 0, methodItem),
        new Instruction35c(Opcode.INVOKE_DIRECT, (byte) 2, (byte) 11, (byte) 12, (byte) 0, (byte) 0, (byte) 0, methodItem),
        new Instruction35c(Opcode.INVOKE_SUPER, (byte) 2, (byte) 11, (byte) 12, (byte) 0, (byte) 0, (byte) 0, methodItem),
        new Instruction35c(Opcode.INVOKE_INTERFACE, (byte) 2, (byte) 11, (byte) 12, (byte) 0, (byte) 0, (byte) 0, methodItem)
      }, new String[] {
        "invoke-static com.test.myMethod(v11)",
        "invoke-virtual com.test.myMethod{v11}(v12)",
        "invoke-direct com.test.myMethod{v11}(v12)",
        "invoke-super com.test.myMethod{v11}(v12)",
        "invoke-interface com.test.myMethod{v11}(v12)"
      }, this.hierarchy);
  }

  @Test
  public void testParse_Invoke_Range() throws InstructionParseError {
    DexFile file = new DexFile();
    TypeIdItem classType = TypeIdItem.internTypeIdItem(file, "Lcom.test;");
    TypeIdItem returnType = TypeIdItem.internTypeIdItem(file, "V");
    TypeIdItem intType = TypeIdItem.internTypeIdItem(file, "I");
    StringIdItem methodName = StringIdItem.internStringIdItem(file, "myMethod");

    List<TypeIdItem> paramsList = new LinkedList<TypeIdItem>();
    for (int j = 0; j < 10; ++j)
      paramsList.add(intType);

    TypeListItem paramsItem = TypeListItem.internTypeListItem(file, paramsList);
    ProtoIdItem protoItem = ProtoIdItem.internProtoIdItem(file, returnType, paramsItem);
    MethodIdItem methodItem = MethodIdItem.internMethodIdItem(file, classType, protoItem, methodName);

    Utils.parseAndCompare(
      new Instruction[] {
        new Instruction3rc(Opcode.INVOKE_STATIC_RANGE, (short) 10, 48000 , methodItem),
        new Instruction3rc(Opcode.INVOKE_VIRTUAL_RANGE, (short) 11, 48000 , methodItem),
        new Instruction3rc(Opcode.INVOKE_DIRECT_RANGE, (short) 11, 48000 , methodItem),
        new Instruction3rc(Opcode.INVOKE_SUPER_RANGE, (short) 11, 48000 , methodItem),
        new Instruction3rc(Opcode.INVOKE_INTERFACE_RANGE, (short) 11, 48000 , methodItem)
      }, new String[] {
        "invoke-static com.test.myMethod(v48000, v48001, v48002, v48003, v48004, v48005, v48006, v48007, v48008, v48009)",
        "invoke-virtual com.test.myMethod{v48000}(v48001, v48002, v48003, v48004, v48005, v48006, v48007, v48008, v48009, v48010)",
        "invoke-direct com.test.myMethod{v48000}(v48001, v48002, v48003, v48004, v48005, v48006, v48007, v48008, v48009, v48010)",
        "invoke-super com.test.myMethod{v48000}(v48001, v48002, v48003, v48004, v48005, v48006, v48007, v48008, v48009, v48010)",
        "invoke-interface com.test.myMethod{v48000}(v48001, v48002, v48003, v48004, v48005, v48006, v48007, v48008, v48009, v48010)"
      }, this.hierarchy);
  }

  @Test
  public void testCheckArguments_Static_Correct() {
    DexTypeCache cache = this.hierarchy.getTypeCache();
    List<DexRegisterType> params = Arrays.asList(new DexRegisterType[] {
                                 DexRegisterType.parse("J", cache)
                               });
    List<DexStandardRegister> regs = Arrays.asList(new DexStandardRegister[] {
                               new DexWideOriginalRegister(1)
                             });

    new DexInstruction_Invoke(this.classLong.getType(),
                              DexMethodId.parseMethodId(
                            		  "valueOf",
                            		  new DexPrototype(this.classLong.getType(), params),
                            		  cache),
                              regs,
                              Opcode_Invoke.Static,
                              this.hierarchy);
  }

  @Test(expected=Error.class)
  public void testCheckArguments_Static_Incorrect() {
	    DexTypeCache cache = this.hierarchy.getTypeCache();
	    List<DexRegisterType> params = Arrays.asList(new DexRegisterType[] {
	                                 DexRegisterType.parse("J", cache)
	                               });
	    List<DexStandardRegister> regs = Arrays.asList(new DexStandardRegister[] {
	                               new DexSingleOriginalRegister(1)
	                             });

	    new DexInstruction_Invoke(this.classLong.getType(),
	                              DexMethodId.parseMethodId(
	                            		  "valueOf",
	                            		  new DexPrototype(this.classLong.getType(), params),
	                            		  cache),
	                              regs,
	                              Opcode_Invoke.Static,
	                              this.hierarchy);
  }
}
