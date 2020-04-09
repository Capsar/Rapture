package pq.rapture.render.font;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetBoolean;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

public class RaptureFontRenderer extends FontRenderer {

	public final Random fontRandom = new Random();
	private final Color[] customColorCodes = new Color[256];
	private final int[] colorCode = new int[32];
	private RaptureFont font, boldFont, italicFont, boldItalicFont;
	private String colorcodeIdentifiers = "0123456789abcdefklmnor";
	private boolean bidi;

	/**
	 * Constructs a new font renderer.
	 *
	 * @param fontName
	 * @param fontSize
	 * @param antiAlias
	 * */
	public RaptureFontRenderer(final Font font, final boolean antiAlias, final int charOffset) {
		super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft()
				.getTextureManager(), false);
		setFont(font, antiAlias, charOffset);
		customColorCodes['q'] = new Color(0, 90, 163);
		colorcodeIdentifiers = setupColorcodeIdentifier();
		setupMinecraftColorcodes();
		FONT_HEIGHT = getHeight();
	}

	@Override
	public int drawString(String text, float x, float y, int color, boolean shadow) {
		if (shadow) return drawStringWithShadow(text, x, y, color);
		return renderString(text, x, y, color, shadow);
	}

	@Override
	public int drawStringWithShadow(final String s, final float x, final float y, final int color) {
		renderString(s, x + 1, y + 1, color, true);
		return renderString(s, x, y, color, false) + 1;
	}

	public void drawCenteredString(final String s, final int x, final int y, final int color, final boolean shadow) {
		if (shadow)
			drawStringWithShadow(s, x - (getStringWidth(s) / 2), y, color);
		else
			drawString(s, x - (getStringWidth(s) / 2), y, color);
	}

	public void drawCenteredStringXY(final String s, final int x, final int y, final int color, final boolean shadow) {
		drawCenteredString(s, x, y - (getHeight() / 2), color, shadow);
	}

	public void drawCenteredString(final String s, final int x, final int y, final int color) {
		drawStringWithShadow(s, x - (getStringWidth(s) / 2), y, color);
	}

	public int renderString(final String text, final float x, final float y, int color, final boolean shadow) {
		if (text == null) return 0;
		glPushMatrix();
		glTranslated(x - 1.5, y + 0.5, 0);
		final boolean wasBlend = glGetBoolean(GL_BLEND);
		GlStateManager.enableAlpha();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		if ((color & -67108864) == 0) color |= -16777216;

		if (shadow) color = ((color & 16579836) >> 2) | (color & -16777216);

		final float red = ((color >> 16) & 0xff) / 255F;
		final float green = ((color >> 8) & 0xff) / 255F;
		final float blue = (color & 0xff) / 255F;
		final float alpha = ((color >> 24) & 0xff) / 255F;
		final Color c = new Color(red, green, blue, alpha);
		if (text.contains("\247")) {
			final String[] parts = text.split("\247");

			Color currentColor = c;
			RaptureFont currentFont = font;
			int width = 0;
			boolean randomCase = false, bold = false, italic = false, strikethrough = false, underline = false;

			for(int index = 0; index < parts.length; index++) {
				if (parts[index].length() <= 0) continue;
				if (index == 0) {

					currentFont.drawString(parts[index], width, 0, currentColor, shadow);
					width += currentFont.getStringWidth(parts[index]);

				} else {
					final String words = parts[index].substring(1);
					final char type = parts[index].charAt(0);
					final int colorIndex = colorcodeIdentifiers.indexOf(type);
					if (colorIndex != -1) if (colorIndex < 16) {
							final int colorcode = colorCode[colorIndex];
							currentColor = getColor(colorcode, alpha);
							bold = false;
							italic = false;
							randomCase = false;
							underline = false;
							strikethrough = false;
						} else if (colorIndex == 16)
							randomCase = true;
						else if (colorIndex == 17)
							bold = true;
						else if (colorIndex == 18)
							strikethrough = true;
						else if (colorIndex == 19)
							underline = true;
						else if (colorIndex == 20)
							italic = true;
						else if (colorIndex == 21) {
							bold = false;
							italic = false;
							randomCase = false;
							underline = false;
							strikethrough = false;
							currentColor = c;
						} else if (colorIndex > 21) {
							final Color customColor = customColorCodes[type];
							currentColor = new Color(customColor.getRed() / 255F, customColor.getGreen() / 255F,
									customColor.getBlue() / 255F, alpha);
						}

					if (bold && italic) {
						boldItalicFont.drawString(randomCase ? toRandom(currentFont, words) : words, width, 0, currentColor, shadow);
						currentFont = boldItalicFont;
					} else if (bold) {
						boldFont.drawString(randomCase ? toRandom(currentFont, words) : words, width, 0, currentColor, shadow);
						currentFont = boldFont;
					} else if (italic) {
						italicFont.drawString(randomCase ? toRandom(currentFont, words) : words, width, 0, currentColor, shadow);
						currentFont = italicFont;
					} else {
						font.drawString(randomCase ? toRandom(currentFont, words) : words, width, 0, currentColor, shadow);
						currentFont = font;
					}
					if (strikethrough)
						drawLine(width, (currentFont.getStringHeight(words) / 2) + 1, width + currentFont.getStringWidth(words),
								(currentFont.getStringHeight(words) / 2) + 1, 1);
					if (underline)
						drawLine(width, currentFont.getStringHeight(words), width + currentFont.getStringWidth(words),
								currentFont.getStringHeight(words), 1);
					width += currentFont.getStringWidth(words);
				}
			}
		} else
			font.drawString(text, 0, 0, c, shadow);
		if (!wasBlend) glDisable(GL_BLEND);
		glPopMatrix();
		GL11.glColor4f(1, 1, 1, 1);

		return (int) (x + getStringWidth(text));
	}

	/**
	 * Make dis
	 * */
	private String toRandom(final RaptureFont font, final String text) {
		String newText = "";
		final String allowedCharacters = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
		for(final char c : text.toCharArray())
			if (ChatAllowedCharacters.isAllowedCharacter(c)) {
				final int index = fontRandom.nextInt(allowedCharacters.length());
				newText += allowedCharacters.toCharArray()[index];
			}
		return newText;
	}

	public int getStringHeight(final String text) {
		if (text == null) return 0;
		return font.getStringHeight(text);
	}

	public int getHeight() {
		return font.getHeight();
	}

	public static String getFormatFromString(final String p_78282_0_) {
		String var1 = "";
		int var2 = -1;
		final int var3 = p_78282_0_.length();

		while ((var2 = p_78282_0_.indexOf(167, var2 + 1)) != -1)
			if (var2 < (var3 - 1)) {
				final char var4 = p_78282_0_.charAt(var2 + 1);
				if (isFormatColor(var4))
					var1 = "\u00a7" + var4;
				else if (isFormatSpecial(var4)) var1 = var1 + "\u00a7" + var4;
			}

		return var1;
	}

	private static boolean isFormatSpecial(final char formatChar) {
		return ((formatChar >= 107) && (formatChar <= 111)) || ((formatChar >= 75) && (formatChar <= 79)) || (formatChar == 114)
				|| (formatChar == 82);
	}

	private void renderSplitString(final String str, final int x, int y, final int wrapWidth, final boolean addShadow, final int textColor) {
		final List var6 = listFormattedStringToWidth(str, wrapWidth);

		for(final Iterator var7 = var6.iterator(); var7.hasNext(); y += getHeight()) {
			final String var8 = (String) var7.next();
			renderStringAligned(var8, x, y, wrapWidth, textColor, addShadow);
		}
	}

	@Override
	public int getColorCode(char character) {
		return colorCode["0123456789abcdef".indexOf(character)];
	}

	private String trimStringNewline(String p_78273_1_) {
		while ((p_78273_1_ != null) && p_78273_1_.endsWith("\n"))
			p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);

		return p_78273_1_;
	}

	private int renderStringAligned(final String p_78274_1_, int p_78274_2_, final int p_78274_3_, final int p_78274_4_,
			final int p_78274_5_, final boolean p_78274_6_) {
		if (bidi) {
			final int var7 = getStringWidth(bidiReorder(p_78274_1_));
			p_78274_2_ = (p_78274_2_ + p_78274_4_) - var7;
		}

		return renderString(p_78274_1_, p_78274_2_, p_78274_3_, p_78274_5_, p_78274_6_);
	}

	@Override
	public void setBidiFlag(final boolean state) {
		bidi = state;
	}

	@Override
	public boolean getBidiFlag() {
		return bidi;
	}

	private String bidiReorder(final String p_147647_1_) {
		try {
			final Bidi var2 = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
			var2.setReorderingMode(0);
			return var2.writeReordered(2);
		} catch (final ArabicShapingException var3) {
			return p_147647_1_;
		}
	}

	private int sizeStringToWidth(final String str, final int wrapWidth) {
		final int var3 = str.length();
		int var4 = 0;
		int var5 = 0;
		int var6 = -1;

		for(boolean var7 = false; var5 < var3; ++var5) {
			final char var8 = str.charAt(var5);
			switch (var8) {
			case 10:
				--var5;
				break;
			case 167:
				if (var5 < (var3 - 1)) {
					++var5;
					final char var9 = str.charAt(var5);
					if ((var9 != 108) && (var9 != 76)) {
						if ((var9 == 114) || (var9 == 82) || isFormatColor(var9)) var7 = false;
					} else
						var7 = true;
				}
				break;
			case 32:
				var6 = var5;
			default:
				var4 += getStringWidth(Character.toString(var8));
				if (var7) ++var4;
			}

			if (var8 == 10) {
				++var5;
				var6 = var5;
				break;
			}

			if (var4 > wrapWidth) break;
		}

		return (var5 != var3) && (var6 != -1) && (var6 < var5) ? var6 : var5;
	}

	private static boolean isFormatColor(final char colorChar) {
		return ((colorChar >= 48) && (colorChar <= 57)) || ((colorChar >= 97) && (colorChar <= 102))
				|| ((colorChar >= 65) && (colorChar <= 70));
	}

	@Override
	public int getCharWidth(final char c) {
		return getStringWidth(Character.toString(c));
	}

	@Override
	public int getStringWidth(final String text) {
		if (text == null) return 0;
		if (text.contains("\247")) {
			final String[] parts = text.split("\247");
			RaptureFont currentFont = font;
			int width = 0;
			boolean randomCase = false, bold = false, italic = false, strikethrough = false, underline = false;

			for(int index = 0; index < parts.length; index++) {
				if (parts[index].length() <= 0) continue;
				if (index == 0)
					width += currentFont.getStringWidth(parts[index]);
				else {
					final String words = parts[index].substring(1);
					final char type = parts[index].charAt(0);
					final int colorIndex = colorcodeIdentifiers.indexOf(type);
					if (colorIndex != -1) if (colorIndex < 16) { // coloring
							bold = false;
							italic = false;
							randomCase = false;
							underline = false;
							strikethrough = false;
						} else if (colorIndex == 16)
							randomCase = true;
						else if (colorIndex == 17)
							bold = true;
						else if (colorIndex == 18)
							strikethrough = true;
						else if (colorIndex == 19)
							underline = true;
						else if (colorIndex == 20)
							italic = true;
						else if (colorIndex == 21) { // reset
							bold = false;
							italic = false;
							randomCase = false;
							underline = false;
							strikethrough = false;
						}
					if (bold && italic)
						currentFont = boldItalicFont;
					else if (bold)
						currentFont = boldFont;
					else if (italic)
						currentFont = italicFont;
					else
						currentFont = font;

					width += currentFont.getStringWidth(words);
				}
			}
			return width;
		} else
			return font.getStringWidth(text);
	}

	/**
	 * Instantiates the CFont objects that will be used to render the shit.
	 * */
	public void setFont(final Font font, final boolean antiAlias, final int charOffset) {
		synchronized (this) {
			this.font = new RaptureFont(font, antiAlias, charOffset);
			boldFont = new RaptureFont(font.deriveFont(Font.BOLD), antiAlias, charOffset);
			italicFont = new RaptureFont(font.deriveFont(Font.ITALIC), antiAlias, charOffset);
			boldItalicFont = new RaptureFont(font.deriveFont(Font.BOLD | Font.ITALIC), antiAlias, charOffset);
			FONT_HEIGHT = getHeight();
		}
	}

	/**
	 * @return CFont instance.
	 * */
	public RaptureFont getFont() {
		return font;
	}

	/**
	 * @return Font name.
	 * */
	public String getFontName() {
		return font.getFont().getFontName();
	}

	/**
	 * @return Font size.
	 * */
	public int getSize() {
		return font.getFont().getSize();
	}

	public List<String> wrapWords(final String text, final double width) {
		final List<String> finalWords = new ArrayList<String>();
		if (getStringWidth(text) > width) {
			final String[] words = text.split(" ");
			String currentWord = "";
			int stringCount = 0;
			char lastColorCode = (char) -1;

			for(final String word : words) {
				for(int i = 0; i < word.toCharArray().length; i++) {
					final char c = word.toCharArray()[i];

					if ((c == '\247') && (i < (word.toCharArray().length - 1))) lastColorCode = word.toCharArray()[i + 1];
				}
				if (getStringWidth(currentWord + word + " ") < width)
					currentWord += word + " ";
				else {
					finalWords.add(currentWord);
					currentWord = (lastColorCode == -1 ? word + " " : "\247" + lastColorCode + word + " ");
					stringCount++;
				}
			}
			if (!currentWord.equals("")) if (getStringWidth(currentWord) < width) {
				finalWords.add((lastColorCode == -1 ? currentWord + " " : "\247" + lastColorCode + currentWord + " "));
				currentWord = "";
				stringCount++;
			} else
				for(final String s : formatString(currentWord, width))
					finalWords.add(s);
		} else
			finalWords.add(text);
		return finalWords;
	}

	public List<String> formatString(final String s, final double width) {
		final List<String> finalWords = new ArrayList<String>();
		String currentWord = "";
		char lastColorCode = (char) -1;
		for(int i = 0; i < s.toCharArray().length; i++) {
			final char c = s.toCharArray()[i];

			if ((c == '\247') && (i < (s.toCharArray().length - 1))) lastColorCode = s.toCharArray()[i + 1];

			if (getStringWidth(currentWord + c) < width)
				currentWord += c;
			else {
				finalWords.add(currentWord);
				currentWord = (lastColorCode == -1 ? String.valueOf(c) : "\247" + lastColorCode + String.valueOf(c));
			}
		}

		if (!currentWord.equals("")) finalWords.add(currentWord);

		return finalWords;
	}

	/**
	 * Renders a line.
	 * */
	private void drawLine(final double x, final double y, final double x1, final double y1, final float width) {
		glDisable(GL_TEXTURE_2D);
		glLineWidth(width);
		glBegin(GL_LINES);
		glVertex2d(x, y);
		glVertex2d(x1, y1);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

	/**
	 * @return True if the font is anti aliased.
	 * */
	public boolean isAntiAliasing() {
		return font.isAntiAlias() && boldFont.isAntiAlias() && italicFont.isAntiAlias() && boldItalicFont.isAntiAlias();
	}

	/**
	 * Resets the font with or without antialiasing enabled.
	 * */
	public void setAntiAliasing(final boolean antiAlias) {
		font.setAntiAlias(antiAlias);
		boldFont.setAntiAlias(antiAlias);
		italicFont.setAntiAlias(antiAlias);
		boldItalicFont.setAntiAlias(antiAlias);
	}

	/**
	 * Sets up the color codes.
	 * */
	private void setupMinecraftColorcodes() {
		for(int index = 0; index < 32; ++index) {
			final int var6 = ((index >> 3) & 1) * 85;
			int var7 = (((index >> 2) & 1) * 170) + var6;
			int var8 = (((index >> 1) & 1) * 170) + var6;
			int var9 = (((index >> 0) & 1) * 170) + var6;

			if (index == 6) var7 += 85;

			if (index >= 16) {
				var7 /= 4;
				var8 /= 4;
				var9 /= 4;
			}

			colorCode[index] = ((var7 & 255) << 16) | ((var8 & 255) << 8) | (var9 & 255);
		}
	}

	@Override
	public String trimStringToWidth(final String p_78269_1_, final int p_78269_2_) {
		return this.trimStringToWidth(p_78269_1_, p_78269_2_, false);
	}

	@Override
	public String trimStringToWidth(final String p_78262_1_, final int p_78262_2_, final boolean p_78262_3_) {
		final StringBuilder var4 = new StringBuilder();
		int var5 = 0;
		final int var6 = p_78262_3_ ? p_78262_1_.length() - 1 : 0;
		final int var7 = p_78262_3_ ? -1 : 1;
		boolean var8 = false;
		boolean var9 = false;

		for(int var10 = var6; (var10 >= 0) && (var10 < p_78262_1_.length()) && (var5 < p_78262_2_); var10 += var7) {
			final char var11 = p_78262_1_.charAt(var10);
			final int var12 = getStringWidth(Character.toString(var11));
			if (var8) {
				var8 = false;
				if ((var11 != 108) && (var11 != 76)) {
					if ((var11 == 114) || (var11 == 82)) var9 = false;
				} else
					var9 = true;
			} else if (var12 < 0)
				var8 = true;
			else {
				var5 += var12;
				if (var9) ++var5;
			}

			if (var5 > p_78262_2_) break;

			if (p_78262_3_)
				var4.insert(0, var11);
			else
				var4.append(var11);
		}

		return var4.toString();
	}

	@Override
	public List listFormattedStringToWidth(final String str, final int wrapWidth) {
		return Arrays.asList(wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
	}

	@Override
	protected String wrapFormattedStringToWidth(final String str, final int wrapWidth) {
		final int var3 = sizeStringToWidth(str, wrapWidth);
		if (str.length() <= var3)
			return str;
		else {
			final String var4 = str.substring(0, var3);
			final char var5 = str.charAt(var3);
			final boolean var6 = (var5 == 32) || (var5 == 10);
			final String var7 = getFormatFromString(var4) + str.substring(var3 + (var6 ? 1 : 0));
			return var4 + "\n" + wrapFormattedStringToWidth(var7, wrapWidth);
		}
	}

	public Color getColor(final int colorCode, final float alpha) {
		return new Color((colorCode >> 16) / 255F, ((colorCode >> 8) & 0xff) / 255F, (colorCode & 0xff) / 255F, alpha);
	}

	/**
	 * Sets up the color code identifier.
	 * */
	private String setupColorcodeIdentifier() {
		String minecraftColorCodes = "0123456789abcdefklmnor";
		for(int i = 0; i < customColorCodes.length; i++)
			if (customColorCodes[i] != null) minecraftColorCodes += (char) i;
		return minecraftColorCodes;
	}

	@Override
	public void onResourceManagerReload(final IResourceManager p_110549_1_) {
	}

	public static RaptureFontRenderer createFont() {
		try {
//			InputStream istream = Font.class.getResourceAsStream("/assets/minecraft/rapture/fonts/Monaco.ttf");
//			Font myFont = Font.createFont(Font.TRUETYPE_FONT, istream);
//			myFont = myFont.deriveFont(18.0F);
//			return new RaptureFontRenderer(myFont, true, 8);
			return new RaptureFontRenderer(new Font("Lato", Font.PLAIN, 18), true, 8);
		} catch (Exception e) {
			return new RaptureFontRenderer(new Font("Verdana", Font.PLAIN, 18), true, 8);
		}
	}
}
