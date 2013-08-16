package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction21c;

import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;

import com.google.common.collect.Sets;

public class DexInstruction_StaticPut extends DexInstruction {

    @Getter private final DexRegister regFrom;
    @Getter private final StaticFieldDefinition fieldDef;
    @Getter private final Opcode_GetPut opcode;

    public DexInstruction_StaticPut(DexRegister to, StaticFieldDefinition fieldDef, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regFrom = to;
        this.fieldDef = fieldDef;
        this.opcode = Opcode_GetPut.getOpcodeFromType(this.fieldDef.getFieldId().getType());

        Opcode_GetPut.checkRegisterWidth(regFrom, opcode);
    }

    public static DexInstruction_StaticPut parse(Instruction insn, CodeParserState parsingState) {
        val opcode = Opcode_GetPut.convert_SPUT(insn.opcode);

        if (insn instanceof Instruction21c && opcode != null) {

            val hierarchy = parsingState.getHierarchy();

            val insnStaticPut = (Instruction21c) insn;
            val refItem = (FieldIdItem) insnStaticPut.getReferencedItem();

            DexRegister regFrom;
            if (opcode == Opcode_GetPut.Wide)
                regFrom = parsingState.getWideRegister(insnStaticPut.getRegisterA());
            else
                regFrom = parsingState.getSingleRegister(insnStaticPut.getRegisterA());

            val classType = DexClassType.parse(
                                refItem.getContainingClass().getTypeDescriptor(),
                                hierarchy.getTypeCache());
            val fieldId = DexFieldId.parseFieldId(
                              refItem.getFieldName().getStringValue(),
                              DexRegisterType.parse(
                                  refItem.getFieldType().getTypeDescriptor(),
                                  hierarchy.getTypeCache()),
                              hierarchy.getTypeCache());

            StaticFieldDefinition fieldDef = hierarchy
                                             .getBaseClassDefinition(classType)
                                             .getAccessedStaticField(fieldId);

            if (fieldDef == null)
                throw new InstructionParseError("Instruction references a non-existent field " + classType.getDescriptor() + "->" + fieldId);

            return new DexInstruction_StaticPut(regFrom, fieldDef, hierarchy);

        } else
            throw FORMAT_EXCEPTION;
    }

    @Override
    public String toString() {
        return "sput" + opcode.getAsmSuffix() + " " + regFrom.toString() + ", " + fieldDef.toString();
    }

    @Override
    public Set<? extends DexRegister> lvaReferencedRegisters() {
        return Sets.newHashSet(regFrom);
    }

    @Override
    public void accept(DexInstructionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected DexClassType[] throwsExceptions() {
        return this.hierarchy.getTypeCache().LIST_Error;
    }

}
