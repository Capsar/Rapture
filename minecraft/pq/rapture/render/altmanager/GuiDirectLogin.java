package pq.rapture.render.altmanager;

import java.io.IOException;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;

import org.lwjgl.input.Keyboard;

import pq.rapture.Wrapper;
import pq.rapture.render.GuiPasswordField;

public class GuiDirectLogin extends GuiScreen {
	public GuiScreen parent;
	public GuiTextField usernameBox;
	public GuiPasswordField passwordBox;
	private FontRenderer fr = Wrapper.getMinecraft().fontRendererObj;

	public GuiDirectLogin(GuiScreen paramScreen) {
		this.parent = paramScreen;
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96 + 12, "Login"));
		buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 + 36, "Back"));
		usernameBox = new GuiTextField(0, fr, width / 2 - 100, 76 - 25, 200, 20);
		passwordBox = new GuiPasswordField(fr, width / 2 - 100, 116 - 25, 200, 20);
		usernameBox.setMaxStringLength(200);
		passwordBox.setMaxStringLength(128);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void updateScreen() {
		usernameBox.updateCursorCounter();
		passwordBox.updateCursorCounter();
	}

	public void mouseClicked(int x, int y, int b) {
		usernameBox.mouseClicked(x, y, b);
		passwordBox.mouseClicked(x, y, b);
		try {
			super.mouseClicked(x, y, b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			if (!usernameBox.getText().trim().isEmpty() && !passwordBox.getText().trim().isEmpty()) {
				try {
					Session session = Manager.getResponse(usernameBox.getText().trim(), passwordBox.getText().trim());
					if (session != null) {
						mc.session = session;
						Manager.altScreen.dispErrorString = "";
					} else
						throw new Exception();
				} catch (Exception error) {
					Manager.altScreen.dispErrorString = "\247cBad Login \2477(" + usernameBox.getText() + ")";
				}
			} else if (!usernameBox.getText().trim().isEmpty()) {
				String usernameBoxxy = usernameBox.getText().trim();
				if (usernameBoxxy.contains(":")) {
					String[] account = usernameBoxxy.split(":");
					String userName = account[0];
					String password = account[1];
					try {
						Session session = Manager.getResponse(userName, password);
						if (session != null) {
							mc.session = session;
							Manager.altScreen.dispErrorString = "";
						} else
							throw new Exception();
					} catch (Exception error) {
						Manager.altScreen.dispErrorString = "\247cBad Login \2477(" + userName + ")";
					}

				} else {
					mc.session = new Session(usernameBox.getText().trim(), "-", "-", Session.Type.MOJANG.name());
					Manager.altScreen.dispErrorString = "Changed Name to: " + mc.session.getUsername();
				}
			}

			mc.displayGuiScreen(parent);
		} else if (button.id == 2) {
			mc.displayGuiScreen(parent);
		}
	}

	protected void keyTyped(char c, int i) {
		usernameBox.textboxKeyTyped(c, i);
		passwordBox.textboxKeyTyped(c, i);
		if (c == '\t') {
			if (usernameBox.isFocused()) {
				usernameBox.setFocused(false);
				passwordBox.setFocused(true);
			} else if (passwordBox.isFocused()) {
				usernameBox.setFocused(false);
				passwordBox.setFocused(false);
			}
		}
		if (c == '\r') {
			actionPerformed((GuiButton) buttonList.get(0));
		}
	}

	public void drawScreen(int x, int y, float f) {
		drawDefaultBackground();
		drawString(fr, "Username", width / 2 - 100, 63 - 25, 0xFFA0A0A0);
		drawString(fr, "Password", width / 2 - 100, 104 - 25, 0xFFA0A0A0);
		try {
			usernameBox.drawTextBox();
			passwordBox.drawTextBox();
		} catch (Exception err) {
			err.printStackTrace();
		}
		super.drawScreen(x, y, f);
	}
}
