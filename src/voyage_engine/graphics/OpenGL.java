package voyage_engine.graphics;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import voyage_engine.assets.mesh.Mesh;
import voyage_engine.assets.texture.Texture;
import voyage_engine.util.Matrix4f;
import voyage_engine.util.Vec2;
import voyage_engine.util.Vec3;
import voyage_engine.util.Vec4;


public class OpenGL {
    private static boolean wireframe = false;
    private static int err;

    private static Shader activeShader;
    private static FloatBuffer matrixBuffer;
    private static int vertexCount = 0;
    private static Camera camera;

    public static void init() {
        GL.createCapabilities();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_CULL_FACE);
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        camera = new Camera();
    }

    public static void prepare() {
        // GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (wireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
    }

    public static void bindTexture(Texture texture) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
    }

    public static void unbindTexture() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public static void bindMesh(Mesh mesh) {
        GL30.glBindVertexArray(mesh.getVao());
        for (int i = 0; i < mesh.getAttribCount(); i++) {
            GL20.glEnableVertexAttribArray(i);
        }
        vertexCount = mesh.getVertexCount();
    }

    public static void unbindMesh(Mesh mesh) {
        for (int i = 0; i < mesh.getAttribCount(); i++) {
            GL20.glDisableVertexAttribArray(i);
        }
        GL30.glBindVertexArray(0);
        vertexCount = 0;
    }

    public static void draw() {
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
    }

    public static void dispatchBatches() {

    }

    public static void loadFloat(String locationName, float value) {
        GL20.glUniform1f(activeShader.getUniformLocation(locationName), value);
    }

    public static void loadVector2(String locationName, Vec2 value) {
        GL20.glUniform2f(activeShader.getUniformLocation(locationName), value.x, value.y);
    }

    public static void loadVector2(String locationName, float x, float y) {
        GL20.glUniform2f(activeShader.getUniformLocation(locationName), x, y);
    }

    public static void loadVector3(String locationName, Vec3 value) {
        GL20.glUniform3f(activeShader.getUniformLocation(locationName), value.x, value.y, value.z);
    }

    public static void loadVector3(String locationName, float x, float y, float z) {
        GL20.glUniform3f(activeShader.getUniformLocation(locationName), x, y, z);
    }

    public static void loadVector4(String locationName, Vec4 value) {
        GL20.glUniform4f(activeShader.getUniformLocation(locationName), value.x, value.y, value.z, value.w);
    }

    public static void loadVector4(String locationName, float x, float y, float z , float w) {
        GL20.glUniform4f(activeShader.getUniformLocation(locationName), x, y, z, w);
    }

    public static void loadBoolean(String locationName, boolean value) {
        GL20.glUniform1f(activeShader.getUniformLocation(locationName), (value) ? 1 : 0);
    }

    public static void loadMatrix(String locationName, Matrix4f matrix) {
        matrixBuffer.clear();
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4fv(activeShader.getUniformLocation(locationName), false, matrixBuffer);
    }

    public static void loadColor(String string, Color color) {
        loadVector4(string, color.getColorVector());
    }

    public static void loadColor(String string, float r, float g, float b, float a) {
        loadVector4(string, r, g, b, a);
    }

    // reads and prints each error to the system console.
    public static void glErrorCheck() {
        while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            System.out.println("[openGL]: ERROR: " + err + "!");
        }
    }

    // checks if the required shader has been enabled
    // if the shader is not enabled, it swaps to the desired shader and calls start.
    public static void checkEnableShader(Shader shader) {
        if (activeShader != null && activeShader.getShaderID() == shader.getShaderID()) {
            return;
        } else {
            // stop the current shader if it exists and if its Shader ID does not equal 0.
            if (activeShader != null && activeShader.getShaderID() != 0)
                activeShader.stop(); // disable the current shader.
            // store the new shader as the active shader and start it.
            activeShader = shader;
            activeShader.start();
        }
    }

    public static Camera getCamera() {
        return camera;
    }
}
