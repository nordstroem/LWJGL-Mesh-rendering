package GLEngine;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseHandler  extends GLFWCursorPosCallback{
	
	// Extending the GLFWCursorPosCallback class would
	// give us errors in an empty class, hovering over 
	// the underlined CursorInput tells us we need to add
	// unimplemented methods. In this case it implements 
	// the invoke method for us.
	public static double x, y;

	@Override
	public void invoke(long window, double x, double y) {
		// Within this invoke method we can then decide what
		// we want to do with our cursor. In this case
		// we will just be printing out our x and y 
		// variables to the command line:
		this.x = x;
		this.y = y;
	}

}
