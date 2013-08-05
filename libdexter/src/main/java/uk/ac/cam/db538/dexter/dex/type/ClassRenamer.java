package uk.ac.cam.db538.dexter.dex.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder;
import uk.ac.cam.db538.dexter.utils.Utils;
import uk.ac.cam.db538.dexter.utils.Utils.NameAcceptor;


public class ClassRenamer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<String, String> rules;
	
	/*
	 * Generates a ClassRenamer for the given file.
	 * Scans all its classes and if classes of the same name are already present
	 * in the builder, it generates a new name.
	 */
	public ClassRenamer(DexFile file, final HierarchyBuilder builder) {
		rules = new HashMap<String, String>();
		
		for (val clsItem : file.ClassDefsSection.getItems()) {
			final DexTypeCache typeCache = builder.getTypeCache();
			String oldDesc = clsItem.getClassType().getTypeDescriptor();
			
			final String namePrefix = oldDesc.substring(0, oldDesc.length() -1);
			final String nameSuffix = ";";
			
			String newDesc = Utils.generateName(namePrefix, nameSuffix, new NameAcceptor() {
				@Override
				public boolean accept(String name) {
					return !builder.hasClass(DexClassType.parse(name, typeCache));
				}
			});
			
			if (!oldDesc.equals(newDesc))
				addRule(oldDesc, newDesc);
		}
	}
	
	private void addRule(String original, String replacement) {
		if (!DexClassType.isClassDescriptor(original) || !DexClassType.isClassDescriptor(replacement))
			throw new Error("Invalid class type descriptor");
		else if (rules.containsKey(original))
			throw new Error("Multiple name replacement rules for class " + original);
		else
			rules.put(original, replacement);
	}
	
	public String applyRules(String clazz) {
		val rule = rules.get(clazz);
		if (rule == null)
			return clazz;
		else
			return rule;
	}
}
