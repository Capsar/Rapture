package pq.rapture.command.base;

import java.util.Arrays;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.rxdy.EventSendChatMessage;

public abstract class Command extends Wrapper {

	private String name;
	private String[] alias;
	private String desc;

	public Command(String[] strings, String desc) {
		this.name = Arrays.asList(strings).get(0);
		this.alias = strings;
		this.desc = desc;
	}

	public String[] getAlias() {
		return alias;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	protected abstract void onCommand(String[] args, String message);

	public boolean isAlias(String string) {
		for (String s : alias) {
			if (s.equalsIgnoreCase(string))
				return true;
		} 
		return false;
	}

	public String getCommand() {
		return RaptureClient.getPrefix() + getName().toLowerCase();
	}

}
