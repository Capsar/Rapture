package pq.rapture.module;

import net.minecraft.network.play.client.C0BPacketEntityAction;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;

public class Sneak extends Module {

	public Sneak() {
		super(Protection.decrypt("BBB309A6280B729478DBD96BB18DE083"), new String[] {}, Protection.decrypt("53AC0EC8A3D757AC6767D3FA8DC51238FD61A1C0A368D5E0A3C83325827F213B"), Type.MOVEMENT, "NONE", 0xFF7ede48);
	}
	
	@EventAllowance (allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventPostMotion.class })
	public void renderGUI(EventPreMotion pre, EventPostMotion post) {
		if (pre != null) {
			if (getGameSettings().keyBindSneak.isKeyDown())
				return;
			addPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SNEAKING));
			addPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SNEAKING));
		} else if (post != null) {
			if (getGameSettings().keyBindSneak.isKeyDown())
				return;
			addPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SNEAKING));
			addPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SNEAKING));
		}
	}

	@Override
	public boolean onDisable() {
		sendPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SNEAKING));
		return super.onDisable();
	}
	
}
