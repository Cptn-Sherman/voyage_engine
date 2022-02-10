package voyage_engine.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Module {

	private String filepath, hash, version, engine_version, name, description;
	private boolean unpacked = false;
	private short id;

	public Module(String filepath) {
		this(filepath, false);
	}

	public Module(String filepath, boolean unpacked) {
		this.filepath = filepath;
		this.unpacked = unpacked;
		hash = (unpacked) ? "UNPACKED-SKIPPING-HASH" : Module.computeHash(filepath);
	}

	public static String computeHash(String filepath) {
		try {
			return getFileChecksum(MessageDigest.getInstance("SHA-256"), new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
		// Get file input stream for reading the file content
		FileInputStream inputStream = new FileInputStream(file);
		// Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;
		// Read file data and update in message digest
		while ((bytesCount = inputStream.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		// close the stream; We don't need it now.
		inputStream.close();
		// Get the hash's bytes
		byte[] bytes = digest.digest();
		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		// return complete hash
		return sb.toString();
	}

	public String getHash() {
		return hash;
	}

	public short getId() {
		return id;
	}

	public boolean isUnpacked() {
		return unpacked;
	}
}
