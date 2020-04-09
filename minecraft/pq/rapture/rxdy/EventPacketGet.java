package pq.rapture.rxdy;

import pq.rapture.rxdy.event.Event;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class EventPacketGet extends Event {

	private Packet packet;

	public EventPacketGet(Packet packet) {
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

}
