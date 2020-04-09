package pq.rapture.render.altmanager;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import pq.rapture.Wrapper;
import pq.rapture.render.GuiPasswordField;

public class GuiAltEdit extends GuiScreen {
	public GuiScreen parent;
	public GuiTextField usernameBox;
	public GuiPasswordField passwordBox;
	public GuiTextField nicknameBox;
	private int altIndex;

	public GuiAltEdit(GuiScreen paramScreen, int altIndex) {
		this.parent = paramScreen;
		this.altIndex = altIndex;
	}

	public void updateScreen() {
		usernameBox.updateCursorCounter();
		passwordBox.updateCursorCounter();
		nicknameBox.updateCursorCounter();
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96 + 12, "Save Alt"));
		buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 + 36, "Change ign to current account"));
		buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 96 + 60, "Back"));
		usernameBox = new GuiTextField(0, Wrapper.getMinecraft().fontRendererObj, width / 2 - 100, 76, 200, 20);
		usernameBox.setFocused(true);
		usernameBox.setText(Manager.altList.get(altIndex).getUsername());
		usernameBox.setMaxStringLength(200);
		passwordBox = new GuiPasswordField(Wrapper.getMinecraft().fontRendererObj, width / 2 - 100, 116, 200, 20);
		if (Manager.altList.get(altIndex).getPassword() != null) {
			passwordBox.setText(Manager.altList.get(altIndex).getPassword());
			passwordBox.setMaxStringLength(128);
		}
		nicknameBox = new GuiTextField(0, Wrapper.getMinecraft().fontRendererObj, width / 2 - 100, 156, 200, 20);
		if (Manager.altList.get(altIndex).getNickname() != null) {
			nicknameBox.setText(Manager.altList.get(altIndex).getNickname());
			nicknameBox.setMaxStringLength(200);
		}
	}

	public void mouseClicked(int x, int y, int b) {
		usernameBox.mouseClicked(x, y, b);
		passwordBox.mouseClicked(x, y, b);
		nicknameBox.mouseClicked(x, y, b);
		try {
			super.mouseClicked(x, y, b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			Manager.altList.remove(Manager.altList.get(altIndex));
			if (!usernameBox.getText().trim().isEmpty() && passwordBox.getText().trim().isEmpty()) {
				Alt theAlt = new Alt(usernameBox.getText().trim());
				if (!Manager.altList.contains(theAlt)) {
					Manager.altList.add(theAlt);
					Manager.saveAlts();
				}
			} else if (!usernameBox.getText().trim().isEmpty() && !passwordBox.getText().isEmpty() && nicknameBox.getText().isEmpty()) {
				Alt theAlt = new Alt(usernameBox.getText().trim(), passwordBox.getText().trim(), usernameBox.getText().trim());
				if (!Manager.altList.contains(theAlt)) {
					Manager.altList.add(theAlt);
					Manager.saveAlts();
				}
			} else if (!usernameBox.getText().trim().isEmpty() && !nicknameBox.getText().trim().isEmpty()
					&& !passwordBox.getText().isEmpty()) {
				Alt theAlt = new Alt(usernameBox.getText().trim(), passwordBox.getText().trim(), nicknameBox.getText().trim());
				if (!Manager.altList.contains(theAlt)) {
					Manager.altList.add(theAlt);
					Manager.saveAlts();
				}
			}
			mc.displayGuiScreen(parent);
		} else if (button.id == 2) {
			nicknameBox.setText(mc.getSession().getUsername());
		} else if (button.id == 3) {
			mc.displayGuiScreen(parent);
		}
	}

	protected void keyTyped(char c, int i) {
		usernameBox.textboxKeyTyped(c, i);
		passwordBox.textboxKeyTyped(c, i);
		nicknameBox.textboxKeyTyped(c, i);
		if (c == '\t') {
			if (usernameBox.isFocused()) {
				usernameBox.setFocused(false);
				passwordBox.setFocused(true);
				nicknameBox.setFocused(false);
			} else if (passwordBox.isFocused) {
				usernameBox.setFocused(false);
				passwordBox.setFocused(false);
				nicknameBox.setFocused(true);
			} else if (nicknameBox.isFocused()) {
				usernameBox.setFocused(true);
				passwordBox.setFocused(false);
				nicknameBox.setFocused(false);
			}
		}
		if (c == '\r') {
			actionPerformed((GuiButton) buttonList.get(0));
		}
	}

	public void drawScreen(int x, int y, float f) {
		drawDefaultBackground();
		drawString(Wrapper.getMinecraft().fontRendererObj, "Username", width / 2 - 100, 63, 0xFFa0a0a0);
		drawString(Wrapper.getMinecraft().fontRendererObj, "Password", width / 2 - 100, 104, 0xFFa0a0a0);
		if (usernameBox.getText().contains("@")) {
			drawString(Wrapper.getMinecraft().fontRendererObj, "InGameName", width / 2 - 100, 145, 0xFFa0a0a0);
		}
		try {
			usernameBox.drawTextBox();
			passwordBox.drawTextBox();
			if (usernameBox.getText().contains("@")) {
				nicknameBox.drawTextBox();
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
		super.drawScreen(x, y, f);
	}
}
