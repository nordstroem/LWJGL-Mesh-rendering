package GLEngine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.BufferUtils;

public abstract class MeshBatchBase {

	protected Map<Mesh, VAO> vaoMap = new HashMap<Mesh, VAO>();
	protected ArrayList<Model> renderList = new ArrayList<Model>();
	protected ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
	protected VAO quadVAO = allocateSpriteVAO();

	protected class Model implements Comparable<Model> {
		public Mesh mesh;
		public Texture texture;
		public Matrix4f modelMatrix;
		public Vector3f color;
		public int meshVAO;

		public int compareTo(Model r) {
			if (this.texture.textureID < r.texture.textureID)
				return -1;
			if (this.texture.textureID > r.texture.textureID)
				return 1;
			return 0;
		}
	}

	public class Sprite {

		public int textureID;
		public Matrix4f modelMatrix;
		public Vector3f color = new Vector3f(1, 1, 1);
	}

	protected class VAO {
		public int VAO; //Vertex array
		public int VBO; //Position buffer
		public int IBO; //Index buffer (ebo)
		public int UVBO; //Texture buffer
		public int NBO; //Normal buffer
	}

	public void allocateVAO(Mesh mesh) {
		VAO vao = new VAO();

		vao.VAO = glGenVertexArrays();
		glBindVertexArray(vao.VAO);
		if (mesh.vertices.length == 0 || mesh.textureCoords.length == 0 || mesh.normalCoords.length == 0 || mesh.indices.length == 0) {
			System.out.println("ERROR!");

		}
		// Position
		vao.VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vao.VBO);
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(mesh.vertices.length);
		vbuf.put(mesh.vertices).rewind();
		glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
		glVertexAttribPointer(GLSLProgram.VERTEX_ATTRIB, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(GLSLProgram.VERTEX_ATTRIB);

		// Texture
		vao.UVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vao.UVBO);
		FloatBuffer tbuf = BufferUtils.createFloatBuffer(mesh.textureCoords.length);
		tbuf.put(mesh.textureCoords).rewind();
		glBufferData(GL_ARRAY_BUFFER, tbuf, GL_STATIC_DRAW);
		glVertexAttribPointer(GLSLProgram.UV_COORDS_ATTRIB, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(GLSLProgram.UV_COORDS_ATTRIB);

		// Normal
		vao.NBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vao.NBO);
		FloatBuffer nbuf = BufferUtils.createFloatBuffer(mesh.normalCoords.length);
		nbuf.put(mesh.normalCoords).rewind();
		glBufferData(GL_ARRAY_BUFFER, nbuf, GL_STATIC_DRAW);
		glVertexAttribPointer(GLSLProgram.NORMAL_COORDS_ATTRIB, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(GLSLProgram.NORMAL_COORDS_ATTRIB);

		// Index buffer
		vao.IBO = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vao.IBO);
		IntBuffer ibuf = BufferUtils.createIntBuffer(mesh.indices.length);
		ibuf.put(mesh.indices).rewind();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);

		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		//Insert in map
		vaoMap.put(mesh, vao);
	}

	private VAO allocateSpriteVAO() {
		VAO vao = new VAO();

		vao.VAO = glGenVertexArrays();
		glBindVertexArray(vao.VAO);

		float vertices[] = { 0, 1, // Top-left
				1, 1, // Top-right
				1, 0, // Bottom-right
				0, 0, // Bottom-left
		};
		int elements[] = { 0, 1, 2, 2, 3, 0 };
		float uv[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };

		// Position
		vao.VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vao.VBO);
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(vertices.length);
		vbuf.put(vertices).rewind();
		glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
		glVertexAttribPointer(GLSLProgram.VERTEX_ATTRIB, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(GLSLProgram.VERTEX_ATTRIB);

		// Texture
		vao.UVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vao.UVBO);
		FloatBuffer uvbuf = BufferUtils.createFloatBuffer(uv.length);
		uvbuf.put(uv).rewind();
		glBufferData(GL_ARRAY_BUFFER, uvbuf, GL_STATIC_DRAW);
		glVertexAttribPointer(GLSLProgram.UV_COORDS_ATTRIB_2D, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(GLSLProgram.UV_COORDS_ATTRIB_2D);

		// Index buffer
		vao.IBO = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vao.IBO);
		IntBuffer ibuf = BufferUtils.createIntBuffer(elements.length);
		ibuf.put(elements).rewind();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);

		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		//Insert in map
		return vao;
	}

	private void freeMeshVAO(Mesh mesh) {
		if (!vaoMap.containsKey(mesh)) {
			return;
		}
		VAO vao = vaoMap.get(mesh);
		glDeleteBuffers(vao.VBO);
		glDeleteBuffers(vao.UVBO);
		glDeleteBuffers(vao.NBO);
		glDeleteBuffers(vao.IBO);
		glDeleteVertexArrays(vao.VAO);
	}

	public void add(Mesh mesh, Texture texture, Matrix4f modelMatrix, Vector3f color) {

		if (!vaoMap.containsKey(mesh)) { //Maybe remove this and return error somewhere else if its not there..
			allocateVAO(mesh);
		}

		Model m = new Model();
		m.mesh = mesh;
		m.texture = texture;
		m.modelMatrix = modelMatrix;
		m.color = color;
		m.meshVAO = vaoMap.get(mesh).VAO;

		renderList.add(m);

	}

	public void addSprite(Texture texture, Matrix4f modelMatrix) {
		
		Sprite m = new Sprite();
		m.textureID = texture.textureID;
		m.modelMatrix = modelMatrix;
		m.color = new Vector3f(1.0f, 1.0f, 1.0f);
		spriteList.add(m);
	}
	
	public void add(Mesh mesh, Texture texture, Matrix4f modelMatrix) {
		add(mesh, texture, modelMatrix, new Vector3f(1.0f, 1.0f, 1.0f));
	}


	public void free() {
		Iterator it = vaoMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Mesh mesh = (Mesh) pair.getKey();
			freeMeshVAO(mesh);
			it.remove(); // avoids a ConcurrentModificationException
		}
		VAO vao = quadVAO;
		glDeleteBuffers(vao.VBO);
		glDeleteBuffers(vao.UVBO);
		glDeleteBuffers(vao.IBO);
		glDeleteVertexArrays(vao.VAO);
	}

	public abstract void init();

	public abstract void begin();

	public abstract void end();

}
