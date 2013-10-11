package uk.ac.cam.db538.dexter.apk;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.Getter;

import org.apache.commons.io.IOUtils;

public class SignatureFile {
	
	@Getter Certificate[] signatures;
	
	public SignatureFile(JarEntry entry, JarFile file) throws IOException {
		// force read
		IOUtils.toByteArray(file.getInputStream(entry));
		signatures = entry.getCertificates();
	}
}
