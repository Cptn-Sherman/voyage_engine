package voyage_engine.content.assets.mesh;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import spool.IData;


public class MeshData implements IData {
    public Mesh mesh;
    public float[] positions;
    public float[] uv;
    public float[] normals;
    public int[] indices;
    public int floatsPerPosition = 3;
    public int normalsPerFace = 1;
    
    public MeshData(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void process() {
        mesh.setVao(GL30.glGenVertexArrays()); 
        GL30.glBindVertexArray(mesh.getVao());
        List<Integer> list = new ArrayList<Integer>();
        int attributeCounter = 0;
        // store positions
        if(positions != null && positions.length > 0) {
            storeDataInAttributeLists(list, attributeCounter++, floatsPerPosition, positions);
        }
        // store uv
        if(uv != null && uv.length > 0) {
            storeDataInAttributeLists(list, attributeCounter++, 2, uv);
        }
        // store normals
        if(normals != null && normals.length > 0) {
            storeDataInAttributeLists(list, attributeCounter++, normalsPerFace, normals);
        }
        // store indicies
        if(indices != null && indices.length > 0) {
            bindIndicesBuffer(list, indices);
            mesh.vertex_count = indices.length;
        } else {
            mesh.vertex_count = positions.length;
        }
        
        GL30.glBindVertexArray(0);
        // mesh should now be completely loaded.
        mesh.setVboList(list);
        mesh.attrib_count = attributeCounter;
        mesh.setReady(true);
        
        // todo: need to register the mesh to the assetMap for reference later... AssetManager
    }

    private void storeDataInAttributeLists(List<Integer> list, int attributeNumber, int coordinateSize, float[] data) {
        int vbo = GL15.glGenBuffers();
        list.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        java.nio.FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data);
        buffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(buffer);
    }

    private void bindIndicesBuffer(List<Integer> list, int[] indices) {
        int vbo = GL15.glGenBuffers();
        list.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer =  MemoryUtil.memAllocInt(indices.length);
        buffer.put(indices);
        buffer.flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);
    }
}
