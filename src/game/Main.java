package game;

import static org.lwjgl.glfw.GLFW.*;
import GLEngine.*;

public class Main {

	private Window window;
	private MeshBatch meshBatch;
	public static int tick = 0;

	public Main() {
		init();
		long t0;

		while (!window.shouldClose()) {
			tick++;
			t0 = System.nanoTime();
			KeyHandler.update();
			window.pollEvents();

			if (KeyHandler.pressed(GLFW_KEY_ESCAPE)) {
				window.setShouldClose(true);
			}
			meshBatch.begin();
			for (int i = -10; i <= 10; i++) {
				Matrix4f.Pipeline p = new Matrix4f.Pipeline();
				p.add(Matrix4f.rotate((float) Math.cos(tick / 100.0) * 360, 1, 1, 0));
				p.add(Matrix4f.scale(1, 1, 1));
				p.add(Matrix4f.translate(i, 1, -10));
				meshBatch.add(ResourceHandler.skybox, ResourceHandler.skytex, p.getMatrix());
			}
			Matrix4f.Pipeline p = new Matrix4f.Pipeline();
			p.add(Matrix4f.scale(50, 50, 1));
			p.add(Matrix4f.translate(50, 50, 0));
			meshBatch.addSprite(ResourceHandler.icon, p.getMatrix());
			meshBatch.addString("Text.", new Vector3f(150,50,1));
			meshBatch.end();
			window.swapBuffers();

			long delta = System.nanoTime() - t0;
			if (delta != 0 && tick % 50 == 0) {
				System.out.println(1.0 / (delta / 1E9));
			}
		}

		free();

	}

	private void init() {
		window = new Window("LWJGL Mesh renderer", 1200, 800);
		window.init(1, false);
		meshBatch = new MeshBatch();
		meshBatch.init();
	}

	private void free() {
		meshBatch.free();
		window.destroy();
	}

	public static void main(String args[]) {
		new Main();
	}

}
