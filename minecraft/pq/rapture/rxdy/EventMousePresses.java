package pq.rapture.rxdy;

import pq.rapture.rxdy.event.Event;
import net.minecraft.client.settings.KeyBinding;

public class EventMousePresses extends Event {

	private State state;
	private int mouseCode;
	private boolean pressed;

	public static enum State {
		ONPRESS, UNPRESS;
	}

	public EventMousePresses(int mouseCode, boolean pressed, State state) {
		this.mouseCode = mouseCode;
		this.pressed = pressed;
		this.state = state;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public int getMouseCode() {
		return mouseCode;
	}

	public State getState() {
		return state;
	}
}
