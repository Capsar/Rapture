package pq.rapture.render.altmanager;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;

import pq.rapture.Wrapper;
import pq.rapture.render.GUIUtil;

public class SlotAlt extends GuiSlot {
	private GuiAltList aList;
	private int selected;
	Random rand = new Random();
	String i = String.valueOf(rand.nextInt(1000));

	public SlotAlt(Minecraft theMinecraft, GuiAltList aList) {
		super(theMinecraft, aList.width, aList.height, 32, aList.height - 59, Manager.slotHeight);
		this.aList = aList;
		this.selected = 0;
	}

	@Override
	protected int getContentHeight() {
		return this.getSize() * Manager.slotHeight;
	}

	@Override
	protected int getSize() {
		return Manager.altList.size();
	}

	@Override
	protected void elementClicked(int var1, boolean var2, int var3, int var4) {
		setSelected(var1);
		if (var2) {
			try {
				Session session = Manager.getResponse(Manager.getList().get(var1).getUsername(), Manager.getList().get(var1).getPassword());
				if (session != null) {
					Wrapper.getMinecraft().session = session;
					Manager.altScreen.setErrorString("Succesfull login");
				} else
					throw new Exception();
			} catch (Exception error) {
				Manager.altScreen.setErrorString("\247cBad Login \2477(" + Manager.getList().get(var1).getUsername() + ")");
			}

		}
	}

	@Override
	protected boolean isSelected(int var1) {
		return this.selected == var1;
	}

	public void setSelected(int var1) {
		this.selected = var1;
	}

	protected int getSelected() {
		return this.selected;
	}

	@Override
	protected void drawBackground() {
		aList.drawDefaultBackground();
	}

	@Override
	protected void drawSlot(int selectedIndex, int x, int y, int var4, int var5, int var6) {
		Alt theAlt = Manager.altList.get(selectedIndex);
		String user;
		if (theAlt.getNickname() == null) {
			aList.drawString(aList.fr(), theAlt.getUsername(), x, y + 1, 0xFFFFFF);
			user = theAlt.getUsername();
		} else {
			aList.drawString(aList.fr(), theAlt.getNickname(), x, y + 1, 0xFFFFFF);
			user = theAlt.getNickname();
		}
		if (theAlt.isPremium()) {
			if (theAlt.getPassword().length() > 15) {
				aList.drawString(aList.fr(), Manager.makePassChar("wadrf24f2da" + i), x, y + 12, 0x808080);
			} else {
				aList.drawString(aList.fr(), Manager.makePassChar(theAlt.getPassword() + i), x, y + 12, 0x808080);
			}
		} else {
			aList.drawString(aList.fr(), "N/A", x, y + 12, 0x808080);
		}
		GL11.glScalef(0.5F, 0.25F, 0.25F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 255.0F);
		downloadSkin(user);
		Gui.drawScaledCustomSizeModalRect(aList.width + 178, y * 4 + 5, 8.0F, 8.0F, 8, 8, 38, 74, 64.0F, 64.0F);
		GUIUtil.drawBorderedRect(aList.width + 177, y * 4 + 4, aList.width + 216, y * 4 + 80, 1, 0xAA2c6700, 0x0);
		GL11.glScalef(2.0F, 4.0F, 4.0F);
	}

	private void downloadSkin(String username) {
		ResourceLocation localSkin = new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
		TextureManager var4 = Minecraft.getMinecraft().getTextureManager();
		ITextureObject var5 = var4.getTexture(localSkin);
		if (var5 == null) {
			ThreadDownloadImageData var51 = new ThreadDownloadImageData(null, String.format(
					"http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(username) }),
					new ResourceLocation("textures/entity/steve.png"), new ImageBufferDownload());
			var4.loadTexture(localSkin, var51);
		}
		Wrapper.getMinecraft().getTextureManager().bindTexture(localSkin);
	}

	public void setScreenToIndex(int indexOf) {
		amountScrolled = indexOf * 25;
		this.bindAmountScrolled();
	}

}
