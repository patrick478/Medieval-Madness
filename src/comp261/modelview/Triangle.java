package comp261.modelview;

import initial3d.engine.Vec3;

public class Triangle {

	public final float[] diffuse = new float[4];
	public final float[] specular = new float[4];
	
	public final Vec3[] vertices = new Vec3[3];
	public final Vec3[] normals = new Vec3[3];
	public final Vec3 trinorm;
	
	public Triangle(Vec3 v0, Vec3 v1, Vec3 v2, int r, int g, int b) {
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;
		trinorm = (v1.sub(v0)).cross(v2.sub(v1)).unit();
		normals[0] = trinorm;
		normals[1] = trinorm;
		normals[2] = trinorm;
//		diffuse[0] = 0.9f;
//		diffuse[1] = 0.9f;
//		diffuse[2] = 0.9f;
//		specular[0] = r / 255f;
//		specular[1] = g / 255f;
//		specular[2] = b / 255f;
		
		float specratio = 0.85f;
		float cr = r / 255f;
		float cg = g / 255f;
		float cb = b / 255f;
		
		diffuse[0] = cr * cr * (1f - specratio);
		diffuse[1] = cg * cg * (1f - specratio);
		diffuse[2] = cb * cb * (1f - specratio);
		specular[0] = cr * specratio;
		specular[1] = cg * specratio;
		specular[2] = cb * specratio;
		
//		diffuse[0] = 0.081f;
//		diffuse[1] = 0.064f;
//		diffuse[2] = 0.036f;
//		specular[0] = 0.81f;
//		specular[1] = 0.72f;
//		specular[2] = 0.54f;
	}
	
}
