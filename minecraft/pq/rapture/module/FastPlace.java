package pq.rapture.module;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;

public class FastPlace extends Module {

	private boolean mobs;
	private boolean players;

	public FastPlace() {
		super(Protection.decrypt("698B7AB16A332D164DA3EEAD5F598F16"), new String[] { "" }, Protection.decrypt("877D29BD45EC327EC89BDA446DE9423F88D796185B81AF1A2B5F6BEED463E865"), Type.PLAYER, "NONE", 0xFFb9abe5);
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		getMinecraft().rightClickDelayTimer--;
	}
}
