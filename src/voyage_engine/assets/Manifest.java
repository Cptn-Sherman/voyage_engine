package voyage_engine.assets;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import spool.IJsonSource;

public class Manifest implements IJsonSource {
	private HashMap<String, String> filenameToPath;
	private HashMap<String, Integer> filenameToId;
	private HashMap<String, Integer> packageToLastId;
	private HashMap<String, Short> packagenameToId;
	private int highestID;

	public Manifest() {
		filenameToPath = new HashMap<String, String>();
		filenameToId = new HashMap<String, Integer>();
		packageToLastId = new HashMap<String, Integer>();
		highestID = 0;
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
		System.out.println("[manifest]: printing data...");
		boolean folderFound = false;
		int lastID = 0;
		for (File folder : folders) {
			// check to see if a last ID was recorded for this folder name.
			Integer folderLastID = packageToLastId.get(folder.toString());
			if (folderLastID != null) {
				lastID = folderLastID.intValue();
				folderFound = true;
				System.out.println("[manifest]: using last ID: " + lastID);
			} else {
				lastID = highestID;
			}

			System.out.println(folder);

			lastID = searchDirectory(folder, relativePath, lastID);
			
			// record the new lastID for the specific folder.
			packageToLastId.put(folder.toString(), lastID);
			
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

	public int getID(String filename) {
		return filenameToId.get(filename);
	}

	public String getPath(String filename) {
		return filenameToPath.get(filename);
	}

	public String formatFilename(String path) {
		return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
	}

	private int searchDirectory(File folder, String relativePath, int lastID) {
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				lastID = searchDirectory(f, relativePath, lastID);
			} else {
				String filename = formatFilename(f.toString());
				if (!(filenameToPath.containsKey(filename) && filenameToId.containsKey(filename))) {
					filenameToPath.put(filename, f.getAbsolutePath().replace(relativePath, ""));
					filenameToId.put(filename, lastID);
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
