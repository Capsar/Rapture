package pq.rapture.command;

import pq.rapture.RaptureClient;
import pq.rapture.command.base.Command;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;

/**
 * Created by Haze on 7/5/2015.
 */
public class Visibility extends Command {

	public Visibility() {
		super(new String[] { "vis", "v" }, "Change the visbility of mods.");
	}

	@Override
	protected void onCommand(String[] args, String message) {
		switch (args.length) {
		case 2:
			if (args[1].equalsIgnoreCase("list")) {
				for(Module m : ModuleManager.modules) {
					addChat(ChatEnum.NOTIFY, m.getName() + " | " + m.isVisible());
				}
				break;
			} else {
				boolean found = false;
				for(Module m : ModuleManager.modules) {
					String modulename = m.getName().replace(" ", "_");
					if (args[1].equalsIgnoreCase(modulename)) {
						found = true;
						m.setVisible(!m.isVisible());
						if (m.isVisible()) {
							addChat(ChatEnum.NOTIFY, m.getName() + " will now be shown in the arraylist.");
						} else {
							addChat(ChatEnum.NOTIFY, m.getName() + " will no longer be shown in the arraylist.");
						}
						RaptureClient.safeClient();
						break;
					}
				}
				if (!found) {
					addChat(ChatEnum.ERROR, "Module " + args[1] + " was not found!");
				}
			}
			break;
		case 1:
			addChat(ChatEnum.NOTIFY, getCommand() + " <name>");
			addChat(ChatEnum.NOTIFY, getCommand() + " list");
		}
	}

}
