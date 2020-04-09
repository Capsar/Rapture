package pq.rapture.module;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.PacketUtil;

public class Rewind extends Module {

	public boolean hasDamaged;
	private int color = 0xFF333333;
	private int tickDelay;

	public Rewind() {
		// off = 333333
		// on = 7fcc7f
		super("Rewind", new String[] {}, "Rewind your actions?", Type.EXPLOITS, "NONE", 0xFF333333);
	}

	@Override
	public boolean onEnable() {
		Module speed = getMod(Speed.class);
		if (speed != null && speed.getState()) {
			speed.toggle(true);
		}
		sendPacket(new C0BPacketEntityAction(getPlayer(), Action.START_SPRINTING));
		double[] d = { 0.2D, 0.26D };
		for (int a = 0; a < 69; a++) {
			for (int i = 0; i < d.length; i++) {
				PacketUtil.addPlayerOffsetPacket(0, d[i], 0, false);
			}
		}
		sendPacket(new C0BPacketEntityAction(getPlayer(), Action.STOP_SPRINTING));
		
		return super.onEnable();
	}

	@Override
	public int getColor() {
		return color;
	}

	@ETarget(eventClasses = EventPacketGet.class)
	public void onPacketGet(EventPacketGet eventPacketGet) {
		if (eventPacketGet.getPacket() instanceof S06PacketUpdateHealth) {
			float lastHealth = getPlayer().getHealth();
			if (((S06PacketUpdateHealth) eventPacketGet.getPacket()).getHealth() < lastHealth) {
				hasDamaged = true;
				color = 0xFF7fcc7f;
			}
		}
	}

	@ETarget(eventClasses = EventPreMotion.class)
	public void onPreMotion() {
		if (!hasDamaged) {
			sendPacket(new C0BPacketEntityAction(getPlayer(), Action.START_SPRINTING));
			double[] d = { 0.2D, 0.26D };
			for (int a = 0; a < 69; a++) {
				for (int i = 0; i < d.length; i++) {
					PacketUtil.addPlayerOffsetPacket(0, d[i], 0, false);
				}
			}
			sendPacket(new C0BPacketEntityAction(getPlayer(), Action.STOP_SPRINTING));

		}
		tickDelay++;
		if (tickDelay == 16) {
			getPlayer().setSprinting(false);
		} else {
			if (!hasDamaged) {
				boolean canSprint = !getPlayer().isSneaking() && getPlayer().getFoodStats().getFoodLevel() > 6 && !getPlayer().isUsingItem() && !getPlayer().isOnLadder() && !getPlayer().isSprinting();

				if (getPlayer().moveForward > 0 && canSprint) {
					getPlayer().setSprinting(true);
				}
			}
			if (tickDelay < 8) {
				PacketUtil.sendPlayerPacket(false);
			}
		}
		if (hasDamaged) {
			getPlayer().motionY = -1.3;
		}
	}

	@ETarget(eventClasses = EventPacketSend.class)
	public void onPacketSend(EventPacketSend event) {
		if (hasDamaged) {
			if ((event.getPacket() instanceof C03PacketPlayer) && tickDelay > 15) ((C03PacketPlayer) event.getPacket()).setY(((C03PacketPlayer) event.getPacket()).getY() + 0.07D);
		}
	}

	public boolean onDisable() {
		sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + Integer.MAX_VALUE, getPlayer().posZ, false));
		hasDamaged = false;
		color = 0xFF333333;
		tickDelay = 0;
		return super.onDisable();
	}

	public void damage() {
		double[] d = { 0.2D, 0.26D };
		for (int a = 0; a < 69; a++) {
			for (int i = 0; i < d.length; i++) {
				PacketUtil.addPlayerOffsetPacket(0, d[i], 0, false);
			}
		}
	}

}
