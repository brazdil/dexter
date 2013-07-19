package uk.ac.cam.db538.dexter.dex.code;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jf.dexlib.CodeItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class Utils {

  private static InstructionList parse(Instruction[] insns, RuntimeHierarchy hierarchy) {
	  CodeItem codeItem = CodeItem.internCodeItem(null, 16, 16, 16, null, Arrays.asList(insns), null, null);
	  CodeParserState parserCache = new CodeParserState(codeItem, hierarchy);

	  List<DexCodeElement> insnsList = new ArrayList<DexCodeElement>();
	  for (Instruction insn : insns)
		  insnsList.add(CodeParser.parseInstruction(insn, parserCache));
	  return new InstructionList(insnsList);
  }
	
  public static DexCodeElement parseAndCompare(Instruction insn, String output, RuntimeHierarchy hierarchy) {
	  InstructionList insnList = parse(new Instruction[] { insn }, hierarchy);

    assertEquals(1, insnList.size());

    DexCodeElement insnInsn = insnList.get(0);
    assertEquals(output, insnInsn.toString());

    return insnInsn;
  }

  public static void parseAndCompare(Instruction[] insns, String[] output, RuntimeHierarchy hierarchy) {
    InstructionList insnList = parse(insns, hierarchy);

    assertEquals(output.length, insnList.size());
    for (int i = 0; i < output.length; ++i)
      assertEquals(output[i], insnList.get(i).toString());
  }

  public static long numFitsInto_Signed(int bits) {
    return (1L << (bits - 1)) - 1;
  }

  public static int numFitsInto_Unsigned(int bits) {
    return (1 << bits) - 1;
  }

//  public static void instrumentAndCompare(DexCode code, String[] output) {
//    code.instrument();
//    val insnList = code.getInstructionList();
//    assertEquals(output.length, insnList.size());
//    for (int i = 0; i < output.length; ++i)
//      assertEquals(output[i], insnList.get(i).getOriginalAssembly());
//  }

  public static TypeIdItem getTypeItem(String desc) {
    return TypeIdItem.internTypeIdItem(new DexFile(), desc);
  }

  public static StringIdItem getStringItem(String str) {
    return StringIdItem.internStringIdItem(new DexFile(), str);
  }

  public static FieldIdItem getFieldItem(String classTypeName, String fieldTypeName, String fieldName) {
    DexFile dexFile = new DexFile();
    TypeIdItem classTypeItem = TypeIdItem.internTypeIdItem(dexFile, classTypeName);
    TypeIdItem fieldTypeItem = TypeIdItem.internTypeIdItem(dexFile, fieldTypeName);
    StringIdItem fieldNameItem = StringIdItem.internStringIdItem(dexFile, fieldName);
    return FieldIdItem.internFieldIdItem(dexFile, classTypeItem, fieldTypeItem, fieldNameItem);
  }
}
