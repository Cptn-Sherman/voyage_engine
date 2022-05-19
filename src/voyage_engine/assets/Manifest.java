package voyage_engine.assets;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import voyage_engine.module.Module;

import spool.IJsonSource;

public class Manifest implements IJsonSource {
	private static int MAX_MODULE_COUNT = Math.abs(Short.MAX_VALUE) + Short.MAX_VALUE;

	private HashMap<String, String> filenameToPath;
	private HashMap<String, Integer> filenameToId;

	private HashMap<Short, Module> moduleIdToModule;
	private HashMap<String, Short> moduleHashToId;

	private String moduleFolderpath;
	private short next_id;
	private String algorithm_type;

	public Manifest(String module_folderpath) {
		filenameToPath = new HashMap<String, String>();
		filenameToId = new HashMap<String, Integer>();
		moduleIdToModule = new HashMap<Short, Module>();
		moduleHashToId = new HashMap<String, Short>();
		moduleFolderpath = module_folderpath;
		algorithm_type = Module.HASH_ALGORITHM;
		next_id = AssetManager.RESERVED_GENERATED_ASSET_ID + 1;
	}

	/**
	 * Reads the current manifest instance and asserts that either the contents of
	 * the data folder have not changed or that new additions do not collide with
	 * existing entries.
	 */
	public boolean validate() {
		boolean discrepancy = false;
		System.out.println("[manifest]: validating manifest...");

		if (!this.algorithm_type.equals(Module.HASH_ALGORITHM)) {
			System.out.println("[manifest]: algorithm type for module hash did not match.");
			System.out.println("\t expected: " + Module.HASH_ALGORITHM + " found: " + this.algorithm_type);
			return false;
		}
		File[] modules = getModules();
		// gaurd if the number of modules does not match the number of modules in the
		// manifest, they cannot match.
		if (modules.length != moduleIdToModule.size()) {
			System.out.println("[manifest]: module count did not match id-to-module map.");
			return false;
		}

		for (File module : modules) {
			Module m = new Module(module.getPath(), !module.getPath().endsWith(".jar"));
			if (!matchesStoredModule(m)) {
				addModule(m);
				discrepancy = true;
				break;
			}
		}
		return (!discrepancy);
	}

	/**
	 * Checks if the given module matches on stored in the manfiest
	 * 
	 * @param module The module to check for
	 * @return true if the module was a match
	 */
	public boolean matchesStoredModule(Module module) {
		Short id = moduleHashToId.get(module.getHash());
		if (id == null) {
			return false;
		}
		Module storedModule = moduleIdToModule.get(id);
		if (!storedModule.equals(module)) {
			System.out.println("\tERROR -> " + module.toString());
			System.out
					.println("\t\tERROR!!! Expected to find match, but module was not exact match to manifest record.");
			return false;
		} else {
			System.out.println("\tusing -> \t" + storedModule.toString());
			return true;
		}
	}

	private void addModule(Module module) {
		module.setId(getNextId());
		moduleHashToId.put(module.getHash(), module.getId());
		moduleIdToModule.put(module.getId(), module);
		System.out.println("\tinserting -> \t" + module.toString());
	}

	/**
	 * Reads the data folder and adds modules and their contents to a new master
	 * manifest.
	 */
	public void compile() {
		System.out.println("[manifest]: compiling...");
		// get a list of all modules in data folder.
		File[] modules = getModules();
		// ensure the number of mods to not exceed the max.
		if (modules.length >= MAX_MODULE_COUNT) {
			System.out.println("[manifest]: engine does not support " + modules.length + " id's.");
			System.out.println("\tthis is a lot of mods fella...");
			return;
		}
		// for each of the modules add them to the list and process it.
		for (File moduleFile : modules) {
			Module module = new Module(moduleFile.getPath(), !moduleFile.getPath().endsWith(".jar"));
			processModule(module);
			addModule(module);
		}
		// saves the current manifest file to the disk.
		AssetManager.writeToJson(this, moduleFolderpath + "manifest.json", true);
	}

	private File[] getModules() {
		FileFilter moduleFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(".jar");
			}
		};
		return new File(moduleFolderpath).listFiles(moduleFilter);
	}

	private void processModule(Module module) {
		if (module.isUnpacked()) {

		} else {

		}
	}

	private int searchDirectory(File folder, String relativePath, int lastID) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				lastID = searchDirectory(f, relativePath, lastID);
			} else {
				String filename = formatFilename(f.toString());
				if (!(filenameToPath.containsKey(filename) &&
						filenameToId.containsKey(filename))) {
					filenameToPath.put(filename, f.getAbsolutePath().replace(relativePath, ""));
					filenameToId.put(filename, lastID);
					lastID++;
					System.out.println("\tadding: " + filename);
				} else {
					System.out.println("\tfound: " + filename);
				}
			}
		}
		return lastID;
	}

	private short getNextId() {
		return next_id++;
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
