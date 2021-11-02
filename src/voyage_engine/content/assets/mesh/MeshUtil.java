package voyage_engine.content.assets.mesh;

import voyage_engine.Application;

public class MeshUtil {
    

    public static Mesh generateQuad(int width, int height) {
        Mesh mesh = new Mesh();
        MeshData data = new MeshData(mesh);
        data.floatsPerPosition = 2;
        float screenWidth = (float) Application.getSettings().getWidth();
        float screenHeight = (float) Application.getSettings().getHeight();
        data.positions = new float[] { 
            (-(width / 2) / screenWidth), 
            (-(height / 2) / screenHeight), 
            ((width / 2) / screenWidth), 
            (-(height / 2) / screenHeight), 
            ((width / 2) / screenWidth), 
            ((height / 2) / screenHeight), 
            (-(width / 2) / screenWidth), 
            ((height / 2) / screenHeight)};
        data.uv = new float[] { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f };
        data.indices = new int[] { 0, 1, 2, 0, 3, 2 };
        data.process();
        return mesh;
    }

    
}
