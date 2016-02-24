package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import GLEngine.GLSLProgram;
import GLEngine.Matrix4f;
import GLEngine.MeshBatchBase;
import GLEngine.MouseHandler;
import GLEngine.Texture;
import GLEngine.Vector3f;

public class MeshBatch extends MeshBatchBase {

	private GLSLProgram modelShader;
	private GLSLProgram spriteShader;

	// Uniform locations
	private int u_PV, u_M, u_TEX, u_tick, u_mouse;

	// Float buffers
	private FloatBuffer b_PV, b_M;

	@Override
	public void init() {
		modelShader = ResourceHandler.standard;
		spriteShader = ResourceHandler.sprite;
		u_PV = glGetUniformLocation(modelShader.programID, "PV");
		u_M = glGetUniformLocation(modelShader.programID, "M");
		u_TEX = glGetUniformLocation(modelShader.programID, "texSampler");
		u_tick = glGetUniformLocation(modelShader.programID, "tick");
		u_mouse = glGetUniformLocation(modelShader.programID, "mousePos");
		b_PV = BufferUtils.createFloatBuffer(16);
		b_M = BufferUtils.createFloatBuffer(16);
	}

	@Override
	public void begin() {
		renderList.clear();
		spriteList.clear();
	}

	@Override
	public void end() {
		renderMeshes();
		renderSprites();
	}


	@Override
	public void free() {
		super.free();
		modelShader.free();
	}

	public void renderMeshes() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glUseProgram(modelShader.programID);
		//Matrix4f PV = Matrix4f.orthographic(0, 1200, 800, 0, -500, 500);
		Matrix4f PV = Matrix4f.perspective(90, 1200/800.0f, 0.01f, 100);
		PV.setBuffer(b_PV);
		glUniformMatrix4fv(u_PV, false, b_PV);
		glUniform1i(u_tick, Main.tick);
		glActiveTexture(GL_TEXTURE0);

		glUniform2f(u_mouse, (float) MouseHandler.x, (float) MouseHandler.y);
		for (Model model : renderList) {

			glBindTexture(GL_TEXTURE_2D, model.texture.textureID);
			glUniform1i(u_TEX, 0);

			Matrix4f M = model.modelMatrix;
			M.setBuffer(b_M);
			glUniformMatrix4fv(u_M, false, b_M);
			glBindVertexArray(model.meshVAO);
			glDrawElements(GL_TRIANGLES, model.mesh.indices.length, GL_UNSIGNED_INT, 0);
		}

		glBindVertexArray(0);
		glUseProgram(0);
	}

	private void renderSprites() {
		glUseProgram(spriteShader.programID);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);


		Matrix4f PV = Matrix4f.orthographic(0, 1200, 800, 0, -500, 500);
		PV.setBuffer(b_PV);
		glUniformMatrix4fv(u_PV, false, b_PV);
		
		int m = spriteShader.getUniform("M");
		int tex = spriteShader.getUniform("texSampler");
		for(Sprite s : spriteList){
			Matrix4f M = s.modelMatrix;
			M.setBuffer(b_M);
			glUniformMatrix4fv(m, false, b_M);
		    glBindTexture(GL_TEXTURE_2D, s.textureID);
		    glUniform1i(tex, 0); 
			glBindVertexArray(quadVAO.VAO);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
			glBindVertexArray(0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glBindVertexArray(0);
		glUseProgram(0);
	}

	public void addString(String text, Vector3f pos){
		int x = 0;
		for(int i = 0; i < text.length(); i++){
			Sprite m = new Sprite();
			Texture ch = ResourceHandler.getCharTexture(text.charAt(i));
			int width = ch.width;
			int height = ch.height;
			Matrix4f.Pipeline p = new Matrix4f.Pipeline();
			p.add(Matrix4f.scale(width, height, 1));
			p.add(Matrix4f.translate(pos.x + x, pos.y, pos.z));
			m.textureID = ch.textureID;
			m.modelMatrix = p.getMatrix();
			x += width;
			spriteList.add(m);
		}
	}
	
}
