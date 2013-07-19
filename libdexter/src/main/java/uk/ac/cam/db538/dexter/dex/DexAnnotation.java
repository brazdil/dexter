package uk.ac.cam.db538.dexter.dex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jf.dexlib.AnnotationItem;
import org.jf.dexlib.AnnotationSetItem;
import org.jf.dexlib.AnnotationSetRefList;
import org.jf.dexlib.AnnotationVisibility;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.EncodedValue.AnnotationEncodedSubValue;
import org.jf.dexlib.EncodedValue.EncodedValue;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;

public class DexAnnotation {
	private final DexClassType type;
	private final AnnotationVisibility visibility;
	private final List<String> paramNames;
	private final List<EncodedValue> paramValues;
	
	public DexAnnotation(DexClassType type, AnnotationVisibility visibility) {
		
		this.type = type;
		this.visibility = visibility;
		// Order of parameters matter, so it can't be stored in a hash table.
		this.paramNames = new ArrayList<String>();
		this.paramValues = new ArrayList<EncodedValue>();
	}
	
	public DexAnnotation(AnnotationItem anno, DexTypeCache cache) {
		this(DexClassType.parse(anno.getEncodedAnnotation().annotationType.getTypeDescriptor(), cache), anno.getVisibility());
		final org.jf.dexlib.EncodedValue.AnnotationEncodedSubValue encAnno = anno.getEncodedAnnotation();
		int len = encAnno.names.length;
		for (int i = 0; i < len; ++i) addParam(encAnno.names[i].getStringValue(), encAnno.values[i]);
	}
	
	public void addParam(String name, EncodedValue value) {
		paramNames.add(name);
		paramValues.add(value);
	}
	
	public List<String> getParamNames() {
		return Collections.unmodifiableList(paramNames);
	}
	
	public List<EncodedValue> getParamValues() {
		return Collections.unmodifiableList(paramValues);
	}
	
	public static List<DexAnnotation> parseAll(AnnotationSetItem annotations, DexTypeCache cache) {
		if (annotations == null) return Collections.emptyList();
		final org.jf.dexlib.AnnotationItem[] items = annotations.getAnnotations();
		final java.util.ArrayList<uk.ac.cam.db538.dexter.dex.DexAnnotation> list = new ArrayList<DexAnnotation>(items.length);
		for (final org.jf.dexlib.AnnotationItem anno : items) list.add(new DexAnnotation(anno, cache));
		return list;
	}
	
	public static List<List<DexAnnotation>> parseAll(AnnotationSetRefList annotations, DexTypeCache cache) {
		if (annotations == null) return Collections.emptyList();
		final org.jf.dexlib.AnnotationSetItem[] annotationLists = annotations.getAnnotationSets();
		if (annotationLists.length == 0) return Collections.emptyList();
		List<List<DexAnnotation>> list = new ArrayList<List<DexAnnotation>>(annotationLists.length);
		for (final org.jf.dexlib.AnnotationSetItem anno : annotationLists) list.add(parseAll(anno, cache));
		return list;
	}
	
	public AnnotationItem writeToFile(DexFile outFile, DexAssemblingCache cache) {
		int paramCount = paramNames.size();
		int paramIndex = 0;
		final org.jf.dexlib.StringIdItem[] paramNames = new StringIdItem[paramCount];
		final org.jf.dexlib.EncodedValue.EncodedValue[] paramValues = new EncodedValue[paramCount];
		for (int i = 0; i < paramCount; i++) {
			paramNames[paramIndex] = cache.getStringConstant(this.paramNames.get(i));
			paramValues[paramIndex] = DexUtils.cloneEncodedValue(this.paramValues.get(i), cache);
			paramIndex++;
		}
		final org.jf.dexlib.EncodedValue.AnnotationEncodedSubValue subValue = new AnnotationEncodedSubValue(cache.getType(type), paramNames, paramValues);
		return AnnotationItem.internAnnotationItem(outFile, visibility, subValue);
	}
	
	@java.lang.SuppressWarnings("all")
	public DexClassType getType() {
		return this.type;
	}
	
	@java.lang.SuppressWarnings("all")
	public AnnotationVisibility getVisibility() {
		return this.visibility;
	}
}