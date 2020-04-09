package pq.rapture.command;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import pq.rapture.command.base.Command;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;

public class Bind extends Command {

	public Bind() {
		super(new String[] { "bind", "b" }, "Bind your modules.");
	}

	@Override
	protected void onCommand(String[] args, String message) {
		switch (args.length) {
		case 3:
			boolean found = false;
			for(Module m : ModuleManager.modules) {
				String modulename = m.getName().replace(" ", "");
				if (args[1].equalsIgnoreCase(modulename)) {
					found = true;
					if (m.getKey().equalsIgnoreCase(args[2])) {
						addChat(ChatEnum.ERROR, m.getName() + " already has key " + args[2].toUpperCase() + " bound to it!");
						break;
					} else {
						if (args[2].toUpperCase().contains("BUTTON")) {
							if (Mouse.getButtonName(Mouse.getButtonIndex(args[2].toUpperCase())).equals("NONE")
									&& !args[2].toUpperCase().equals("NONE")) {
								addChat(ChatEnum.ERROR, "Could not find button: " + args[2]);
								break;
							}
						} else if (Keyboard.getKeyName(Keyboard.getKeyIndex(args[2].toUpperCase())).equals("NONE")
								&& !args[2].toUpperCase().equals("NONE")) {
							addChat(ChatEnum.ERROR, "Could not find key: " + args[2]);
							break;
						}
						m.setKey(args[2].toUpperCase());
						addChat(ChatEnum.NOTIFY, m.getName() + " is now bound to " + args[2].toUpperCase() + ".");
						break;
					}
				}
			}
			if (!found) {
				addChat(ChatEnum.ERROR, "Module " + args[1] + " was not found!");
				addChat(ChatEnum.ERROR, "[If the name contains a Space, type it like: \"killaura\" instead of \"kill aura\"]");
			}
			break;
		case 2:
			if (args[1].equalsIgnoreCase("list")) {
				for(Module m : ModuleManager.modules) {
					addChat(ChatEnum.NOTIFY, m.getName() + " | " + m.getKey());
				}
				break;
			}
			if (args[1].equalsIgnoreCase("special")) {
				try {
					String url = "http://minecraft.gamepedia.com/Key_Codes";
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
				} catch (IOException e) {
				}
				addChat(ChatEnum.NOTIFY, "Use the words that are in all caps. Like so: PERIOD, MINUS.");
				break;
			}

			addChat(ChatEnum.NOTIFY, getCommand() + " <modulename> <key>");
			break;
		case 1:
			addChat(ChatEnum.NOTIFY, getCommand() + " <modulename> <key>");
			addChat(ChatEnum.NOTIFY, getCommand() + " list");
			addChat(ChatEnum.NOTIFY, getCommand() + " special");
		}
	}

}
