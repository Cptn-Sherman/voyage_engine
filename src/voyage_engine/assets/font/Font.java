package voyage_engine.assets.font;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import spool.Asset;
import spool.IInstantLoad;
import voyage_engine.Application;
import voyage_engine.assets.AssetManager;
import voyage_engine.assets.IGPUAsset;
import voyage_engine.assets.mesh.Mesh;
import voyage_engine.assets.mesh.MeshData;
import voyage_engine.assets.texture.Texture;
import voyage_engine.util.IOUtil;
import voyage_engine.util.Vec2;

public class Font extends Asset implements IInstantLoad, IGPUAsset {
	private static final int BITMAP_W = 2048;
	private static final int BITMAP_H = 2048;
	private static final float BAKED_FONT_PIXEL_SIZE = 128.0f;

	String filename;
	FontStyle style;
	Texture texture;
	boolean filter;
	int oversampling = 1;

	final STBTTAlignedQuad quad;
	final FloatBuffer xb;
	final FloatBuffer yb;

	float font_height;
	float line_height;
	float scale;

	STBTTFontinfo font_info = STBTTFontinfo.malloc();
	STBTTPackedchar.Buffer chardata;
	Map<Integer, Integer> chardataIndices;

	public Font(String filename, int oversampling, boolean filter) {
		super(true);
		this.filename = filename;
		this.filter = filter;
		this.oversampling = oversampling;
		texture = new Texture();
		
		// create constants for getting character information
		// used to build the text meshes.
		quad = STBTTAlignedQuad.malloc();
		xb = memAllocFloat(1);
		yb = memAllocFloat(1);
	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	public void load() {
		if (!isFilenameValid(filename)) {
			System.out.println("[content]: Error font could not be loaded. \n\tNo filename provided or filename did not end with \".ttf\" or \".otf\".");
			return;
		}
		texture.setTextureID(glGenTextures());
		chardata = STBTTPackedchar.malloc(96);
		chardataIndices = new HashMap<Integer, Integer>();

		for (int i = 0; i < chardata.remaining(); i++) {
			chardataIndices.put(i + 32, i);
		}

		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			// byteBuffer containing the ttf file content.
			ByteBuffer ttf = IOUtil.ioResourceToByteBuffer("data\\" + filename, 512 * 1024);
			// byte buffer containing the bitmap to store on the gpu.
			ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
			// prepare to pack the bitmap with the characters.
			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
			// font info used to get the actually pixel height desired.
			stbtt_InitFont(font_info, ttf);
			// set the oversampling values to allow smaller resolutions to render more
			// crisp.
			stbtt_PackSetOversampling(pc, oversampling, oversampling);
			// write the characters on the bytebuffer.
			scale = stbtt_ScaleForPixelHeight(font_info, BAKED_FONT_PIXEL_SIZE);
			stbtt_PackFontRange(pc, ttf, 0, BAKED_FONT_PIXEL_SIZE, 32, chardata);
			// end the writing on the buffer.
			stbtt_PackEnd(pc);
			// store the texture on the gpu.
			glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
			glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
			if (filter) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			}
			// unbind the texture.
			glBindTexture(GL_TEXTURE_2D, 0);
			System.out.println("[content]: generated font texture for: " + filename);

			// find the fonts vertical height used for alignment and positioning
			try (MemoryStack stack = stackPush()) {
				IntBuffer ascent = memAllocInt(1);
				IntBuffer descent = memAllocInt(1);
				IntBuffer line_gap = memAllocInt(1);
				stbtt_GetFontVMetrics(font_info, ascent, descent, line_gap);
				line_height = (((ascent.get(0) - descent.get(0) + line_gap.get(0)))) * (scale);
			}

		} catch (IOException e) {
			System.out.println("[content]: ERROR something went wrong loading the font: " + filename);
			throw new RuntimeException(e);
		}
		System.out.println("[content]: loaded font: " + filename);
	}

	public Vec2 generateMesh(Mesh mesh, String text, int size) {
		xb.put(0, 0f);
		yb.put(0, 0f);
		chardata.position(0);
		// generate the mesh data class.
		// 8 floats per character, 4 sets of 2 positions.
		// 8 floats per character, 4 sets of 2 texture coordinates.
		// 6 integers per character, 2 sets of 3 to build triangles in counter clockwise
		// order.
		MeshData data = new MeshData(mesh);
		data.floatsPerPosition = 2;
		data.positions = new float[text.length() * 8];
		data.uv = new float[text.length() * 8];
		data.indices = new int[text.length() * 6];
		// set up counter variables to find array position.
		int pos = 0, uv = 0, index = 0, last_index = 0;
		// calculate the actual size for the characters.
		float font_scale = (float) 2f * (size / BAKED_FONT_PIXEL_SIZE);
		//
		float text_width = 0.0f, text_height = 0.0f;
		// loop and add each character to the mesh data.
		for (int i = 0; i < text.length(); i++) {
			// TODO: need to handle special characters like new line, tabs, etc...
			if(text.charAt(i) == '\n') {
				xb.put(0, 0f);
				yb.put(0, yb.get(0) + line_height);
				text_height += line_height;
				continue;
			}
			
			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, chardataIndices.get((int) text.charAt(i)), xb, yb, quad,
					true);
			// vertex 0
			data.positions[pos++] = font_scale
					* (quad.x0() / (float) Application.getSettings().getWidth());
			data.positions[pos++] = -font_scale
					* (quad.y0() / (float) Application.getSettings().getHeight());
			data.uv[uv++] = quad.s0();
			data.uv[uv++] = quad.t0();
			// vertex 1
			data.positions[pos++] = font_scale
					* (quad.x1() / (float) Application.getSettings().getWidth());
			data.positions[pos++] = -font_scale
					* (quad.y0() / (float) Application.getSettings().getHeight());
			data.uv[uv++] = quad.s1();
			data.uv[uv++] = quad.t0();
			// vertex 2
			data.positions[pos++] = font_scale
					* (quad.x1() / (float) Application.getSettings().getWidth());
			data.positions[pos++] = -font_scale
					* (quad.y1() / (float) Application.getSettings().getHeight());
			data.uv[uv++] = quad.s1();
			data.uv[uv++] = quad.t1();
			// vertex 3
			data.positions[pos++] = font_scale
					* (quad.x0() / (float) Application.getSettings().getWidth());
			data.positions[pos++] = -font_scale
					* (quad.y1() / (float) Application.getSettings().getHeight());
			data.uv[uv++] = quad.s0();
			data.uv[uv++] = quad.t1();
			// triangle 0
			data.indices[index++] = last_index + 0;
			data.indices[index++] = last_index + 2;
			data.indices[index++] = last_index + 1;
			// triangle 1
			data.indices[index++] = last_index + 0;
			data.indices[index++] = last_index + 3;
			data.indices[index++] = last_index + 2;
			last_index += 4;

			// add up the total line_width
			if(font_scale * quad.x1() > text_width) {				
				text_width = font_scale * quad.x1();
			}
		}
		
		if(text_height == 0.0f) {
			text_height = getHeight(size);
		}
		// go ahead and process the mesh data now.
		data.process();
		// todo: try to avoid making a instance of vec2 here.
		return new Vec2(text_width, text_height);
	}

	@Override
	public void setReady(boolean value) {
		this.isReady = value;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public float getHeight(float font_size) {
		return ((2.0f * font_size * Application.getSettings().getHeight()) / BAKED_FONT_PIXEL_SIZE) * (scale);
	}

	public enum FontStyle {
		NORMAL, 
		BOLD, 
		ITALIC
	}

	@Override
	public void remove() {
		chardata.free();
		font_info.free();
		AssetManager.unload(texture);
	}
	
	private boolean isFilenameValid(String filename) {
		return filename != null && (filename.toLowerCase().endsWith(".ttf") || filename.toLowerCase().endsWith(".otf"));
	}
}