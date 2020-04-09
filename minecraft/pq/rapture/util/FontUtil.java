package pq.rapture.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import pq.rapture.Wrapper;
import static org.lwjgl.opengl.GL11.*;

public class FontUtil extends Gui {
	private static FontRenderer fr = Wrapper.getFontRenderer();

	public static void drawCenteredString(String line, int posX, int posY, int colour) {
		Wrapper.getFontRenderer().drawString(line, posX - Wrapper.getFontRenderer().getStringWidth(line) / 2, posY, colour, true);
	}

	public static int drawScaledString(String text, double d, double e, int color, float scale, boolean shadow) {
		glScaled(scale, scale, scale);
		int id = Wrapper.getFontRenderer().drawString(text, ((float) d * (1 / scale)), ((float) e * (1 / scale)), color, shadow);
		glScaled(1.0F / scale, 1.0F / scale, 1.0F / scale);
		return id;
	}

	public static void drawScaledTTF(String text, double d, double e, int color, float scale, boolean shadow) {
		glScaled(scale, scale, scale);
		Wrapper.getFont().drawString(text, ((float) d * (1 / scale)), ((float) e * (1 / scale)), color, shadow);
		glScaled(1.0F / scale, 1.0F / scale, 1.0F / scale);
	}

	public static void drawCenteredScaledString(String text, double posX, double posY, int color, float scale) {
		glScaled(scale, scale, scale);
		int half = (Wrapper.getFontRenderer().getStringWidth(text) / 2);
		Wrapper.getFontRenderer().drawString(text, ((float) posX * (1 / scale) - (half)), ((float) posY * (1 / scale)), color, true);
		glScaled(1.0F / scale, 1.0F / scale, 1.0F / scale);
	}

	public static void drawCenteredTTFString(String text, double posX, double posY, int color, boolean shadow) {
		float half = Wrapper.getFont().getStringWidth(text) / 2;
		Wrapper.getFont().renderString(text, (float) (posX - half), (float) (posY), color, shadow);
	}

	public static int drawStringVariableWidth(String text, int x, int y, int color, float shadowSeperation){
		return Wrapper.getFontRenderer().drawString(text, x, y, color, true, shadowSeperation);
	}

	public static int drawStringWithShadow(String text, int d, int e, int color) {
		int id = Wrapper.getFontRenderer().drawString(text, d, e, color, true);
		return id;
	}

}
