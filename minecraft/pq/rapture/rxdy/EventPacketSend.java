package pq.rapture.rxdy;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import pq.rapture.Wrapper;
import pq.rapture.rxdy.event.Event;

public class EventPacketSend extends Event {

	private Packet packet;
	private NetworkManager manager;

	public EventPacketSend(Packet packet, NetworkManager manager) {
		this.packet = packet;
		this.manager = manager;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public NetworkManager getNm() {
		return manager;
	}
	
}
