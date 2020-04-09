package pq.rapture.hooks;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import pq.rapture.protection.Protection;
import pq.rapture.protection.screen.GAuthScreen;
import pq.rapture.render.altmanager.GuiAltList;

import java.io.IOException;

/**
 * This class must be initialized everywhere the game initializes the GuiMainMenu.
 */
public class GuiMainMenuHook extends GuiMainMenu {

	@Override
	public void initGui() {
		super.initGui();
		if(!Protection.isValid)
			return;


		buttonList.clear();
		int var4 = this.height / 4 + 48;
		buttonList.add(new GuiButton(0, width / 2 - 100, var4 + 72, 98, 20, I18n.format(Protection.decrypt("25F3B32844403E032124E34B30394733"), new Object[0])));
		buttonList.add(new GuiButton(1, width / 2 - 100, var4, I18n.format(Protection.decrypt("543E223EE94FC607A40C01718B0F52CDA418BF1AAFCBD7C3F3982501429C3E80"), new Object[0])));
		buttonList.add(new GuiButton(2, width / 2 - 100, var4 + 24 * 1, I18n.format(Protection.decrypt("BEC894517EE7EA2B2E8AF9B01DE8C67715319490761930059D7FEF3DD60AA33A"), new Object[0])));
		buttonList.add(new GuiButton(4, width / 2 + 2, var4 + 72, 98, 20, I18n.format(Protection.decrypt("19C1D6546F46B46F1DC6AFB5962BE2C7"), new Object[0])));
		buttonList.add(new GuiButtonLanguage(5, width / 2 - 124, var4 + 72));
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) throws IOException {
		if(!Protection.isValid) {
			super.actionPerformed(par1GuiButton);
			return;
		}
			
			
		if (par1GuiButton.id == 0) {
			mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
		}
		if (par1GuiButton.id == 1) {
			mc.displayGuiScreen(new GuiSelectWorld(this));
		}
		if (par1GuiButton.id == 2) {
			mc.displayGuiScreen(new GuiMultiplayer(this));
		}
		if (par1GuiButton.id == 4) {
			mc.shutdown();
		}
		if (par1GuiButton.id == 5) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Protection.isValidGAUTH) {
				mc.displayGuiScreen(new GuiAltList());
			} else if (!Protection.isValidGAUTH && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				this.mc.displayGuiScreen(new GuiYesNo(new GAuthScreen(mc), "Have you set-up Google Authentication with Rapture?", "", 1));
			} else {
				this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
			}
		}
	}

}
