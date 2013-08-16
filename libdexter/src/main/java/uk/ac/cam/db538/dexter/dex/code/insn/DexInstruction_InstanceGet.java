package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction22c;

import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.InstanceFieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

import com.google.common.collect.Sets;

public class DexInstruction_InstanceGet extends DexInstruction {

  @Getter private final DexRegister regTo;
  @Getter private final DexSingleRegister regObject;
  @Getter private final InstanceFieldDefinition fieldDef;
  @Getter private final Opcode_GetPut opcode;

  public DexInstruction_InstanceGet(DexRegister to, DexSingleRegister obj, InstanceFieldDefinition fieldDef, RuntimeHierarchy hierarchy) {
    super(hierarchy);

    this.regTo = to;
    this.regObject = obj;
    this.fieldDef = fieldDef;
    this.opcode = Opcode_GetPut.getOpcodeFromType(this.fieldDef.getFieldId().getType());
    
    Opcode_GetPut.checkRegisterWidth(regTo, opcode);
  }
  
  public DexInstruction_InstanceGet(DexRegister to, DexSingleRegister obj, DexInstanceField field, RuntimeHierarchy hierarchy) {
	this(to, obj, field.getFieldDef(), hierarchy);
  }

  public static DexInstruction_InstanceGet parse(Instruction insn, CodeParserState parsingState) {
    val opcode = Opcode_GetPut.convert_IGET(insn.opcode);
    
	if (insn instanceof Instruction22c && opcode != null) {

      val hierarchy = parsingState.getHierarchy();
    	
      val insnInstanceGet = (Instruction22c) insn;
      val refItem = (FieldIdItem) insnInstanceGet.getReferencedItem();
      
      DexRegister regTo;
      if (opcode == Opcode_GetPut.Wide)
    	  regTo = parsingState.getWideRegister(insnInstanceGet.getRegisterA());
      else
    	  regTo = parsingState.getSingleRegister(insnInstanceGet.getRegisterA());
      val regObj = parsingState.getSingleRegister(insnInstanceGet.getRegisterB());
      
      val classType = DexClassType.parse(
			  refItem.getContainingClass().getTypeDescriptor(),
			  hierarchy.getTypeCache());
      val fieldId = DexFieldId.parseFieldId(
	    		refItem.getFieldName().getStringValue(),
	    		DexRegisterType.parse(
	    				refItem.getFieldType().getTypeDescriptor(),
	    				hierarchy.getTypeCache()),
	    		hierarchy.getTypeCache());
      
      InstanceFieldDefinition fieldDef = hierarchy
    		 .getClassDefinition(classType)
    		 .getAccessedInstanceField(fieldId);
      
      if (fieldDef == null)
    	  throw new InstructionParseError("Instruction references a non-existent field " + classType.getDescriptor() + "->" + fieldId);
      
      return new DexInstruction_InstanceGet(regTo, regObj, fieldDef, hierarchy);

    } else
      throw FORMAT_EXCEPTION;
  }

  @Override
  public String toString() {
    return "iget" + opcode.getAsmSuffix() + " " + regTo.toString() + ", {" + regObject.toString() + "}" + fieldDef.toString();
  }

  @Override
  public Set<? extends DexRegister> lvaReferencedRegisters() {
    return Sets.newHashSet(regObject);
  }

  @Override
  public Set<? extends DexRegister> lvaDefinedRegisters() {
    return Sets.newHashSet(regTo);
  }

  @Override
  public void accept(DexInstructionVisitor visitor) {
	visitor.visit(this);
  }

  @Override
  protected DexClassType[] throwsExceptions() {
	return this.hierarchy.getTypeCache().LIST_Error_NullPointerException;
  }
  
}
