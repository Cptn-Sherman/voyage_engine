package voyage_engine.assets.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import spool.IData;


public class TextureData implements IData {

	Texture texture;
    ByteBuffer buffer;
    int width, height;
    int bytesPerPixel;

    public TextureData(Texture texture, ByteBuffer image, int width, int height) {
        this.texture = texture;
        this.buffer = image;
        this.width = width;
        this.height = height;
        texture.setWidth(width);
        texture.setHeight(height);
        this.bytesPerPixel = 4;
    }

    public TextureData(Texture texture, ByteBuffer image, int width, int height, int bpp) {
        this.texture = texture;
        this.buffer = image;
        this.width = width;
        this.height = height;
        texture.setWidth(width);
        texture.setHeight(height);
        this.bytesPerPixel = bpp;
    }

    @Override
    public void process() {
        if (texture.getTextureID() != 0) {
            // usually we do not wanna do this, call remove directly.
            // this should be reserved for the asset manager however, in this
            // instance setting new data it should be fine.
            texture.remove();
            // may not need to set these values but just to be safe.
            texture.setReady(false);
            texture.setTextureID(0);
        }
 

        texture.setTextureID(glGenTextures());
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // filtering
        if (texture.isFiltered()) {
            if (texture.isMipmapped()) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            } else {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        // mip-mapping
        if (texture.isMipmapped()) {
            glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
        }
   
        // sets the internal format based on the number of bytes per pixel provided.
        // this allows monochrome (black & white) images to use only 1 byte or textures with no alpha can use 3 bytes.
        int internal_format, format;
        switch(bytesPerPixel) {
            case 4:
            internal_format = GL_RGBA8;
            format = GL_RGBA;
            break;
            case 3:
            internal_format = GL11.GL_RGB8;
            format = GL11.GL_RGB;
            break;
            case 1:
            internal_format = GL11.GL_LUMINANCE8;
            format = GL11.GL_LUMINANCE;
            break;
            default:
            internal_format = GL11.GL_RGB8;
            format = GL11.GL_RGB;
            break;
        }    
        // store image byte buffer on the gpu.
        glTexImage2D(GL_TEXTURE_2D, 0, internal_format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer);
        if (texture.isMipmapped()) {
            GL30.glGenerateMipmap(GL_TEXTURE_2D);
        }
        texture.setReady(true);
        glBindTexture(GL_TEXTURE_2D, 0);
        System.out.println("[content]: loaded texture: " + texture.getFilename());
    }
}
