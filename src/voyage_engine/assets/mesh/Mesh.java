package voyage_engine.assets.mesh;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import spool.SpoolAsset;
import spool.IMultithreadLoad;
import voyage_engine.assets.IGPUAsset;

public class Mesh extends SpoolAsset implements IMultithreadLoad, IGPUAsset {
	int vao;
    List<Integer> vbo_list = new ArrayList<Integer>();
    int vertex_count;
    int attrib_count;
    String filename;

    public Mesh() {
		super(true);
	}
    
    @Override
    public void process() {
    	// TODO: do some shit.
        return;
    }

    @Override
    public void remove() {
        GL30.glDeleteVertexArrays(vao);
        for(int buffer : vbo_list) {
            GL15.glDeleteBuffers(buffer);
        }
    }

    @Override
    public boolean isReady() {
        return (vao != 0) && this.isReady;
    }

    @Override
    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public void setVao(int vao) {
        this.vao = vao;
    }

    public int getVao() {
        return vao;
    }

    public int getVertexCount() {
        return vertex_count;
    }
    public void setVertexCount(int vertex_count) {
        this.vertex_count = vertex_count;
    }

    public int getAttribCount() {
        return attrib_count;
    }
    public void setAttribCount(int attribCount) {
        this.attrib_count = attribCount;
    }

    public void setVboList(List<Integer> vbos) {
        this.vbo_list = vbos;
    }
    public List<Integer> getVboList() {
        return vbo_list;
    }

	@Override
	public boolean returnsData() {
		return true;
	}
}