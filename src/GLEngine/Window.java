package GLEngine;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;


public class Window {
	
    private long windowID;
    private int width = 1200, height = 800;
    private String title;
    private boolean vsync = true;
    
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cursorCallback;
    private GLFWErrorCallback errorCallback;
    
    public Window(String title){
    	this.title = title;
    }
    
    public Window(String title, int w, int h){
    	this.title = title;
    	width = w;
    	height = h;
    }
    
    public void init(int antiAliasing, boolean fullscreen){
    	init(false, 0, 0, antiAliasing, fullscreen);
    }
    
    public void init(int locationX, int locationY, int antiAliasing, boolean fullscreen){
    	init(true, locationX, locationY, antiAliasing, fullscreen);
    }
    
    private void init(boolean customLocation, int locationX, int locationY, int antiAliasing, boolean fullscreen){
    	try{
    		if(glfwInit() != GL_TRUE){
    			System.err.println("GLFW initialization failed!");
    		}

    		//glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
			glfwWindowHint(GLFW_SAMPLES, antiAliasing); // Anti Aliasing

    		windowID = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : NULL , NULL);
    		if (fullscreen) {
    			glfwSetInputMode(windowID, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    		}

    		if(windowID == NULL){
    			System.err.println("Could not create our Window!");
    		}

    		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    		if (customLocation) {
    			glfwSetWindowPos(windowID, locationX, locationY);
    		} else {
    			glfwSetWindowPos(
        				windowID,
        				(GLFWvidmode.width(vidmode) - width) / 2,
        				(GLFWvidmode.height(vidmode) - height) / 2
        				);
    		}
    		
    		//glfwSetWindowPos(windowID, 100, 100);

    		glfwMakeContextCurrent(windowID);
    		glfwShowWindow(windowID);
    		GLContext.createFromCurrent();

    		//Background color
    		
    		glClearColor(0,0, 0, 1);
    		glEnable(GL_DEPTH_TEST);
    		//glEnable(GL_CULL_FACE);     // Cull back facing polygons
    	    //glCullFace(GL_BACK); 
    		//glEnable(GL_BLEND); //För 2D transparansfunktionalitet (ersätt depth test)
    		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  
    		System.out.println("OpenGL: " + glGetString(GL_VERSION));
    		
    		//Init inputhandler
    		glfwSetKeyCallback(windowID, keyCallback = new KeyHandler());
    		glfwSetCursorPosCallback(windowID, cursorCallback = new MouseHandler());
    		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

    		//vsync
    		glfwSwapInterval(vsync ? 1 : 0);

    		//wireframe
    		 //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
    	}catch(Exception e){
    		System.out.println("Can not create window");
    	}
    }
    
    public long getWindowID(){
    	return windowID;
    }
    
    public void destroy(){
        glfwDestroyWindow(windowID);
        keyCallback.release();
        glfwTerminate();
        errorCallback.release();
    }

	public void swapBuffers() {
	    glfwSwapBuffers(windowID);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowID) == GL_TRUE;
	}
	
	public void setShouldClose(boolean shouldClose){
		glfwSetWindowShouldClose(windowID, shouldClose ? GL_TRUE : GL_FALSE);
	}

	public void pollEvents() {
		glfwPollEvents();
		
	}
	
	public float getAspect(){
		return (float)width/height;
	}

	public float getWidth() {
		return (float)width;
	}

	public float getHeight() {
		return (float)height;
	}
}
