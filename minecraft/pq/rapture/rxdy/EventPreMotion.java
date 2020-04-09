package pq.rapture.rxdy;

import pq.rapture.rxdy.event.Event;


public class EventPreMotion extends Event {
	
	private float yaw;
	private float pitch;

	public EventPreMotion(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
}
