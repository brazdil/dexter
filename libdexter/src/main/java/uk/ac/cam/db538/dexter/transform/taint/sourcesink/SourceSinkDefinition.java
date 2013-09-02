package uk.ac.cam.db538.dexter.transform.taint.sourcesink;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.transform.MethodCall;
import uk.ac.cam.db538.dexter.transform.taint.CodeGenerator;

public abstract class SourceSinkDefinition {
	
	protected final MethodCall methodCall;
	protected final RuntimeHierarchy hierarchy;
	
	protected SourceSinkDefinition(MethodCall methodCall) {
		this.methodCall = methodCall;
		this.hierarchy = methodCall.getInvoke().getHierarchy();
	}
	
	protected abstract boolean isApplicable();
	
	public DexCodeElement insertBefore(CodeGenerator codeGen) { return codeGen.empty(); }
	public DexCodeElement insertAfter(CodeGenerator codeGen) { return codeGen.empty(); }
	
	private static final void addDef(List<SourceSinkDefinition> list, SourceSinkDefinition def) {
		if (def.isApplicable())
			list.add(def);
	}
	
	public static final SourceSinkDefinition findApplicableDefinition(MethodCall methodCall) {
		List<SourceSinkDefinition> list = new ArrayList<SourceSinkDefinition>();
		
		// Add new source/sink definitions here
		addDef(list, new Source_Query(methodCall));
		addDef(list, new Source_SystemService(methodCall));
		addDef(list, new Source_Browser(methodCall));
		
		if (list.isEmpty())
			return null;
		else if (list.size() == 1)
			return list.get(0);
		else
			throw new AssertionError("Multiple source/sink definitions applicable to a method call");
	}
	
	protected boolean isVirtualCall() {
		Opcode_Invoke opcode = methodCall.getInvoke().getCallType();
		return opcode == Opcode_Invoke.Virtual || opcode == Opcode_Invoke.Interface;
	}
	
	protected boolean isStaticCall() {
		return methodCall.getInvoke().isStaticCall();
	}
	
	protected boolean classIsChildOf(String desc) {
		DexReferenceType calledOn = methodCall.getInvoke().getClassType();
		
		BaseClassDefinition calledOnDef = hierarchy.getBaseClassDefinition(calledOn);
		BaseClassDefinition descDef = hierarchy.getBaseClassDefinition(DexReferenceType.parse(desc, hierarchy.getTypeCache()));
		
		return calledOnDef.isChildOf(descDef);
	}
	
	protected boolean methodIsCalled(String name) {
		return methodCall.getInvoke()
				.getMethodId()
				.getName()
				.equals(name);
	}
	
	protected boolean movesResult() {
		return methodCall.movesResult();
	}
	
	protected boolean returnTypeIs(String desc) {
		return methodCall.getInvoke()
				.getMethodId()
				.getPrototype()
				.getReturnType()
				.getDescriptor()
				.equals(desc);
	}
	
	protected boolean paramIsOfType(int index, String desc) {
		return methodCall.getInvoke()
				.getMethodId()
				.getPrototype()
				.getParameterType(index, isStaticCall(), methodCall.getInvoke().getClassType())
				.getDescriptor()
				.equals(desc);
	}
	
	protected DexRegister getParamRegister(int index) {
		return methodCall.getInvoke().getArgumentRegisters().get(index);
	}
	
	protected DexRegister getResultRegister() {
		assert movesResult();
		return methodCall.getResult().getRegTo();
	}
}
