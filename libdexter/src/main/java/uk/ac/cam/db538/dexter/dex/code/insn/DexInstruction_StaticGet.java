package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction21c;
import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.field.DexInstanceField;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition;

import com.google.common.collect.Sets;

public class DexInstruction_StaticGet extends DexInstruction {

    @Getter private final DexRegister regTo;
    @Getter private final StaticFieldDefinition fieldDef;
    @Getter private final Opcode_GetPut opcode;

    public DexInstruction_StaticGet(DexRegister to, StaticFieldDefinition fieldDef, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regTo = to;
        this.fieldDef = fieldDef;
        this.opcode = Opcode_GetPut.getOpcodeFromType(this.fieldDef.getFieldId().getType());

        Opcode_GetPut.checkRegisterWidth(regTo, opcode);
    }

    public static DexInstruction_StaticGet parse(Instruction insn, CodeParserState parsingState) {
        val opcode = Opcode_GetPut.convert_SGET(insn.opcode);

        if (insn instanceof Instruction21c && opcode != null) {

            val hierarchy = parsingState.getHierarchy();

            val insnStaticGet = (Instruction21c) insn;
            val refItem = (FieldIdItem) insnStaticGet.getReferencedItem();

            DexRegister regTo;
            if (opcode == Opcode_GetPut.Wide)
                regTo = parsingState.getWideRegister(insnStaticGet.getRegisterA());
            else
                regTo = parsingState.getSingleRegister(insnStaticGet.getRegisterA());

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

            if (fieldDef == null) {
                // This is weird, the code is accessing a non-existent method in an existing class
                // Is it actually reasonable to fake the field or not?
                // The reality is that 10% of the top 177 apps do contain such invalid code.
               fieldDef = new StaticFieldDefinition(hierarchy.getClassDefinition(classType), fieldId, AccessFlags.STATIC.getValue());
               hierarchy.getClassDefinition(classType).addDeclaredStaticField(fieldDef);
//                throw new InstructionParseError("Instruction references a non-existent field " + classType.getDescriptor() + "->" + fieldId);
			}

            return new DexInstruction_StaticGet(regTo, fieldDef, hierarchy);

        } else
            throw FORMAT_EXCEPTION;
    }

    @Override
    public String toString() {
        return "sget" + opcode.getAsmSuffix() + " " + regTo.toString() + ", " + fieldDef.toString();
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
        return this.hierarchy.getTypeCache().LIST_Error;
    }

}
