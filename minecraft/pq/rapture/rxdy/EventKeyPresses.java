package pq.rapture.rxdy;

import pq.rapture.rxdy.event.Event;
import net.minecraft.client.settings.KeyBinding;

public class EventKeyPresses extends Event {

	private State state;
	private int keyCode;
	private boolean pressed;

	public static enum State {
		WHILE, ONPRESS, UNPRESS;
	}

	public EventKeyPresses(int keyCode, boolean pressed, State state) {
		this.keyCode = keyCode;
		this.pressed = pressed;
		this.state = state;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public int getKeyCode() {
		return keyCode;
	}

	public State getState() {
		return state;
	}
}
