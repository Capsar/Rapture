package pq.rapture.module;

import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBoundingBox;
import pq.rapture.rxdy.EventEntityInsideOpaqueBlock;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.EventManager;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;

public class NarniaPhase extends Module {

	public NarniaPhase() {
		super(Protection.decrypt("97BE646B9B7866156E9ADE3654D5004F"), new String[] {}, Protection
				.decrypt("CBA2280EF9432AA2CE3CDEBDC1023217BD1E2E5C7D37C7811E4A5AAF15B8450D5CDF270E0E99B5AE0AC1111E0B0CF10B"),
				Type.EXPLOITS, "NONE", 0xFFe3faae);
	}

	private int narniaPhase = 0, backWards = 0;
	boolean enableDisable = false;

	@Override
	public boolean onEnable() {
		narniaPhase = 5;
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		KeyBinding.setKeyBindState(getGameSettings().keyBindBack.getKeyCode(), false);
		return false;
	}

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPostMotion.class, EventBoundingBox.class, EventPacketGet.class, EventEntityInsideOpaqueBlock.class })
	public void sendPacket(EventPostMotion pre, EventBoundingBox bb, EventPacketGet get, EventEntityInsideOpaqueBlock inOpaque) {
		if (pre != null) {
			narniaPhase--;
			if (narniaPhase == 3) {
				KeyBinding.setKeyBindState(getGameSettings().keyBindForward.getKeyCode(), true);
			} else if (narniaPhase == -1) {
				KeyBinding.setKeyBindState(getGameSettings().keyBindForward.getKeyCode(), false);
			}

			if (narniaPhase > 0) {
				for(int i = 0; i < 2; i++) {
					sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY - 0.05, getPlayer().posZ,
							true));
					sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, true));
				}
				sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY - 0.05, getPlayer().posZ, true));
			} else if (narniaPhase < 0) {
				getPlayer().motionY *= 1.05;
				if (narniaPhase <= -40) {
					this.setState(false);
					EventManager.getInstance().unregisterAll(this);
				}
			}
		} else if (bb != null) {
			if (!getPlayer().isSneaking() && bb.getY() > getPlayer().getEntityBoundingBox().minY - 0.4
					&& bb.getY() < getPlayer().getEntityBoundingBox().maxY + 1) {
				bb.setBoundingBox(null);
			}
		} else if (get != null) {
			if (get.getPacket() instanceof S12PacketEntityVelocity) {
				if (((S12PacketEntityVelocity) get.getPacket()).func_149412_c() == getPlayer().getEntityId() && isInsideBlock()) {
					get.cancel();
				}
			}
		} else if (inOpaque != null) {
			inOpaque.cancel();
		}
	}

	private boolean isInsideBlock() {
		for(int x = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minX); x < MathHelper.floor_double(getPlayer()
				.getEntityBoundingBox().maxX) + 1; x++) {
			for(int y = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY); y < MathHelper.floor_double(getPlayer()
					.getEntityBoundingBox().maxY) + 1; y++) {
				for(int z = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minZ); z < MathHelper.floor_double(getPlayer()
						.getEntityBoundingBox().maxZ) + 1; z++) {
					final Block block = GameUtil.getBlock(x, y, z);
					if (block == null || block == Blocks.air) {
						continue;
					}

					final AxisAlignedBB boundingBox = block.getCollisionBoundingBox(getWorld(), new BlockPos(x, y, z), getWorld()
							.getBlockState(new BlockPos(x, y, z)));
					if (boundingBox != null && getPlayer().getEntityBoundingBox().intersectsWith(boundingBox)) return true;
				}
			}
		}
		return false;
	}

}
