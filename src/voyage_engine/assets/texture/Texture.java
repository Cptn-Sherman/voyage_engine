package voyage_engine.assets.texture;

import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import spool.IMultithreadLoad;
import spool.Spool;
import voyage_engine.assets.Asset;
import voyage_engine.assets.IGPUAsset;

public class Texture extends Asset implements IMultithreadLoad, IGPUAsset {
    private int textureID, width, height;
    private boolean filter, mipmap;

    public Texture() {
		super(true);
	}
    
    public Texture(String filename, boolean filter, boolean mipmap) {
    	super(true);
        setFilename(filename);
        this.filter = filter;
        this.mipmap = mipmap;
    }

	@Override
	public void process() {
		ByteBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer w = stack.mallocInt(1);
            final IntBuffer h = stack.mallocInt(1);
            final IntBuffer comp = stack.mallocInt(1);
            stbi_set_flip_vertically_on_load(true);
            image = STBImage.stbi_load("data\\" + getFilename(), w, h, comp, 4);
            // error out if the image failed to load for some reason.
            if (image == null) {
                throw new RuntimeException("[asset|thread]: failed loading file, no file found: " + getFilename()
                        + "\n reason: " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();
        }
        Spool.addReturnData(new TextureData(this, image, width, height));
	}

	@Override
	public void remove() {
		GL11.glDeleteTextures(textureID);
	}
	
	@Override
    public boolean isReady() {
        return isReady && (this.getAssetID() != null);
    }

    @Override
    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int id) {
        textureID = id;
    }

    public boolean isFiltered() {
        return filter;
    }

    public boolean isMipmapped() {
        return mipmap;
    }

    public void setFiltered(boolean filter) {
        this.filter = filter;
    }

    public void setMipmapped(boolean mipmap) {
        this.mipmap = mipmap;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

	@Override
	public boolean returnsData() {
		return true;
	}

    public static ByteBuffer createByteBuffer (int size) {
        return BufferUtils.createByteBuffer(size);
    }

    public static ByteBuffer createByteBuffer (int width, int height, int bpp) {
        return createByteBuffer(width * height * bpp);
    }
}