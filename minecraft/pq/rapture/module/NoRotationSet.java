package pq.rapture.module;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.event.annotations.ETarget;

/**
 * Created by Haze on 6/16/2015. Project Rapture.
 */
public class NoRotationSet extends Module {

	public NoRotationSet() {
		super(Protection.decrypt("51A9F0D5C91A6CF371ED1ADCB682F81F"), new String[] { "nrs" }, Protection.decrypt("B27FACE05C7A839794D9FD2DAE679D2B9257C8E0D731120E49E829EEC328A3EFB275D699E39EAF56703E9B9EE8E3F838"), Type.COMBAT, "NONE", 0xFFe10fbc);
		setVisible(false);
	}

	@ETarget(eventClasses = EventPacketGet.class)
	public void getPacket(EventPacketGet eventPacketGet) {
		if (eventPacketGet.getPacket() instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook packetPlayerPosLook = (S08PacketPlayerPosLook) eventPacketGet.getPacket();
			packetPlayerPosLook.yaw = getPlayer().rotationYaw;
			packetPlayerPosLook.pitch = getPlayer().rotationPitch;
			eventPacketGet.setPacket(packetPlayerPosLook);
		}
	}

}
