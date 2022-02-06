package voyage_engine.assets;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import spool.IJsonSource;

public class Manifest implements IJsonSource {
	private HashMap<String, String> filenameToPath;
	private HashMap<String, Long> filenameToID;
	private HashMap<String, Long> folderToLastID;
	private long highestID;

	public Manifest() {
		filenameToPath = new HashMap<String, String>();
		filenameToID = new HashMap<String, Long>();
		folderToLastID = new HashMap<String, Long>();
		highestID = 0L;
	}

	public void compile() {
		// examine the files in the data folder
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		String relativePath = new File("data\\").getAbsolutePath().toString();
		System.out.println("[manifest]: data folder path: " + relativePath);
		File[] folders = new File("data\\").listFiles(directoryFilter);
		System.out.println("[manifest]: printing...");
		boolean folderFound = false;
		long lastID = 0L;
		for (File folder : folders) {
			// check to see if a last ID was recorded for this folder name.
			Long folderLastID = folderToLastID.get(folder.toString());
			if (folderLastID != null) {
				lastID = folderLastID.longValue();
				folderFound = true;
				System.out.println("[manifest]: using last ID: " + lastID);
			} else {
				lastID = highestID;
			}

			System.out.println(folder);
			lastID = searchDirectory(folder, relativePath, lastID);
			
			// record the new lastID for the specific folder.
			folderToLastID.put(folder.toString(), lastID);
			
			// increment the lastID up to next increment of 32767.
			if(folderFound == false) {
				lastID += Short.MAX_VALUE - (lastID % Short.MAX_VALUE);
				// if the current lastID is now higher than the recorded highest ID update the
				// highest value.
				if (lastID > highestID) {
					highestID = lastID;
				}
			}
		}
		// saves the current manifest file to the disk.
		save();
	}

	public void save() {
		AssetManager.writeToJson(this, "data\\manifest.json", true);
	}

	public long getID(String filename) {
		return filenameToID.get(filename);
	}

	public String getPath(String filename) {
		return filenameToPath.get(filename);
	}

	public String formatFilename(String path) {
		return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
	}

	private long searchDirectory(File folder, String relativePath, long lastID) {
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				lastID = searchDirectory(f, relativePath, lastID);
			} else {
				String filename = formatFilename(f.toString());
				if (!(filenameToPath.containsKey(filename) && filenameToID.containsKey(filename))) {
					filenameToPath.put(filename, f.getAbsolutePath().replace(relativePath, ""));
					filenameToID.put(filename, lastID);
					lastID++;
					System.out.println("\tadding:  " + filename);
				} else {
					System.out.println("\tfound:   " + filename);
				}
			}
		}
		return lastID;
	}
}
