package voyage_engine.assets;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.org.apache.xpath.internal.operations.Mod;

import voyage_engine.module.Module;

import spool.IJsonSource;

public class Manifest implements IJsonSource {
	private static int MAX_MODULE_COUNT = Math.abs(Short.MAX_VALUE) + Short.MAX_VALUE;

	private HashMap<String, String> filenameToPath;
	private HashMap<String, Integer> filenameToId;

	private HashMap<Short, Module> moduleIdToModule;
	private HashMap<String, Short> moduleHashToId;

	private String moduleFolderpath;
	private String algorithm_type;
	private boolean usingUnpacked = false;

	private short next_module_id;

	public Manifest(String module_folderpath) {
		filenameToPath = new HashMap<String, String>();
		filenameToId = new HashMap<String, Integer>();
		moduleIdToModule = new HashMap<Short, Module>();
		moduleHashToId = new HashMap<String, Short>();
		moduleFolderpath = module_folderpath;
		algorithm_type = Module.HASH_ALGORITHM;

		// set the starting value to -32,768 + 1 as this value is reserved for generated content.
		next_module_id = Short.MIN_VALUE + 1;
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
			boolean unpacked = !module.getPath().endsWith(".jar");
			if (unpacked) {
				usingUnpacked = true;

			} else {
				Module m = new Module(module.getPath(), unpacked, (short) 0);
				if (!matchesStoredModule(m)) {
					addModule(m);
					discrepancy = true;
					break;
				}	
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
		moduleHashToId.put(module.getHash(), module.getModuleId());
		moduleIdToModule.put(module.getModuleId(), module);
		System.out.println("\tinserting -> \t" + module.toString());
	}

	/**
	 * Reads the data folder and adds modules and their contents to a new master
	 * manifest.
	 */
	public boolean compile() {
		System.out.println("[manifest]: compiling...");
		// get a list of all modules in data folder.
		ArrayList<Module> data_modules = getModules();

		// ensure the number of mods to not exceed the max number of supported modules.
		if (data_modules.size() >= MAX_MODULE_COUNT) {
			System.out.println("[manifest]: engine does not support " + data_modules.size() + " id's.");
			System.out.println("\tthe maximum supported mod count is current " + MAX_MODULE_COUNT);
			System.out.println("\tno one will ever see this error... i hope :)");
			return false;
		}

		for (Module module : moduleIdToModule.values()) {
			// try to find a match in the data modules array

			// if we find one awesome, as long as its not unpacked we can remove it from the data_modules array.

			// if dont find a match at all, this means a mod that was installed has been removed.


		}
		// we need to process each of the mods remaining in the moduleId folder

		// for each of the modules add them to the list and process it.
		for (File moduleFile : data_modules) {
			Module module = new Module(moduleFile.getPath(), !moduleFile.getPath().endsWith(".jar"), next_module_id);
			module.process(this);
			addModule(module);
			// advance the module id by 1.
			next_module_id++;
		}
		// saves the current manifest file to the disk.
		AssetManager.writeToJson(this, moduleFolderpath + "manifest.json", true);
	}

	/**
	 * Scans the games data folder for modules.
	 * @return  An arrayList of modules.
	 */
	private ArrayList<Module> getModules() {
		FileFilter moduleFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(".jar");
			}
		};
		File[] files = new File(moduleFolderpath).listFiles(moduleFilter);
		ArrayList<Module> modules = new ArrayList<Module>(files.length);
		for (int i = 0; 0 < files.length; i++) {
			modules.add(new Module(files[i].getPath(), !files[i].getPath().endsWith(".jar"), getModuleId()));
		}
		return modules;
	}

	private short getModuleId() {
		return next_module_id++;
	}

	public int getID(String filename) {
		return filenameToId.get(filename);
	}

	public String getPath(String filename) {
		return filenameToPath.get(filename);
	}

	public String getModuleDataFolderLocation() {
		return moduleFolderpath;
	}

	public HashMap<String, Integer> getFilenameToIdMap() {
		return this.filenameToId;
	}

	public HashMap<String, String> getFilenameToPathMap() {
		return this.filenameToPath;
	}

	public static String toAssetString(int assetID) {
		return "[0x" + String.format("%08x", assetID).toUpperCase() + "]";
	}
}
