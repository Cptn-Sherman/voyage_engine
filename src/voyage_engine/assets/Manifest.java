package voyage_engine.assets;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import voyage_engine.module.Module;

import spool.IJsonSource;

public class Manifest implements IJsonSource {
	private static int MODULE_ID_RANGE = Math.abs(Short.MAX_VALUE) + Short.MAX_VALUE;
	private HashMap<String, String> filenameToPath;
	private HashMap<String, Integer> filenameToId;

	private HashMap<Short, Module> moduleIdToModule;
	private HashMap<String, Short> moduleHashToId;

	private String moduleFolderpath;
	private short next_id;

	public Manifest(String module_folderpath) {
		filenameToPath = new HashMap<String, String>();
		filenameToId = new HashMap<String, Integer>();
		moduleIdToModule = new HashMap<Short, Module>();
		moduleHashToId = new HashMap<String, Short>();
		moduleFolderpath = module_folderpath;
		next_id = AssetManager.RESERVED_GENERATED_ASSET_ID + 1;
	}

	/**
	 * Reads the current manifest instance and asserts that either the contents of
	 * the data folder have not changed or that new additions do not collide with
	 * existing entries.
	 */
	public boolean validate() {
		System.out.println("[manifest]: validating...");
		File[] modules = getModules();
		// gaurd if the number of modules does not match the number of modules in the
		// manifest, they cannot match.
		if (modules.length != moduleIdToModule.size())
			return false;
		for (File module : modules) {
			Module m = new Module(module.getPath(), !module.getPath().endsWith(".jar"));

			if (!moduleStored(m)) {
				addModule(m);
			} else {
				if (!matchesStoredModule(m)) {
					System.out.println("\tERROR!!!\n\tPANIC\n\tPANIC\n\tPANIC.");
				}
			}
		}
		return true;
	}

	public boolean matchesStoredModule(Module module) {
		Short id = moduleHashToId.get(module.getHash());
		Module storedModule = moduleIdToModule.get(id);
		if (!storedModule.equals(module)) {
			System.out.println("\tERROR -> " + module.toString());
			System.out.println("\tERROR!!! Expected to find match, but module was not exact match to manifest record.");
			return false;
		} else {
			System.out.println("\tfound -> " + storedModule.toString());
			return true;
		}
	}

	private boolean moduleStored(Module module) {
		Short id = moduleHashToId.get(module.getHash());
		return (id != null);
	}

	private void addModule(Module module) {
		module.setId(getNextId());
		moduleHashToId.put(module.getHash(), module.getId());
		moduleIdToModule.put(module.getId(), module);
		System.out.println("\tinserted -> " + module.toString());
	}

	/**
	 * Reads the data folder and adds modules and their contents to master manifest.
	 */
	public void compile() {
		System.out.println("[manifest]: compiling...");
		// examine the files in the data folder
		File[] modules = getModules();

		if (modules.length >= MODULE_ID_RANGE) {
			System.out.println("[manifest]: engine does not support " + MODULE_ID_RANGE + " id's.");
			return;
		}

		for (File module : modules) {
			addModule(new Module(module.getPath(), !module.getPath().endsWith(".jar")));
		}
		// saves the current manifest file to the disk.
		AssetManager.writeToJson(this, moduleFolderpath + "manifest.json", true);
	}

	private short getNextId() {
		return next_id++;
	}

	private File[] getModules() {
		FileFilter moduleFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(".jar");
			}
		};
		File[] modules = new File(moduleFolderpath).listFiles(moduleFilter);
		return modules;
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

	public int getID(String filename) {
		return filenameToId.get(filename);
	}

	public String getPath(String filename) {
		return filenameToPath.get(filename);
	}

	public String formatFilename(String path) {
		return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
	}
}
