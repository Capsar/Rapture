package pq.rapture.module;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBoundingBox;
import pq.rapture.rxdy.EventEntityInsideOpaqueBlock;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventPushOutOfBlock;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;
import pq.rapture.util.TimeHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Haze on 6/17/2015. Project Rapture.
 */
public class SkipPhase extends Module {

	public SkipPhase() {
		super(Protection.decrypt("5E050EDF84B10CAB2A396DA31258A70D"), new String[] {}, Protection
				.decrypt("C5713E8D617E4BA47823946497AE79BC220CB949C5630414E5057CBA1AE5760C54E45D9A1C2C91BE75D5E3D35E1C0E38"),
				Type.EXPLOITS, "NONE", 0xFF1132cc);
	}

	public double vert, horiz;
	public TimeHelper time = new TimeHelper();
	public TimeHelper inBlockTime = new TimeHelper();

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventBoundingBox.class, EventEntityInsideOpaqueBlock.class, EventPushOutOfBlock.class })
	public void bb(EventBoundingBox bb, EventEntityInsideOpaqueBlock inBlock, EventPushOutOfBlock pushOut) {
		if (bb != null) {
			if (GameUtil.isInsideBlock()) {
				if (bb.getY() > getPlayer().getEntityBoundingBox().minY - 0.4 && bb.getY() < getPlayer().getEntityBoundingBox().maxY + 1
						&& inBlockTime.hasDelayRun(100)) {
					bb.setBoundingBox(null);
				}
			} else {
				inBlockTime.reset();
			}
		} else if (inBlock != null) {
			inBlock.cancel();
		} else if (pushOut != null) {
			pushOut.cancel();
		}

	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void pre(EventPreMotion motion) {
		if (getPlayer().isCollidedHorizontally && time.hasDelayRun(60) && !getPlayer().isOnLadder()) {
			float dir = getPlayer().rotationYaw;
			if (getPlayer().moveForward < 0) {
				dir += 180;
			}
			if (getPlayer().moveStrafing > 0) {
				dir -= 90 * (getPlayer().moveForward > 0 ? 0.5F : getPlayer().moveForward < 0 ? -0.5F : 1);
			} else if (getPlayer().moveStrafing < 0) {
				dir += 90 * (getPlayer().moveForward > 0 ? 0.5F : getPlayer().moveForward < 0 ? -0.5F : 1);
			}
			double xD = (float) Math.cos((dir + 90) * Math.PI / 180) * 0.209;
			double zD = (float) Math.sin((dir + 90) * Math.PI / 180) * 0.209;

			// double[] offY = new double[] { -0.02500000037252903,
			// -0.028571428997176036, -0.033333333830038704,
			// -0.04000000059604645,
			// -0.05000000074505806, -0.06666666766007741, -0.10000000149011612,
			// -0.20000000298023224, -0.04000000059604645,
			// -0.033333333830038704, -0.028571428997176036,
			// -0.02500000037252903 };
			double[] offY = new double[] { -0.025, -0.028, -0.033, -0.04, -0.1, -0.15, -0.2, - 0.15, -0.1, -0.4, -0.033, -0.028, -0.025 };

			for(int i = 0; i < offY.length; i++) {
				sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + offY[i], getPlayer().posZ,
						getPlayer().onGround));
				sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + (xD * i), getPlayer().posY, getPlayer().posZ
						+ (zD * i), getPlayer().onGround));
			}
		} else if (!getPlayer().isCollidedHorizontally) {
			time.reset();
		}
	}
}
