package GLEngine;

import java.util.HashMap;


public class Mesh {

	public float[] vertices;
	public float[] textureCoords;
	public float[] normalCoords;
	public int[] indices;
	public Material material;
	
	public static class Material{
		public Vector3f specular = new Vector3f(0,0,0);
	}
	
	public static Mesh getMesh(String path) {
		return getMesh(path, 1, true);
	}
	
	public static Mesh getMesh(String path, float scale) {
		return getMesh(path, scale, true);
	}
	
	public static Mesh getMesh(String path, float scale, boolean forceFlatShading) {
		Mesh value = OBJLoader.LOADER.getIndexedMesh(path, scale, forceFlatShading);
		return value;
	}
	
}

