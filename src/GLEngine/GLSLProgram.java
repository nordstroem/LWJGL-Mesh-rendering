package GLEngine;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class GLSLProgram {

	public static final int VERTEX_ATTRIB = 0;
	public static final int UV_COORDS_ATTRIB = 1;
	public static final int NORMAL_COORDS_ATTRIB = 2;
	public static final int UV_COORDS_ATTRIB_2D = 1;
	
	public final int programID;

	
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();

	public static GLSLProgram getProgram(String vertex, String fragment){
		return new GLSLProgram(vertex,fragment);
	}
	
	public GLSLProgram(String vertex, String fragment){
		programID = load(vertex, fragment);
	}

	public int getUniform(String name){
		if (locationCache.containsKey(name)){
			return locationCache.get(name);
		}
		int result = glGetUniformLocation(programID, name);
		if(result == -1){
			System.err.println("Could not find uniform variable'" + name + "'!");
		}
		else {
			locationCache.put(name, result);
		}
		
		return glGetUniformLocation(programID, name);
	}

	public void setUniform1i(String name, int value){
		glUniform1i(getUniform(name), value);
	}

	public void setUniform2f(String name, float x, float y){
		glUniform2f(getUniform(name), x, y);
	}

	public void setUniform3f(String name, Vector3f vector){
		glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
	}


	public void enable(){
		glUseProgram(programID);
	}

	public void disable(){
		glUseProgram(0);
	}
	
	
	public void free(){
		glDeleteProgram(programID);
	}
	
	public static int load(String vertPath, String fragPath) {
		
		String vert = loadAsString(vertPath);
		String frag = loadAsString(fragPath);
		
		int program = glCreateProgram();
		int vertID = glCreateShader(GL_VERTEX_SHADER);
		int fragID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(vertID, vert);
		glShaderSource(fragID, frag);


		glCompileShader(vertID);
		if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile vertexd shader!");
			System.err.println(glGetShaderInfoLog(vertID));
		}

		glCompileShader(fragID);
		if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile fragment shader!");
			System.err.println(glGetShaderInfoLog(fragID));
		}


		glAttachShader(program, vertID);
		glAttachShader(program, fragID);
		glLinkProgram(program);
		glValidateProgram(program);
		
		//Delete the shaders
		glDetachShader(program, vertID);
		glDetachShader(program, fragID);
		glDeleteShader(vertID);
		glDeleteShader(fragID);

		return program;
	}	


	public static String loadAsString(String location) {
		StringBuilder result = new StringBuilder();
		try {
			InputStream in = OBJLoader.class.getResourceAsStream(location); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer);
				result.append("\n");
			}
			reader.close();

		} catch (IOException e) {
			System.err.println(e);
		}
		return result.toString();
	}
}
