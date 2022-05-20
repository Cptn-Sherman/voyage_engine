package voyage_engine.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import voyage_engine.assets.Manifest;

public class Module {
	public static String HASH_ALGORITHM = "SHA-1";

	private static final int KILOBYTE = 1024;
	private static final int MEGABYTE = 1024 * KILOBYTE;
	private static final int GIGABYTE = 1024 * MEGABYTE;

	private String name, filepath, hash, version, engine_version, description;
	private short module_id;
	private boolean unpacked, drop_in, isRoot;
	private short id, asset_count, content_count, last_id;
	private long total_bytes = 0L;

	public Module(String filepath, boolean unpacked, short module_id) {
		File file = new File(filepath);
		this.name = file.getName();
		this.filepath = filepath;
		this.unpacked = unpacked;
		this.module_id = module_id;
		this.total_bytes = new File(filepath).length();
		// we should not try to hash unpacked modules as there contents are expected to
		// change frequently.
		hash = (unpacked) ? "UNPACKED_" + name : Module.computeHash(filepath, HASH_ALGORITHM);
		last_id = 0;
	}

	public void process(Manifest manifest) {
		//todo: process the modules manifest file for descripition and other things like that.
		//todo: for unpacked modules we should know if we are "re-checking"
		// if we have "any" unpacked modules we need to revalidate the folder and process every time or check if modified date does not match, which means we need to store this value as well... in this class
		if (this.isUnpacked()) {
			searchDirectory(manifest, filepath);
		} else {

		}
	}

	private void searchDirectory(Manifest manifest, String path) {
		File[] files = new File(path).listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				searchDirectory(manifest, f.getPath());
			} else {
				String filename = formatFilename(f.toString());
				if(!(manifest.getFilenameToIdMap().containsKey(filename) && manifest.getFilenameToPathMap().containsKey(filename))) {
					manifest.getFilenameToPathMap().put(filename, f.getAbsolutePath().replace(manifest.getModuleDataFolderLocation(), ""));
					manifest.getFilenameToIdMap().put(filename, getAssetId());
					System.out.println("\tadding: " + filename);
					//todo: check whether this is an asset file or content and increase the counter.
				} else {
					// do nothing becuase the asset or content already exists.
					System.out.println("\tfound: " + filename);
				}
			}
		}
	}

	public int getAssetId() {
		return (int)((int)module_id << 16) | ((int) last_id++); //! <--- needs testing
	}

	public String formatFilename(String path) {
		return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
	}

	public static String computeHash(String filepath, String algorithm) {
		try {
			return getFileChecksum(MessageDigest.getInstance(algorithm), new File(filepath)).toUpperCase();
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

	public String getVersion() {
		return version;
	}

	public String getSupportedEngineVersion() {
		return engine_version;
	}

	public short getModuleId() {
		return module_id;
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

	public String getFilepath() {
		return filepath;
	}

	public String getFormatedSizeString() {
		float val = 0;
		String str = "";
		if (total_bytes == 0L)
			return "???";
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
		return other.getHash().equals(this.getHash()) && other.getBtyeCount() == this.getBtyeCount()
				&& other.isUnpacked() == this.unpacked;
	}

	@Override
	public String toString() {
		return "module: [name: " + name + ", id: " + getModuleIdString() + ", unpacked: "
				+ unpacked + ", size: "
				+ getFormatedSizeString() + ", hash: " + hash + "]";
	}
}
