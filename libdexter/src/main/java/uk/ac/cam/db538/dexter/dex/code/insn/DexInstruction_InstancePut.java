package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction22c;

import uk.ac.cam.db538.dexter.dex.DexClass;
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

public class DexInstruction_InstancePut extends DexInstruction {

    @Getter private final DexRegister regFrom;
    @Getter private final DexSingleRegister regObject;
    @Getter private final InstanceFieldDefinition fieldDef;
    @Getter private final Opcode_GetPut opcode;

    public DexInstruction_InstancePut(DexRegister from, DexSingleRegister obj, InstanceFieldDefinition fieldDef, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regFrom = from;
        this.regObject = obj;
        this.fieldDef = fieldDef;
        this.opcode = Opcode_GetPut.getOpcodeFromType(this.fieldDef.getFieldId().getType());

        Opcode_GetPut.checkRegisterWidth(regFrom, opcode);
    }

    public static DexInstruction_InstancePut parse(Instruction insn, CodeParserState parsingState) {
        val opcode = Opcode_GetPut.convert_IPUT(insn.opcode);

        if (insn instanceof Instruction22c && opcode != null) {

            val hierarchy = parsingState.getHierarchy();

            val insnInstancePut = (Instruction22c) insn;
            val refItem = (FieldIdItem) insnInstancePut.getReferencedItem();

            DexRegister regFrom;
            if (opcode == Opcode_GetPut.Wide)
                regFrom = parsingState.getWideRegister(insnInstancePut.getRegisterA());
            else
                regFrom = parsingState.getSingleRegister(insnInstancePut.getRegisterA());
            val regObj = parsingState.getSingleRegister(insnInstancePut.getRegisterB());

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

            if (fieldDef == null) {
                fieldDef = new InstanceFieldDefinition(hierarchy.getClassDefinition(classType), fieldId, 0);
                hierarchy.getClassDefinition(classType).addDeclaredInstanceField(fieldDef);
//                throw new InstructionParseError("Instruction references a non-existent field " + classType.getDescriptor() + "->" + fieldId);
            }

            return new DexInstruction_InstancePut(regFrom, regObj, fieldDef, hierarchy);

        } else
            throw FORMAT_EXCEPTION;
    }

    @Override
    public String toString() {
        return "iput" + opcode.getAsmSuffix() + " " + regFrom.toString() + ", {" + regObject.toString() + "}" + fieldDef.toString();
    }

    @Override
    public Set<? extends DexRegister> lvaReferencedRegisters() {
        return Sets.newHashSet(regFrom, regObject);
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
