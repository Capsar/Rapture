package pq.rapture.render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pq.rapture.Wrapper;
import pq.rapture.util.FontUtil;

public class GUIUtil extends Gui {

	public static void drawGradientRect(double x1, double z1, double x2, double z2, int par5, int par6) {
		float var7 = (par5 >> 24 & 255) / 255.0F;
		float var8 = (par5 >> 16 & 255) / 255.0F;
		float var9 = (par5 >> 8 & 255) / 255.0F;
		float var10 = (par5 & 255) / 255.0F;
		float var11 = (par6 >> 24 & 255) / 255.0F;
		float var12 = (par6 >> 16 & 255) / 255.0F;
		float var13 = (par6 >> 8 & 255) / 255.0F;
		float var14 = (par6 & 255) / 255.0F;
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		glShadeModel(GL_SMOOTH);
		Wrapper.getWorldRenderer().startDrawingQuads();
		Wrapper.getWorldRenderer().setColorRGBA_F(var8, var9, var10, var7);
		Wrapper.getWorldRenderer().addVertex(x2, z1, 0.0);
		Wrapper.getWorldRenderer().addVertex(x1, z1, 0.0);
		Wrapper.getWorldRenderer().setColorRGBA_F(var12, var13, var14, var11);
		Wrapper.getWorldRenderer().addVertex(x1, z2, 0.0);
		Wrapper.getWorldRenderer().addVertex(x2, z2, 0.0);
		Tessellator.getInstance().draw();
		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
	}

