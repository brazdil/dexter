package uk.ac.cam.db538.dexter.aux;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public enum TaintConstants {

	EMPTY                             (),
	
	SOURCE_CONTACTS            (0, true),
	SOURCE_SMS                 (1, true),
    SOURCE_CALL_LOG 		   (2, true),
    SOURCE_LOCATION 		   (3, true),
	SOURCE_BROWSER 		       (4, true),
	SOURCE_DEVICE_ID 		   (5, true),

	SINK_FILE   		     (29, false),
	SINK_SOCKET   		     (30, false),
	SINK_OUT   			     (31, false);
  
	public final int value;
	public final boolean source;
  
	private TaintConstants() {
		this.value = 0;
		this.source = false; // doesn't matter
	}
  
	private TaintConstants(int shl, boolean source) {
		this.value = 1 << shl;
		this.source = source;
	}
	
	private static int TAINT_SOURCE;
	private static int TAINT_SINK;
	
	static {
		TAINT_SOURCE = EMPTY.value;
		TAINT_SINK = EMPTY.value;
		
		for (TaintConstants constant : values()) {
			if (constant.source)
				TAINT_SOURCE |= constant.value;
			else
				TAINT_SINK |= constant.value;
		}
	}
  
	public static final int queryTaint(String query) {
		if (query.startsWith("content://com.android.contacts"))
			return SOURCE_CONTACTS.value;
		else if (query.startsWith("content://sms"))
			return SOURCE_SMS.value;
		else if (query.startsWith("content://call_log"))
			return SOURCE_CALL_LOG.value;
		else
			return EMPTY.value;
	}

	public static final int serviceTaint(String name) {
		if (name.equals("location"))
			return SOURCE_LOCATION.value;
		else if (name.equals("phone"))
			return SOURCE_DEVICE_ID.value;
		else
			return EMPTY.value;
	}
  
	public static final int sinkTaint(Object obj, int taint) {
		if (obj instanceof File)
			taint |= SINK_FILE.value;
		else if (obj instanceof Socket)
			taint |= SINK_SOCKET.value;
		return taint;
	}
  
	public static final boolean isSourceTaint(int taint) {
		return (taint & TAINT_SOURCE) != 0;
	}
  
	public static final boolean isSinkTaint(int taint) {
		return (taint & TAINT_SINK) != 0;
	}
  
	public static final boolean isImmutable(Object obj) {
		return obj == null || isImmutableType(obj.getClass());
	}
  
	public static final boolean isImmutableType(Class<?> cls) {
		return IMMUTABLES.contains(cls);
	}

	public static final List<Class<?>> IMMUTABLES = Arrays.asList(
		String.class,
		Integer.class,
		Boolean.class,
		Byte.class,
		Character.class,
		Double.class,
		Float.class,
		Long.class,
		Short.class,
		Void.class,
		BigDecimal.class,
		BigInteger.class,
		java.security.Timestamp.class
	);
  
	public static final void logLeakage(int taint, String leakType) {
		StringBuilder str = new StringBuilder();
		str.append("Data leak: ");
	  
		boolean first = true;
		for (TaintConstants constant : values()) {
			if ((constant.value & taint) != 0) {
				if (first)
					first = false;
				else
					str.append(", ");
			  
				str.append(constant.name());
			}
		}
		
		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.err.println("==========   DEXTER DATA LEAK REPORT   ==========");
		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		
	}
}
