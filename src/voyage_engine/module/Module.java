package voyage_engine.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Module {

	private static final int KILOBYTE = 1024;
	private static final int MEGABYTE = 1024 * KILOBYTE;
	private static final int GIGABYTE = 1024 * MEGABYTE;

	private String filepath, hash, version, engine_version, name, description;
	private boolean unpacked = false;
	private boolean drop_in = true;
	private short id, asset_count, content_count;
	private long total_bytes = 0L;

	public Module(String filepath) {
		this(filepath, false);
	}

	public Module(String filepath, boolean unpacked) {
		File file = new File(filepath);
		this.name = file.getName();
		this.filepath = filepath;
		this.unpacked = unpacked;

		this.total_bytes = new File(filepath).length();
		hash = (unpacked) ? "UNPACKED_" + name : Module.computeHash(filepath);
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

	public void setId(short val) {
		id = val;	
	}

	public String getModuleIdString() {
		return "0x" + String.format("%04x", id).toUpperCase();
	}

	public boolean isUnpacked() {
		return unpacked;
	}

	public long getBtyeCount() {
		return total_bytes;
	}

	public String getFormatedSizeString() {
		float val = 0;
		String str = "";
		if (total_bytes <= KILOBYTE) {
			val = total_bytes;
			val = (float) (Math.round(val * 100.0) / 100.0);
			str = Float.toString(val) + " Bytes";
		} else if (total_bytes <= MEGABYTE) {
			val = ((float) total_bytes / (float) KILOBYTE);
			val = (float) (Math.round(val * 100.0) / 100.0);
			str = Float.toString(val) + " KB";
		} else if (total_bytes <= GIGABYTE) {
			val = ((float) total_bytes / (float) MEGABYTE);
			val = (float) (Math.round(val * 100.0) / 100.0);
			str = Float.toString(val) + " MB";
		} else {
			val = ((float) total_bytes / (float) GIGABYTE);
			val = (float) (Math.round(val * 100.0) / 100.0);
			str = Float.toString(val) + " GB";
		}
		return str;
	}

	public boolean equals(Module other) {
		return other.getHash().equals(this.getHash()) && other.getBtyeCount() == this.getBtyeCount() && other.isUnpacked() == this.unpacked;
	}

	@Override
	public String toString() {
		return "[module: " + name + ", id: " + getModuleIdString() + ", hash: " + hash + ", unpacked: "
		+ unpacked + ", size: "
				+ getFormatedSizeString() + "]";
	}
}
