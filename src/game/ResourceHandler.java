package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import GLEngine.GLSLProgram;
import GLEngine.Mesh;
import GLEngine.Texture;

public class ResourceHandler {

	// Meshes
	public static Mesh cube = Mesh.getMesh("obj/cube.obj");
	public static Mesh skybox = Mesh.getMesh("obj/skybox_1x1x1.obj");
	public static Mesh quad = Mesh.getMesh("obj/quad.obj");

	// Textures
	public static Texture white = Texture.getTexture("textures/rectangle.png");
	public static Texture icon = Texture.getTexture("textures/icon.png");
	public static Texture skytex = Texture.getTexture("textures/skybox.png");

	// Shaders
	public static GLSLProgram standard = GLSLProgram.getProgram("/shaders/vertex.vert", "/shaders/fragment.frag");
	public static GLSLProgram sprite = GLSLProgram.getProgram("/shaders/vertex2D.vert", "/shaders/fragment2D.frag");

	// Text rendering
	private static Texture[] CHARS;
	static {
		int num = 256;
		CHARS = new Texture[num];
		int height = 12;
		Font font = new Font("Times New Roman", Font.PLAIN, height);
		//Font font = loadFont("/fonts/GROBOLD.ttf", height);
		FontMetrics metrics = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB).getGraphics().getFontMetrics(font);
		for (char c = 0; c < num; c++) {
			if (Character.isISOControl(c)) {
				continue;
			}
			
			int hgt = metrics.getHeight();
			int adv = metrics.stringWidth("" + c);
			Dimension size = new Dimension(adv+1, hgt+8);
			BufferedImage charImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
			Graphics go = charImg.getGraphics();
			Graphics2D g = (Graphics2D) go;
			//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setFont(font);
		
			g.setColor(Color.WHITE);
			g.drawString("" + c, 0, size.height - 4);
			CHARS[c] = new Texture(charImg);
		}
	}

	private static Font loadFont(String path, float size) {
		InputStream is = Texture.class.getResourceAsStream(path);
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		return font.deriveFont(size);
	}
	
	public void free() {
		
	}

	public static Texture getCharTexture(int c) {
		return CHARS[c];
	}
}
