package voyage_engine.content.assets.font;

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
// stbtt_GetFontVMetrics
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
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import spool.SpoolAsset;
import spool.IInstantLoad;
import voyage_engine.Application;
import voyage_engine.content.assets.AssetManager;
import voyage_engine.content.assets.IGPUAsset;
import voyage_engine.content.assets.mesh.Mesh;
import voyage_engine.content.assets.mesh.MeshData;
import voyage_engine.content.assets.texture.Texture;
import voyage_engine.util.IOUtil;
import voyage_engine.util.Vec2;

public class Font extends SpoolAsset implements IInstantLoad, IGPUAsset {
	private static final int BITMAP_W = 2048, BITMAP_H = 2048;
	private static final float BAKED_FONT_SIZE = 128.0f;
	private static final int DEFAULT_OVERSAMPLE = 2;
	private static final boolean DEFAULT_FILTER = true;
	// inputs
	private String filename;
	private FontStyle style;
	private Texture texture;
	private boolean filter;
	private int oversampling = 1;
	
	private STBTTFontinfo font_info;
	private STBTTPackedchar.Buffer chardata;
	private final STBTTAlignedQuad quad;
	private final FloatBuffer xb;
	private final FloatBuffer yb;

	public Font (String filename) {
		this(filename, DEFAULT_OVERSAMPLE, DEFAULT_FILTER);
	}

	public Font(String filename, int oversampling, boolean filter) {
		super(true);
		setFilename(filename);
		this.filter = filter;
		this.oversampling = oversampling;
		texture = new Texture();
		// create constants for getting character information
		// used to build the text meshes.
		chardata = STBTTPackedchar.malloc(96);
		font_info = STBTTFontinfo.malloc();
		quad = STBTTAlignedQuad.malloc();
		xb = memAllocFloat(1);
		yb = memAllocFloat(1);
	}
	
	public void load() {
		if (!isFilenameValid(filename)) {
			System.out.println(
					"[asset]: Error font could not be loaded. \n\t" 
					+ "No filename provided or filename did not end with \".ttf\" or \".otf\".");
			return;
		}

		texture.setTextureID(glGenTextures());

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
			stbtt_PackFontRange(pc, ttf, 0, BAKED_FONT_SIZE, 32, chardata);
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
			System.out.println("[asset]: generated font texture for: " + filename);
		} catch (IOException e) {
			System.out.println("[asset]: ERROR something went wrong loading the font: " + filename);
			throw new RuntimeException(e);
		}
		System.out.println("[asset]: loaded font: " + filename);
	}

	public void generateMesh(Mesh mesh, String text, int desired_size, Vec2 outDimensions) {
		// reset the position in the buffers
		xb.put(0, 0f);
		yb.put(0, 0f);
		// chardata.position(0); // idk what this does but removing makes not affect...
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
		float scale_factor = (float) desired_size / (float) BAKED_FONT_SIZE;
		float text_height = 0f, text_width = 0f, out_text_height = 0f; 
		float x0, x1, y0, y1, u0, v0, u1, v1, offset;

		// loop and add each character to the mesh data.
		for (int i = 0; i < text.length(); i++) {

			if(text.charAt(i) == '\n') { 
				// the baked line height is used because the yb assumes Baked size spacing the scaling is done later.
				text_height += BAKED_FONT_SIZE;
				xb.put(0, 0f);
				yb.put(0, text_height); // advance the y position by some amount
				continue;
			} else if (text.charAt(i) == ' ') {
				stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, ' ' - 32, xb, yb, quad, false);
				continue;
			} else if (text.charAt(i) == '\t') {
				// !this is kinda hacky but it essientally adds 5 spaces to the mesh; correctly spacing the x and y buffer value.
				stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, ' ' - 32, xb, yb, quad, false);
				stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, ' ' - 32, xb, yb, quad, false);
				stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, ' ' - 32, xb, yb, quad, false);
				stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, ' ' - 32, xb, yb, quad, false);
				stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, ' ' - 32, xb, yb, quad, false);
				continue;
			}

			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i) - 32, xb, yb, quad, false);
			
			offset = BAKED_FONT_SIZE * scale_factor / (float) Application.getHeight();
			x0 = quad.x0() * scale_factor / (float) Application.getWidth();
			x1 = quad.x1() * scale_factor / (float) Application.getWidth();
			y0 = quad.y0() * scale_factor / (float) Application.getHeight();
			y1 = quad.y1() * scale_factor / (float) Application.getHeight();
			// read texture coordinates from packed quad
			u0 = quad.s0();
			v0 = quad.t0();
			u1 = quad.s1();
			v1 = quad.t1();

			// vertex 0
			data.positions[pos++] = x0;
			data.positions[pos++] = -y0 - offset;
			data.uv[uv++] = u0;
			data.uv[uv++] = v0;
			// vertex 1
			data.positions[pos++] = x0;
			data.positions[pos++] = -y1 - offset;
			data.uv[uv++] = u0;
			data.uv[uv++] = v1;
			// vertex 2
			data.positions[pos++] = x1;
			data.positions[pos++] = -y1 - offset;
			data.uv[uv++] = u1;
			data.uv[uv++] = v1;
			// vertex 3
			data.positions[pos++] = x1;
			data.positions[pos++] = -y0 - offset;
			data.uv[uv++] = u1;
			data.uv[uv++] = v0;
			// triangle 0
			data.indices[index++] = last_index + 0;
			data.indices[index++] = last_index + 2;
			data.indices[index++] = last_index + 1;
			// triangle 1
			data.indices[index++] = last_index + 0;
			data.indices[index++] = last_index + 3;
			data.indices[index++] = last_index + 2;
			last_index += 4;

			// if the xbuffer is bigger its the new width.
			float xb_scaled = xb.get(0) * scale_factor;
			text_width = (xb_scaled > text_width) ? xb_scaled : text_width;
		}
		// computes the size of the box
		text_height = (text_height == 0) ? BAKED_FONT_SIZE : text_height;
		out_text_height = text_height * scale_factor;
		// set the return values for the bounding box surrounding the text.
		outDimensions.set(text_width, out_text_height);
		// process the mesh data.
		data.process();
	}

	public float scale(float center, float offset, float factor) {
		return (offset - center) * factor + center;
	}
	
	@Override
	public void setReady(boolean value) {
		this.isReady = value;
	}

	@Override
	public boolean isReady() {
		return isReady;
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


	public enum FontStyle {
		NORMAL, BOLD, ITALIC
	}

	@Override
	public void remove() {
		memFree(xb);
		memFree(yb);
		quad.free();
		chardata.free();
		font_info.free();
		AssetManager.unload(texture);
	}

	private boolean isFilenameValid(String filename) {
		return filename != null && (filename.toLowerCase().endsWith(".ttf") || filename.toLowerCase().endsWith(".otf"));
	}
}