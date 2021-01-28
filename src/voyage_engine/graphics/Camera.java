package voyage_engine.graphics;

import voyage_engine.Application;
import voyage_engine.input.Input;
import voyage_engine.input.Input.Key;
import voyage_engine.util.Matrix4f;
import voyage_engine.util.Vec3;

public class Camera {
    private Matrix4f projMatrix;
    private Matrix4f orthoMatrix;
    private Matrix4f viewMatrix;

    private Vec3 position, front, right, up;
    private float yaw = 0, pitch = 0;
    private final Vec3 worldUp = new Vec3(0.0f, 1.0f, 0.0f);
    private float velocity = 5;
    private float sensitivity = 15;

    public Camera() {
        Application.getSettings().setWidthScale(1f); 
    	Application.getSettings().setHeightScale(1f);
        if (Application.getSettings().getWidth() > Application.getSettings().getHeight()) {
        	Application.getSettings().setHeightScale((float) Application.getSettings().getHeight() / (float) Application.getSettings().getWidth());
        } else if (Application.getSettings().getWidth() < Application.getSettings().getHeight()) {
        	Application.getSettings().setWidthScale((float) Application.getSettings().getWidth() / (float) Application.getSettings().getHeight());
        }

        projMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();

        front = new Vec3(0, 0, 1);
        position = new Vec3(0, 0, 0);
        right = new Vec3();
        up = new Vec3();

        createOrthoMatrix(-1.0f, 1.0f, -1.0f, 1.0f, -1f, 100f);
        createProjectionMatrix(1000.0f, 0.1f, 75.0f, Application.getSettings().getWidth(), Application.getSettings().getHeight());
    }
	
	public void update(double delta) {
        // set the initial camera vectors for front, right and up
        yaw += Input.mouseDelta.x * sensitivity;
        pitch += Input.mouseDelta.y * sensitivity;
        // ensure that the pitch doesnt roll over past 89 or beneath -89.
        if(pitch >  89.0f) 
            pitch = 89.0f;
        if(pitch < -89.0f) 
            pitch = -89.0f;

		front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))); 
		front.y = (float) Math.sin(Math.toRadians(pitch)); 
		front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.normalise();
		Vec3.cross(front, worldUp, right);
		right.normalise();
		Vec3.cross(right, front, up);
        up.normalise();
        
        if(Input.isKeyDown(Key.LEFT_SHIFT)){
			velocity = (float) (25.0f * delta);
		} else {
			velocity = (float) (5.0f * delta);
		}

        // check for inputs to change the camera.
		if(Input.isKeyDown(Key.W)) {
			Vec3.add(position, new Vec3(front.x * velocity, front.y * velocity, front.z * velocity), position);
		}
		if(Input.isKeyDown(Key.S)) {
			Vec3.add(position, new Vec3(-front.x * velocity, -front.y * velocity, -front.z * velocity), position);
		}
		if(Input.isKeyDown(Key.D)) {
			Vec3.add(position, new Vec3(right.x * velocity, right.y * velocity, right.z * velocity), position);
		}
		if(Input.isKeyDown(Key.A)) {
			Vec3.add(position, new Vec3(-right.x * velocity, -right.y * velocity, -right.z * velocity), position);
        }
        if(Input.isKeyDown(Key.SPACE)) {
			Vec3.add(position, new Vec3(worldUp.x * velocity, worldUp.y * velocity, worldUp.z * velocity), position);
        }
        if(Input.isKeyDown(Key.LEFT_CONTROL)) {
			Vec3.add(position, new Vec3(-worldUp.x * velocity, -worldUp.y * velocity, -worldUp.z * velocity), position);
		}
        updateViewMatrix();
        
        // System.out.println(front.toString());
	}

    private void updateViewMatrix() {
		viewMatrix.setIdentity();
		viewMatrix.m00 = right.x;
		viewMatrix.m10 = right.y;
		viewMatrix.m20 = right.z;
		viewMatrix.m01 = up.x;
		viewMatrix.m11 = up.y;
		viewMatrix.m21 = up.z;
		viewMatrix.m02 = -front.x;
		viewMatrix.m12 = -front.y;
        viewMatrix.m22 = -front.z;
		Matrix4f.translate(new Vec3(-position.x, -position.y, -position.z), viewMatrix, viewMatrix);
	}

    private  void createOrthoMatrix(float left, float right, float bottom, float top, float near, float far) {
        orthoMatrix = new Matrix4f();
        orthoMatrix.setIdentity();
        orthoMatrix.m00 = 2.0f / (right - left);
        orthoMatrix.m01 = 0;
        orthoMatrix.m02 = 0;
        orthoMatrix.m03 = 0;
        orthoMatrix.m10 = 0;
        orthoMatrix.m11 = 2.0f / (top - bottom);
        orthoMatrix.m12 = 0;
        orthoMatrix.m13 = 0;
        orthoMatrix.m20 = 0;
        orthoMatrix.m21 = 0;
        orthoMatrix.m22 = -2.0f / (far - near);
        orthoMatrix.m23 = -(far + near) / (far - near);
        orthoMatrix.m30 = -(right + left) / (right - left);
        orthoMatrix.m31 = -(top + bottom) / (top - bottom);
        orthoMatrix.m32 = -(far + near) / (far - near);
        orthoMatrix.m33 = 1;
    }

	
    
    private void createProjectionMatrix(float zFar, float zNear, float fov, int screenWidth, int screenHeight) {
        projMatrix = new Matrix4f();
        projMatrix.setIdentity();
        // compute values for projection matrix.
        float aspectRatio = (float) screenWidth / (float) screenHeight;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = zFar - zNear;
        // set matrix values.
        projMatrix.m00 = x_scale;
        projMatrix.m11 = y_scale;
        projMatrix.m22 = -((zFar + zNear) / frustum_length);
        projMatrix.m23 = -1;
        projMatrix.m32 = -((2 * zNear * zFar) / frustum_length);
        projMatrix.m33 = 0;
    }
    
    public Matrix4f getOrthogonalMatrix() {
		return orthoMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}