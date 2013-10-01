package uk.ac.cam.db538.dexter.manifest;

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
	
	public String getApplicationClass() throws IOException {
		return BinXmlUtil.getApplicationClass(getDataStream());
	}
	
	public void setApplicationClass(String className) throws IOException {
		this.dataArray = BinXmlUtil.setApplicationClass(getDataStream(), className);
	}
	
	public void setApplicationClass(DexClass clazz) throws IOException {
		setApplicationClass(clazz.getClassDef().getType().getJavaDescriptor());
	}
	
	public InputStream getDataStream() {
		return new ByteArrayInputStream(this.dataArray);
	}
}
