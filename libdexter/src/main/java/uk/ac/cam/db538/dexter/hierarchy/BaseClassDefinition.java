package uk.ac.cam.db538.dexter.hierarchy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jf.dexlib.Util.AccessFlags;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;

public abstract class BaseClassDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	private final DexClassType type;
	private final int accessFlags;
	private final boolean internal;
	private BaseClassDefinition superclass;
	private final Set<BaseClassDefinition> _children;
	private final Set<BaseClassDefinition> children;
	private final Set<MethodDefinition> _methods;
	private final Set<MethodDefinition> methods;
	private final Set<StaticFieldDefinition> _staticFields;
	private final Set<StaticFieldDefinition> staticFields;
	
	BaseClassDefinition(DexClassType type, int accessFlags, boolean isInternal) {
		
		this.type = type;
		this.accessFlags = accessFlags;
		this.internal = isInternal;
		this.superclass = null;
		this._children = new HashSet<BaseClassDefinition>();
		this.children = Collections.unmodifiableSet(this._children);
		this._methods = new HashSet<MethodDefinition>();
		this.methods = Collections.unmodifiableSet(this._methods);
		this._staticFields = new HashSet<StaticFieldDefinition>();
		this.staticFields = Collections.unmodifiableSet(this._staticFields);
	}
	// only to be called by HierarchyBuilder
	public void setSuperclassLink(BaseClassDefinition superclass) {
		this.superclass = superclass;
		this.superclass._children.add(this);
	}
	
	public void addDeclaredMethod(MethodDefinition method) {
		assert method.getParentClass() == this;
		this._methods.add(method);
	}
	
	public void addDeclaredStaticField(StaticFieldDefinition field) {
		assert field.isStatic();
		assert field.getParentClass() == this;
		this._staticFields.add(field);
	}
	
	public EnumSet<AccessFlags> getAccessFlags() {
		AccessFlags[] flags = AccessFlags.getAccessFlagsForClass(accessFlags);
		if (flags.length == 0) return EnumSet.noneOf(AccessFlags.class); else return EnumSet.of(flags[0], flags);
	}
	
	public boolean isAbstract() {
		return getAccessFlags().contains(AccessFlags.ABSTRACT);
	}
	
	public boolean isInterface() {
		return getAccessFlags().contains(AccessFlags.INTERFACE);
	}
	
	public boolean isRoot() {
		return this.getSuperclass() == null;
	}
	
	public boolean isChildOf(BaseClassDefinition parent) {
		BaseClassDefinition inspected = this;
		while (true) {
			if (inspected.equals(parent)) return true; else if (inspected.isRoot()) return false; else inspected = inspected.getSuperclass();
		}
	}
	
	public MethodDefinition getMethod(DexMethodId methodId) {
		for (final uk.ac.cam.db538.dexter.hierarchy.MethodDefinition methodDef : this.methods) if (methodDef.getMethodId().equals(methodId)) return methodDef;
		return null;
	}
	
	public StaticFieldDefinition getStaticField(DexFieldId fieldId) {
		for (final uk.ac.cam.db538.dexter.hierarchy.StaticFieldDefinition fieldDef : this.staticFields) if (fieldDef.getFieldId().equals(fieldId)) return fieldDef;
		return null;
	}
	
	public static enum CallDestinationType {
		Internal,
		External,
		Undecidable;
		
	}
	
	public CallDestinationType getMethodDestinationType(DexMethodId methodId, Opcode_Invoke opcode) {
		boolean foundExternal = false;
		boolean foundInternal = false;
		for (final uk.ac.cam.db538.dexter.hierarchy.MethodDefinition callableImplementation : getCallableMethodImplementations(methodId, opcode)) if (callableImplementation.getParentClass().isInternal()) foundInternal = true; else foundExternal = true;
		if (foundInternal && foundExternal) return CallDestinationType.Undecidable; else if (foundInternal) return CallDestinationType.Internal; else return CallDestinationType.External;
	}
	
	private List<MethodDefinition> getCallableMethodImplementations(DexMethodId methodId, Opcode_Invoke opcode) {
		switch (opcode) {
		case Direct: 
			return callableMethodImplementations_Direct(methodId);
		
		case Static: 
			return callableMethodImplementations_Static(methodId);
		
		case Super: 
			return callableMethodImplementations_Super(methodId);
		
		case Virtual: 
			return callableMethodImplementations_Virtual(methodId);
		
		case Interface: 
			return callableMethodImplementations_Interface(methodId);
		
		default: 
			throw new Error("Unknown opcode");
		
		}
	}
	
	private List<MethodDefinition> callableMethodImplementations_Direct(DexMethodId methodId) {
		// Direct calls are for private methods and constructors
		// They are always made on the implementing class, i.e. it must be a proper class
		final uk.ac.cam.db538.dexter.hierarchy.MethodDefinition methodDef = getMethod(methodId);
		if (methodDef != null && this instanceof ClassDefinition && !methodDef.isStatic() && (methodDef.isPrivate() || methodDef.isConstructor())) return Arrays.asList(methodDef); else throw new HierarchyException("Invalid method call (direct call destination not found)");
	}
	
	private List<MethodDefinition> callableMethodImplementations_Static(final DexMethodId methodId) {
		// Static calls invoke a static method in the class itself 
		// or the closest parent that implements it
		// They can be applied on any kind of class
		MethodDefinition methodDef = iterateThroughParents(methodId, extractorMethod, acceptorStaticCalls, false); // start iterating with this class
		if (methodDef != null) return Arrays.asList(methodDef); else throw new HierarchyException("Invalid method call (static call destination not found)");
	}
	
	private List<MethodDefinition> callableMethodImplementations_Super(final DexMethodId methodId) {
		// Super calls invoke a non-private method  
		// in the closest parent that implements it
		// They can be applied on any kind of class
		MethodDefinition methodDef = iterateThroughParents(methodId, extractorMethod, acceptorVirtualCall, false); // this is the superclass already, so don't skip first
		if (methodDef != null) return Arrays.asList(methodDef); else throw new HierarchyException("Invalid method call (super call destination not found)");
	}
	
	private List<MethodDefinition> callableMethodImplementations_Virtual(DexMethodId methodId) {
		// Virtual calls invoke a non-private, non-static method
		// in the class itself, in the closest parent or in any of the children
		if (this instanceof ClassDefinition) {
			List<MethodDefinition> fromChildren = iterateThroughChildren(methodId, extractorMethod, acceptorVirtualCall);
			MethodDefinition fromParents = iterateThroughParents(methodId, extractorMethod, acceptorVirtualCall, true); // no need to scan this class twice
			if (fromParents != null) fromChildren.add(fromParents);
			return fromChildren;
		} else throw new HierarchyException("Invalid method call (virtual call made on non-class)");
	}
	
	private List<MethodDefinition> callableMethodImplementations_Interface(DexMethodId methodId) {
		// Interface calls invoke a non-private, non-static method
		// in one of the classes that implement the given interface,
		// or any of its children
		if (this instanceof InterfaceDefinition) {
			final java.util.ArrayList<uk.ac.cam.db538.dexter.hierarchy.MethodDefinition> list = new ArrayList<MethodDefinition>();
			for (final uk.ac.cam.db538.dexter.hierarchy.ClassDefinition implementor : ((InterfaceDefinition)this).getImplementors()) list.addAll(implementor.iterateThroughChildren(methodId, extractorMethod, acceptorVirtualCall));
			return list;
		} else throw new HierarchyException("Invalid method call (interface method call on non-class)");
	}
	
	public StaticFieldDefinition getAccessedStaticField(DexFieldId fieldId) {
		// Application can access a static field on class X, but
		// the field might actually be defined in one of X's parents
		// This method will return the definition of the field 
		// in itself or the closest parent
		return iterateThroughParents(fieldId, extractorStaticField, acceptorAlwaysTrue, false);
	}
	
	public BaseClassDefinition getCommonParent(BaseClassDefinition otherClass) {
		// Iterate through parents of this class (including itself) and return the first
		// class that is the parent of the otherClass passed in as a parameter
		// Note that the work is done in the extractor
		return iterateThroughParents(otherClass, extractorParentClass, acceptorAlwaysTrue, false);
	}
	
	protected <Id, T> T iterateThroughParents(Id id, Extractor<Id, T> extractor, Acceptor<? super T> acceptor, boolean skipFirst) {
		BaseClassDefinition inspectedClass = skipFirst ? this.getSuperclass() : this;
		while (true) {
			T def = extractor.extract(inspectedClass, id);
			if (def != null) {
				if (acceptor.accept(def)) return def;
			}
			if (inspectedClass.isRoot()) return null; else inspectedClass = inspectedClass.getSuperclass();
		}
	}
	
	protected <Id, T> List<T> iterateThroughChildren(Id id, Extractor<Id, T> extractor, Acceptor<? super T> acceptor) {
		final java.util.ArrayList<T> list = new ArrayList<T>();
		T def = extractor.extract(this, id);
		if (def != null) {
			if (acceptor.accept(def)) list.add(def);
		}
		for (final uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition child : getChildren()) list.addAll(child.iterateThroughChildren(id, extractor, acceptor));
		return list;
	}
	
	protected static interface Acceptor<T> {
		
		boolean accept(T item);
	}
	
	protected static interface Extractor<Id, T> {
		
		T extract(BaseClassDefinition clazz, Id id);
	}
	private static final Extractor<BaseClassDefinition, BaseClassDefinition> extractorParentClass = new Extractor<BaseClassDefinition, BaseClassDefinition>(){
		
		
		@Override
		public BaseClassDefinition extract(BaseClassDefinition parentClass, BaseClassDefinition otherClass) {
			if (otherClass.isChildOf(parentClass)) return parentClass; else return null;
		}
	};
	private static final Extractor<DexMethodId, MethodDefinition> extractorMethod = new Extractor<DexMethodId, MethodDefinition>(){
		
		
		@Override
		public MethodDefinition extract(BaseClassDefinition clazz, DexMethodId methodId) {
			MethodDefinition methodDef = clazz.getMethod(methodId);
			if (methodDef != null && methodDef.isAbstract()) return null; else return methodDef;
		}
	};
	private static final Extractor<DexFieldId, StaticFieldDefinition> extractorStaticField = new Extractor<DexFieldId, StaticFieldDefinition>(){
		
		
		@Override
		public StaticFieldDefinition extract(BaseClassDefinition clazz, DexFieldId fieldId) {
			return clazz.getStaticField(fieldId);
		}
	};
	protected static final Acceptor<Object> acceptorAlwaysTrue = new Acceptor<Object>(){
		
		
		@Override
		public boolean accept(Object item) {
			return true;
		}
	};
	private static final Acceptor<MethodDefinition> acceptorStaticCalls = new Acceptor<MethodDefinition>(){
		
		
		@Override
		public boolean accept(MethodDefinition method) {
			return method.isStatic();
		}
	};
	private static final Acceptor<MethodDefinition> acceptorVirtualCall = new Acceptor<MethodDefinition>(){
		
		
		@Override
		public boolean accept(MethodDefinition method) {
			return !method.isStatic() && !method.isPrivate();
		}
	};
	
	@java.lang.SuppressWarnings("all")
	public DexClassType getType() {
		return this.type;
	}
	
	@java.lang.SuppressWarnings("all")
	public boolean isInternal() {
		return this.internal;
	}
	
	@java.lang.SuppressWarnings("all")
	public BaseClassDefinition getSuperclass() {
		return this.superclass;
	}
	
	@java.lang.SuppressWarnings("all")
	public Set<BaseClassDefinition> getChildren() {
		return this.children;
	}
	
	@java.lang.SuppressWarnings("all")
	public Set<MethodDefinition> getMethods() {
		return this.methods;
	}
	
	@java.lang.SuppressWarnings("all")
	public Set<StaticFieldDefinition> getStaticFields() {
		return this.staticFields;
	}
}