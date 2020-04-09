package pq.rapture.rxdy;

import pq.rapture.rxdy.event.Event;
import net.minecraft.entity.Entity;

public class EventMove extends Event {

	public EventMove(double x, double y, double z, Entity e) {
		this.z = z;
		this.x = x;
		this.y = y;
	}

	private double x;
	private double y;
	private double z;

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void multY(double d) {
		this.y *= d;
	}
}
