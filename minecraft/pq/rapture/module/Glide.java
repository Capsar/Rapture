package pq.rapture.module;

import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import org.lwjgl.input.Keyboard;

import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventMove;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.TimeHelper;

public class Glide extends Module {

	public Glide() {
		super(Protection.decrypt("FA52DA450E88D0002894A1105939FC48"), new String[] { "" }, Protection.decrypt("FCE91D9D1633DFFE026108A1A50A9CCD7AECCD3EA35C5F770C6EEEDE53691981"), Type.MOVEMENT, "NONE", 0xFFaf283a);
	}

	TimeHelper timeHelp = TimeHelper.getTimer();
	private float maxHeight = 0;

	@Override
	public boolean onEnable() {
		if (getPlayer() == null) return false;
		timeHelp.reset();

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
	public boolean onDisable() {
		getPlayer().motionY *= 0.2;
		return super.onDisable();
	}
	
	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY) 
	@ETarget(eventClasses = { EventMove.class, EventPacketGet.class })
	public void onEvent(EventMove move, EventPacketGet get) {
		if(get != null) {
			if(get.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) get.getPacket();
				if(getPlayer().getEntityId() == packet.func_149412_c()) {
					GameUtil.offset(0, 0.2, 0);
					maxHeight = (float) getPlayer().posY;
				}
			}
		} else if(move != null) {
			if (!getPlayer().capabilities.isCreativeMode) {
				if (!getPlayer().onGround && !getPlayer().isOnLadder()) {
					if (GameUtil.noMovingInput() && timeHelp.hasDelayRun(2000)) {
						move.setX(Math.sin(timeHelp.getCurrentTime() / 150) / 20);
						move.setZ(Math.cos(timeHelp.getCurrentTime() / 150) / 20);
					} else if (!GameUtil.noMovingInput()) timeHelp.reset();
					if (getGameSettings().keyBindSneak.isKeyDown()) {
						move.setY(-0.25D);
					} else if (getGameSettings().keyBindJump.isKeyDown() && getPlayer().posY < maxHeight - 0.1) {
						move.setY(0.25D);
					} else {
						move.multY(0.00000000000500000D);
					}
				}
			}
		}
	}

}
