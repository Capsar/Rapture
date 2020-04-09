package pq.rapture.module;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;

public class NoSlowDown extends Module {

	private boolean justBlocked;

	public NoSlowDown() {
		super(Protection.decrypt("1EF4E533A5C5CD08D3CBAB22883A4B5E"), new String[] { "" }, Protection.decrypt("E849ADDC8B71C2571732B076A08BB1590410FE52C9A15DCFBA44092C6B745A57"), Type.COMBAT, "NONE", 0xFFdd3e89);
	}

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPacketSend.class, EventPostMotion.class })
	public void onEvent(EventPacketSend send, EventPostMotion post) {
		boolean moving = !GameUtil.isStandingStill();
		if (send != null) {
			if(!getPlayer().isSprinting() && getPlayer().moveForward > 0 && (getMod(Sprint.class).getState() || getGameSettings().keyBindSprint.isKeyDown())
					&& getPlayer().isUsingItem() && !getPlayer().isSneaking())
				getPlayer().setSprinting(true);
			
			if (getPlayer().getFoodStats().getFoodLevel() >= 6 && !getPlayer().isOnLadder() && !getMod(Freecam.class).getState() && moving
					&& getPlayer().isBlocking() && justBlocked && send.getPacket() instanceof C03PacketPlayer) {
				sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0),
						EnumFacing.DOWN));
				justBlocked = false;
			}
		} else if (post != null) {
			if (getPlayer().getFoodStats().getFoodLevel() >= 6 && !getPlayer().isOnLadder() && !getMod(Freecam.class).getState() && moving
					&& getPlayer().isBlocking()) {
				addPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
				justBlocked = true;
			}
		}
	}

}
