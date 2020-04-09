package pq.rapture.module;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBoundingBox;
import pq.rapture.rxdy.EventEntityInsideOpaqueBlock;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventPushOutOfBlock;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;
import pq.rapture.util.PacketUtil;

public class Phase extends Module {

	private double mX, mZ;
	public boolean inBlock;
	private int state;
	private double oldY;

	public Phase() {
		super(Protection.decrypt("DABAAAA6034BC173B6E279714C35002F"), new String[] {}, Protection.decrypt("287809CEDD388E60DF902EDCDB296E7B3057DA112A17694C2EC51C97F3BEF28A"), Type.EXPLOITS, "NONE", 0xFF52b504);
	}

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventBoundingBox.class, EventPacketGet.class, EventEntityInsideOpaqueBlock.class,
			EventPushOutOfBlock.class })
	public void onEvent(EventPreMotion pre, EventBoundingBox bb, EventPacketGet get, EventEntityInsideOpaqueBlock eiob,
			EventPushOutOfBlock pushOut) {
		if (pre != null) {
			int posXi = MathHelper.floor_double(getPlayer().posX);
			int posYi = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY + 1);
			int posZi = MathHelper.floor_double(getPlayer().posZ);

			double x = 0, z = 0;
			double posX = getPlayer().posX;
			double posY = getPlayer().posY;
			double posZ = getPlayer().posZ;
			EnumFacing facing = getPlayer().getHorizontalFacing();

			double clip = 0.21D;
			mX = posX + facing.getFrontOffsetX() * clip;
			mZ = posZ + facing.getFrontOffsetZ() * clip;

			if (getPlayer().isCollidedHorizontally) {
				inBlock = true;
				state++;
				int time = 1;
				switch (state) {
				case 1:
					for(int i = 0; i < 25; i++)
						PacketUtil.addPlayerPacket(false);
					oldY = posY;
					getPlayer().setPosition(mX, posY, mZ);
					getPlayer().noClip = true;
					getPlayer().motionY = -0.062638D;
					break;
				case 3:
					for(int i = 0; i < 25; i++)
						PacketUtil.addPlayerPacket(false);
					getPlayer().setPosition(posX, oldY, posZ);
					getPlayer().noClip = false;
					state = 0;
					break;
				}
			}
		} else if (bb != null) {
			if (getPlayer().isCollidedHorizontally || GameUtil.isInsideBlock()) {
				inBlock = true;
				if (bb.getY() > getPlayer().getEntityBoundingBox().minY - 0.4 && bb.getY() < getPlayer().getEntityBoundingBox().maxY + 1) {
					bb.setBoundingBox(null);
				}
			} else {
				inBlock = false;
			}
		} else if (get != null) {
			if (get.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) get.getPacket();
				Entity var2 = getWorld().getEntityByID(packet.func_149412_c());
				if (var2 == getPlayer() && inBlock) {
					get.cancel();
				}
			}
		} else if (eiob != null) {
			eiob.cancel();
		} else if (pushOut != null) {
			pushOut.cancel();
		}
	}
}
