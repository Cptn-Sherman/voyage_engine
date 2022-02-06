package voyage_engine.assets.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.annotations.Expose;

import spool.IInstantLoad;
import spool.IJsonSource;
import voyage_engine.assets.Asset;
import voyage_engine.assets.AssetManager;
import voyage_engine.assets.IGPUAsset;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader extends Asset implements IInstantLoad, IJsonSource, IGPUAsset {
	private int id;
	private int vertexShaderID;
	private int fragmentShaderID;
	private HashMap<Integer, Integer> uniformHashLocation = new HashMap<Integer, Integer>();

	@Expose 	// these are the only values that are going to be exposed in the json file.
	private String vertexFile, fragmentFile;
	
	
	public Shader() {
		super(true);
	}
	
	public void start() {
		GL20.glUseProgram(id);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	public int getUniformLocation(String locationName) {
		Integer ulocation = uniformHashLocation.get(locationName.hashCode());
		if(ulocation == null) {
			ulocation = GL20.glGetUniformLocation(id, locationName);
			if(ulocation == -1) {
				System.err.println("NO SUCH UNIFORM: " + locationName); 
				return -1;
			}
			uniformHashLocation.put(locationName.hashCode(), ulocation);
		}
		return ulocation;
	}

	@Override
	public void load() {
		vertexShaderID = loadShader("data\\" + AssetManager.getFilePath(vertexFile), GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader("data\\" + AssetManager.getFilePath(fragmentFile), GL20.GL_FRAGMENT_SHADER);
		id = GL20.glCreateProgram();
		GL20.glAttachShader(id, vertexShaderID);
		GL20.glAttachShader(id, fragmentShaderID);
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
	}

	@Override
	public void remove() {
        stop();
		GL20.glDetachShader(id, vertexShaderID);
		GL20.glDetachShader(id, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(id);
	}

	public int getShaderID() {
		return id;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null ) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}
		catch(IOException e) {
			System.err.println("[renderer]: ERROR: Could not read shader file: " + file);
			e.printStackTrace();
			System.exit(-1);
		}
		
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);	
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("[renderer|shader]: ERROR: Could not compile shader: " + file);
			System.exit(-1);
		}
		System.out.println("[renderer]: compiled shader: " + file);
		return shaderID;
	}
}
