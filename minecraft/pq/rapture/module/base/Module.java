package pq.rapture.module.base;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.rxdy.event.EventManager;

public class Module extends Wrapper {

	private String name = "mod";
	private String[] aliases = new String[] {};
	private String description = "desc of mod";
	private Type type = Type.CORE;
	private String key = "NONE";
	private int color = 0xFFFFFFFF;

	private boolean visible = true;
	private boolean state = false;

	public Module(String name, String[] aliases, String description, Type type, String key, int color) {
		this.name = name;
		this.aliases = aliases;
		this.description = description;
		this.type = type;
		this.key = key;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getAliases() {
		return aliases;
	}

	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void toggle(boolean safe) {
		if (safe) RaptureClient.safeClient();
		onToggle();
	}

	private void onToggle() {
		this.state = !this.state;
		if (state) {
			if (onEnable()) {
				EventManager.getInstance().registerAll(this);
			} else {
				this.state = false;
			}
		} else {
			if (onDisable()) {
				EventManager.getInstance().unregisterAll(this);
			} else {
				this.state = true;
			}
		}
	}

	public boolean onDisable() {
		return true;
	}

	public boolean onEnable() {
		return true;
	}

}