	public static void drawSexyRect(double posX, double posY, double posX2, double posY2, int col1, int col2) {
		drawRect(posX, posY, posX2, posY2, col2);

		float alpha = (float) (col1 >> 24 & 255) / 255.0F;
		float red = (float) (col1 >> 16 & 255) / 255.0F;
		float green = (float) (col1 >> 8 & 255) / 255.0F;
		float blue = (float) (col1 & 255) / 255.0F;

		Tessellator tess = Tessellator.getInstance();
		WorldRenderer world = tess.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(red, green, blue, alpha);
		glPushMatrix();
		glLineWidth(2);
		glBegin(GL_LINES);
		glVertex2d(posX, posY);
		glVertex2d(posX, posY2);
		glVertex2d(posX2, posY2);
		glVertex2d(posX2, posY);
		glVertex2d(posX, posY);
		glVertex2d(posX2, posY);
		glVertex2d(posX, posY2);
		glVertex2d(posX2, posY2);
		glEnd();
		glPopMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		double var5;

		if (left < right) {
			var5 = left;
			left = right;
			right = var5;
		}

		if (top < bottom) {
			var5 = top;
			top = bottom;
			bottom = var5;
		}

		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;
		Tessellator var9 = Tessellator.getInstance();
		WorldRenderer var10 = var9.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(red, green, blue, alpha <= 0.01 ? 1 : alpha);
		var10.startDrawingQuads();
		var10.addVertex((double) left, (double) bottom, 0.0D);
		var10.addVertex((double) right, (double) bottom, 0.0D);
		var10.addVertex((double) right, (double) top, 0.0D);
		var10.addVertex((double) left, (double) top, 0.0D);
		var9.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawBorderedRect(double x, double y, double x1, double y1, double size, int borderC, int insideC) {
		drawRect(x + size, y + size, x1 - size, y1 - size, insideC);
		drawRect(x + size, y + size, x1, y, borderC);
		drawRect(x, y, x + size, y1, borderC);
		drawRect(x1, y1, x1 - size, y + size, borderC);
		drawRect(x + size, y1 - size, x1 - size, y1, borderC);
	}

	public static void drawTexturedRectangle(ResourceLocation resourceLocation, double posX, double posY, float width, float height, float r, float g, float b) {
		float u = 1, v = 1, uWidth = 1, vHeight = 1, textureWidth = 1, textureHeight = 1;
		glEnable(GL_BLEND);
		glColor4f(r, g, b, 1);
		Wrapper.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		glBegin(GL_QUADS);
		glTexCoord2d(u / textureWidth, v / textureHeight);
		glVertex2d(posX, posY);
		glTexCoord2d(u / textureWidth, (v + vHeight) / textureHeight);
		glVertex2d(posX, posY + height);
		glTexCoord2d((u + uWidth) / textureWidth, (v + vHeight) / textureHeight);
		glVertex2d(posX + width, posY + height);
		glTexCoord2d((u + uWidth) / textureWidth, v / textureHeight);
		glVertex2d(posX + width, posY);
		glEnd();
		glDisable(GL_BLEND);
	}

	public static void renderItemAndEffect(ItemStack item, int with, int high) {
		if (item != null) {
			RenderHelper.enableGUIStandardItemLighting();
			Wrapper.getRenderItem().renderItemAndEffectIntoGUI(item, with + 7, high);
			RenderHelper.disableStandardItemLighting();
		}
	}

	public static void renderItemOverLay(ItemStack item, int with, int high) {
		if (item != null) {
			RenderHelper.enableGUIStandardItemLighting();
			renderItemOverlayIntoGUI(item, with + 7, high, (String) null);
			RenderHelper.disableStandardItemLighting();
		}
	}

	public static void renderItemOverlayIntoGUI(ItemStack par3ItemStack, int par4, int par5, String par6Str) {
		if (par3ItemStack != null) {
			String damage = String.valueOf(par3ItemStack.getMaxDamage() - par3ItemStack.getItemDamage() + 1);
			if (par3ItemStack.isItemDamaged()) {
				glDisable(GL_LIGHTING);
				glDisable(GL_DEPTH_TEST);
				glEnable(GL_LIGHTING);
				glEnable(GL_DEPTH_TEST);
				int var12 = (int) Math.round(13.0D - (double) par3ItemStack.getItemDamage() * 13.0D / (double) par3ItemStack.getMaxDamage());
				int var8 = (int) Math.round(255.0D - (double) par3ItemStack.getItemDamage() * 255.0D / (double) par3ItemStack.getMaxDamage());
				glDisable(GL_DEPTH_TEST);
				glDisable(GL_TEXTURE_2D);
				glDisable(GL_ALPHA_TEST);
				WorldRenderer var9 = Tessellator.getInstance().getWorldRenderer();
				int var10 = 255 - var8 << 16 | var8 << 8;
				int var11 = (255 - var8) / 4 << 16 | 16128;
				Wrapper.getRenderItem().drawRect(var9, par4 + 2, par5 + 13, 13, 2, 0);
				Wrapper.getRenderItem().drawRect(var9, par4 + 2, par5 + 13, 12, 1, var11);
				Wrapper.getRenderItem().drawRect(var9, par4 + 2, par5 + 13, var12, 1, var10);
				glEnable(GL_TEXTURE_2D);
				FontUtil.drawScaledString(damage, par4 + 8, par5 + 10, 0xFFFFFF, 0.6F, true);
				glEnable(GL_DEPTH_TEST);
				glEnable(GL_ALPHA_TEST);

				glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
			if (par3ItemStack.stackSize > 1 || par6Str != null) {
				String var7 = par6Str == null ? String.valueOf(par3ItemStack.stackSize) : par6Str;
				glDisable(GL_LIGHTING);
				glDisable(GL_DEPTH_TEST);
				FontUtil.drawScaledString(var7, par4 + 15 - (var7.length() * 4), par5 + 3, 0xFFFFFF, 0.55F, true);
				glEnable(GL_LIGHTING);
				glEnable(GL_DEPTH_TEST);
			}
		}
	}

	public static void renderESPOverLay(ItemStack stack, int x, int y, RenderItem itemRender) {
		if (stack != null) {
			String damage = String.valueOf(stack.getMaxDamage() - stack.getItemDamage() + 1);
			FontRenderer fr = Wrapper.getFontRenderer();
			int stringWidth = (int) (fr.getStringWidth(damage) * 0.8F);
			if (stack.isItemDamaged()) {
				GlStateManager.disableLighting();
				GlStateManager.disableTexture2D();
				;
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				int var12 = (int) Math.round(13.0D - (double) stack.getItemDamage() * 13.0D / (double) stack.getMaxDamage());
				int var8 = (int) Math.round(255.0D - (double) stack.getItemDamage() * 255.0D / (double) stack.getMaxDamage());
				WorldRenderer var9 = Tessellator.getInstance().getWorldRenderer();
				int var10 = 255 - var8 << 16 | var8 << 8;
				int var11 = (255 - var8) / 4 << 16 | 16128;
				Wrapper.getRenderItem().drawRect(var9, x + 2, y + 13, 13, 2, 0);
				Wrapper.getRenderItem().drawRect(var9, x + 2, y + 13, 12, 1, var11);
				Wrapper.getRenderItem().drawRect(var9, x + 2, y + 13, var12, 1, var10);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.enableTexture2D();
			}
		}
	}

	public static void drawESPItem(ItemStack item, int x, int y, RenderItem itemRender) {
		if (item != null) {
			IBakedModel bakedModel = Wrapper.getRenderItem().getItemModelMesher().getItemModel(item);
			Wrapper.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			Wrapper.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, true);
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

			GlStateManager.translate(x, y - 3, 0);
			GlStateManager.translate(8.0F, 8.0F, 0.0F);
			GlStateManager.scale(1.0F, 1.0F, -1.0F);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);

			if (bakedModel.isAmbientOcclusion()) {
				GlStateManager.scale(35.0F, 35.0F, 0.0F);
				GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.enableLighting();
			} else {
				GlStateManager.scale(64.0F, 64.0F, 0.0F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.disableLighting();
			}
			GlStateManager.enableDepth();
			GlStateManager.disableLighting();
			itemRender.renderItem(item, bakedModel);

			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableLighting();
			GlStateManager.popMatrix();

			Wrapper.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			Wrapper.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
			;
		}
	}

	public static void scissor( double x,  double y,  double w,  double h) {
		float factor = Wrapper.getScaledResolution().getScaleFactor();
		double x2 = x + w, y2 = y + h;
		GL11.glScissor((int) (x * factor), (int) ((Wrapper.getScaledResolution().getScaledHeight() - y2) * factor), (int) (w * factor), (int) (h * factor));
	}

	public static boolean mouseInsideBox(double mouseX, double mouseY,  double x,  double y,  double x2,  double y2) {
		return mouseX > x && mouseX < x2 && mouseY > y && mouseY < y2;
	}
	
}
