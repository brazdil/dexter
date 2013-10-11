package uk.ac.cam.db538.dexter.apk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import uk.ac.cam.db538.dexter.dex.DexClass;

public class Manifest {

	private byte[] dataArray;
	
	public Manifest(byte[] data) {
		if (data == null)
			throw new IllegalArgumentException("Manifest data cannot be NULL");
		this.dataArray = data;
	}
	
	public Manifest(InputStream dataStream) throws IOException {
		this(IOUtils.toByteArray(dataStream));
	}
	
	public String getApplicationClass() {
		try {
			return BinXmlUtil.getApplicationClass(getDataStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setApplicationClass(String className) {
		try {
			this.dataArray = BinXmlUtil.setApplicationClass(getDataStream(), className);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getPackageName() {
		try {
			return BinXmlUtil.getPackage(getDataStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setApplicationClass(DexClass clazz) {
		setApplicationClass(clazz.getClassDef().getType().getJavaDescriptor());
	}
	
	public InputStream getDataStream() {
		return new ByteArrayInputStream(this.dataArray);
	}
}
