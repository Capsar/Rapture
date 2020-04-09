package pq.rapture.render.altmanager;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.Session;
import pq.rapture.Wrapper;
import pq.rapture.hooks.GuiMainMenuHook;
import pq.rapture.protection.Protection;
import pq.rapture.protection.SimpleCrypto;
import pq.rapture.util.FontUtil;

public class GuiAltList extends GuiScreen {

	public static String dispErrorString = "";
	public boolean deleteMenuOpen = false;
	private int offset = 100;

	public GuiAltList() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}
		Manager.loadAlts();
	}

	public FontRenderer fr() {
		return Wrapper.getMinecraft().fontRendererObj;
	}

	public void onGuiClosed() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}
		Manager.saveAlts();
		super.onGuiClosed();
	}

	private SlotAlt tSlot;
	public GuiTextField searchBox;

	public void setErrorString(String e) {
		dispErrorString = e;
	}

	public void initGui() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}

		buttonList.clear();
		buttonList.add(new GuiButton(1, width / 2 - 100 - offset, height - 47, 66, 20, Protection
				.decrypt("B21FE5A8165A9850F27360EA4176519F")));
		buttonList.add(new GuiButton(2, width / 2 - 33 - offset, height - 47, 65, 20, Protection
				.decrypt("64150AD762355DB92D5F40E6CB8FF824")));
		buttonList.add(new GuiButton(3, width / 2 + 32 - offset, height - 47, 65, 20, Protection
				.decrypt("7F199F5C9A6A47D9DE39D434769E389D")));
		buttonList.add(new GuiButton(7, width / 2 + 97 - offset, height - 47, 60, 20, Protection
				.decrypt("81F5EFA5E7676E214151D783321A0DB3")));
		buttonList.add(new GuiButton(10, width / 2 + 158 - offset, height - 47, 67, 20, Protection
				.decrypt("516EC412DD14E44408D5F3FC38AE99BB")));
		buttonList.add(new GuiButton(4, width / 2 - 100 - offset, height - 26, 56, 20, Protection
				.decrypt("10CA4B7B953DE515F1084DB1C9B73E45")));
		buttonList.add(new GuiButton(6, width / 2 - 43 - offset, height - 26, 53, 20, Protection
				.decrypt("58F6BF5F1071A9501FDA170C943739A6")));
		buttonList.add(new GuiButton(5, width / 2 + 10 - offset, height - 26, 80, 20, Protection
				.decrypt("C8582202477537A04750A11F756C428D")));
		buttonList.add(new GuiButton(8, width / 2 + 91 - offset, height - 26, 67, 20, Protection
				.decrypt("E42AFB64414619DA8ED0F2BDB574A52B")));
		buttonList.add(new GuiButton(9, width / 2 + 158 - offset, height - 26, 67, 20, Protection
				.decrypt("0B52D926E0D29C2231A1E39AABEA1C34")));
		searchBox = new GuiTextField(0, fr(), width / 2 + 140, height - 25, 100, 17);
		searchBox.setFocused(true);
		searchBox.setMaxStringLength(200);

		tSlot = new SlotAlt(this.mc, this);
		tSlot.registerScrollButtons(7, 8);
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		searchBox.mouseClicked(x, y, b);
		try {
			super.mouseClicked(x, y, b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void keyTyped(char c, int i) {
		searchBox.textboxKeyTyped(c, i);
		if (c == '\t') {
			if (searchBox.isFocused()) {
				searchBox.setFocused(false);
			} else {
				searchBox.setFocused(true);

			}
		}
		for(Alt a : Manager.altList) {
			if (searchBox.getText().equals("Username") || searchBox.getText().isEmpty()) continue;
			if (a.getNickname().toLowerCase().startsWith(searchBox.getText().toLowerCase())) {
				tSlot.setSelected(Manager.altList.indexOf(a));
				tSlot.setScreenToIndex(Manager.altList.indexOf(a));
			}
		}
		if (c == '\r') {
			searchBox.setFocused(false);
			loginAlt();
		}
	}

	private void loginAlt() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}

		try {
			Alt a1 = Manager.altList.get(tSlot.getSelected());
			if (a1.isPremium()) {
				try {
					Session session = Manager.getResponse(a1.getUsername(), a1.getPassword());
					if (session != null) {
						Wrapper.getMinecraft().session = session;
						dispErrorString = "Succesful login";
					} else
						throw new Exception();
				} catch (Exception error) {
					dispErrorString = "".concat("\247c" + Protection.decrypt("59FDACD8565E40B768DE96D3C1EA9A20") + " \2477(")
							.concat(a1.getUsername()).concat(")");
				}
			} else {
				Wrapper.getMinecraft().session = new Session(a1.getUsername(), "-", "-", Session.Type.MOJANG.name());
				dispErrorString = "";
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void confirmClicked(boolean flag, int i1) {
		super.confirmClicked(flag, i1);
		if (deleteMenuOpen) {
			deleteMenuOpen = false;

			if (flag) {
				Manager.altList.remove(i1);
				Manager.saveAlts();
			}

			mc.displayGuiScreen(this);
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}

		switch (button.id) {
		case 1:
			GuiAltAdd gaa = new GuiAltAdd(this);
			mc.displayGuiScreen(gaa);
			break;
		case 2:
			loginAlt();
			break;
		case 3:
			try {
				Manager.removeAlt(Manager.altList.get(tSlot.getSelected()));
			} catch (Exception e) {
			}
			break;
		case 4:
			mc.displayGuiScreen(new GuiMainMenuHook());
			break;
		case 5:
			GuiDirectLogin gdl = new GuiDirectLogin(this);
			mc.displayGuiScreen(gdl);
			break;
		case 6:
			mc.displayGuiScreen(new GuiAltEdit(this, tSlot.getSelected()));
			break;
		case 7:
			if (!Manager.getList().isEmpty()) {
				if (Manager.getList().get(tSlot.getSelected()).getUsername().contains("@")) {
					try {
						String currentString = mc.getSession().getUsername();
						loginAlt();
						if (!currentString.equals(mc.getSession().getUsername())) {
							Alt theAlt = new Alt(Manager.getList().get(tSlot.getSelected()).getUsername(), Manager.getList()
									.get(tSlot.getSelected()).getPassword(), Wrapper.getMinecraft().getSession().getUsername());
							Manager.removeAlt(Manager.altList.get(tSlot.getSelected()));
							Manager.addAlt(theAlt, tSlot.getSelected());
						}
					} catch (Exception e) {
						e.printStackTrace();
						dispErrorString = Protection
								.decrypt("EBE55A580BC0A927B5525D91BE94F6ADE87B181FCEC942E228D04A183B72EA27C21AE51A3851950334C08E7DEA7B8E98621D141517DD362BE1FD8961A8BC45C1");

					}
				} else {
					dispErrorString = Protection
							.decrypt("CFE3C0F34A5EE6B711C54D4115D24277A01F04521EAD5B0AD706869DB988205DB2164B606C00529BEB865775030BD5CD");
				}
			}
			break;
		case 8:
			Manager.openList();
			break;
		case 9:
			if (!Manager.getList().isEmpty()) {
				if (tSlot.getSelected() != Manager.getList().size()) {
					try {
						Alt theAlt = Manager.getList().get(tSlot.getSelected());
						Manager.removeAlt(theAlt);
						Manager.addAlt(theAlt, tSlot.getSelected() + 1);
					} catch (Exception e) {
					}
				}
			}
			break;
		case 10:
			Manager.altList.clear();
			Manager.loadAlts();
			Manager.saveAlts();
			break;
		default:
			tSlot.actionPerformed(button);
		}

	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.tSlot.handleMouseInput();
	}

	public void updateScreen() {
		super.updateScreen();
		if (!(mc.currentScreen instanceof GuiYesNo) && deleteMenuOpen) {
			deleteMenuOpen = false;
		}
		searchBox.updateCursorCounter();
	}

	public void drawScreen(int i, int j, float f) {
		tSlot.drawScreen(i, j, f);
		drawCenteredString(fr(), Protection.decrypt("EF7B282126C6BFB53DDAA9E9380FB9F5") + "\2477" + Manager.altList.size(), width / 2, 13,
				0xFFFFFF);
		FontUtil.drawStringWithShadow(Protection.decrypt("5118B30BF70CF43669FE1ACAE67B1C62") + "\2477" + mc.getSession().getUsername(), 3,
				3, 0xFFFFFF);
		FontUtil.drawStringWithShadow(dispErrorString, 3, 13, 0xFFFFFF);
		FontUtil.drawStringWithShadow(Protection.decrypt("4AA894F7A1E47830EB84324D394CB0CF"), width / 2 + 170, height - 41, 0xFFFFFF);
		searchBox.drawTextBox();

		super.drawScreen(i, j, f);
	}
}
