package uk.ac.cam.db538.dexter.dex.type;

import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;

public class DexChar extends DexPrimitiveType {
	
	private static final long serialVersionUID = 1L;

	private static String DESCRIPTOR = "C";
	private static String NAME = "char";
	
	DexChar() { }

    @Override
	public RegisterWidth getTypeWidth() {
    	return RegisterWidth.SINGLE;
	}

	@Override
	public String getDescriptor() {
		return DESCRIPTOR;
	}

	@Override
	public String getPrettyName() {
		return NAME;
	}

    public static DexChar parse(String typeDescriptor, DexTypeCache cache) {
    	if (!typeDescriptor.equals(DESCRIPTOR))
    		throw new UnknownTypeException(typeDescriptor);
    	else
    		return cache.getCachedType_Char();
    }

	public static String jvm2dalvik(String javaName) {
		if (javaName.equals(NAME))
			return DESCRIPTOR;
		else
			throw new UnknownTypeException(javaName);
	}

	@Override
	protected DexClassType getPrimitiveClass(DexTypeCache cache) {
		return DexClassType.parse("Ljava/lang/Character;", cache);	
	};
}