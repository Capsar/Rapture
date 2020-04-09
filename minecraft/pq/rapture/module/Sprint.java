package pq.rapture.module;

import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;

public class Sprint extends Module {

	public Sprint() {
		super(Protection.decrypt("2F249F298F5866843319AD3D63E532CF"), new String[] {}, Protection.decrypt("93F4B71211C899851770A7FFBA9DC2068E88E457D3F613FC89FD5D57AF2EF977"), Type.MOVEMENT, "NONE", 0xFF00b159);
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void preMotion(EventPreMotion pre) {
		boolean canSprint = !getPlayer().isSneaking() && getPlayer().getFoodStats().getFoodLevel() > 6 && !getPlayer().isUsingItem() && !getPlayer().isOnLadder() && !getPlayer().isSprinting() && !getPlayer().isCollidedHorizontally;
		if(getPlayer().moveForward > 0 && canSprint) {
			getPlayer().setSprinting(true);
		}
	}

}
