package pq.rapture.command;

import pq.rapture.command.base.Command;
import pq.rapture.command.base.CommandManager;

public class Help extends Command {

	public Help() {
		super(new String[] {"help", "h", "?"}, "Show all commands with descriptions.");
	}

	@Override
	protected void onCommand(String[] args, String message) {
		getCommandManager();
		for(Command c : CommandManager.commands) {
			addChat(ChatEnum.NOTIFY, c.getCommand() + " | " + c.getDesc());
		}
	}

}
