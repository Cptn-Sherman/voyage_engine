package voyage_engine.util;

public class MathTools {

	public static Matrix4f createTransformationMatrix(Vec3 translation, Vec3 rotation, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rotation.x, new Vec3(1,0,0), matrix, matrix);
		Matrix4f.rotate(rotation.y, new Vec3(0,1,0), matrix, matrix);
		Matrix4f.rotate(rotation.z, new Vec3(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vec3(scale, scale, scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vec3 translation) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		return matrix;
	}
}
