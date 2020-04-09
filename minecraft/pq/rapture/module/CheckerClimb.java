package pq.rapture.module;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBoundingBox;
import pq.rapture.rxdy.EventEntityInsideOpaqueBlock;
import pq.rapture.rxdy.EventJump;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventPushOutOfBlock;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.TimeHelper;

public class CheckerClimb extends Module {

	public CheckerClimb() {
		super(Protection.decrypt("31D21EAE0359B8D40AAB5BA81264373B"), new String[] {}, Protection.decrypt("0CF97197279715644E25E00CB372042CA5BA41F426F86B7F4F7CABB66F880A47B23544EE113F9A44D29AC72F7E7A4927"), Type.EXPLOITS, "NONE", 0xFF52b504);
	}

	private TimeHelper time = TimeHelper.getTimer();
	private boolean jumpCancel = true;

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventBoundingBox.class, EventPacketGet.class, EventEntityInsideOpaqueBlock.class, EventPushOutOfBlock.class, EventJump.class })
	public void onEvent(EventPreMotion pre, EventBoundingBox bb, EventPacketGet get, EventEntityInsideOpaqueBlock eiob, EventPushOutOfBlock pushOut, EventJump jump) {
		if (pre != null) {
			if (!getPlayer().isOnLadder() && !getPlayer().isInWater() && !getPlayer().isInLava()) {
				for (AxisAlignedBB a : (List<AxisAlignedBB>) getWorld().getCollidingBoundingBoxes(getPlayer(), getPlayer().getEntityBoundingBox().expand(0.001, 0, 0.001))) {
					Block b = GameUtil.getBlock(a.minX, a.minY, a.minZ);
					double x1 = a.maxX - getPlayer().getEntityBoundingBox().minX;
					double z1 = a.maxZ - getPlayer().getEntityBoundingBox().minZ;
					double x2 = a.minX - getPlayer().getEntityBoundingBox().maxX;
					double z2 = a.minZ - getPlayer().getEntityBoundingBox().maxZ;
					double mx = 0, mz = 0;

					if (x1 == 0 || x1 == 0.0625 || x1 == 0.125) {
						mx = -0.01;
					}
					if (z1 == 0 || z1 == 0.0625 || z1 == 0.125) {
						mz = -0.01;
					}
					if (x2 == 0 || x2 == 0.0625 || x2 == 0.125) {
						mx = 0.01;
					}
					if (z2 == 0 || z2 == 0.0625 || z2 == 0.125) {
						mz = 0.01;
					}

					if (b == Blocks.chest || b == Blocks.anvil || b == Blocks.ender_chest || b == Blocks.end_portal_frame || b == Blocks.enchanting_table || b == Blocks.hopper) {
						getPlayer().setJumping(false);
						jumpCancel = true;
						if (time.hasDelayRun(150) && getPlayer().onGround) {
							GameUtil.offset(mx, getPlayer().isSneaking() ? -0.9 : getGameSettings().keyBindJump.isKeyDown() ? 1.2 : 0, mz);
							getPlayer().motionY = getPlayer().isSneaking() ? -0.2 : getGameSettings().keyBindJump.isKeyDown() ? -0.15 : 0;
							time.reset();
							break;
						}
					} else {
						jumpCancel = false;
					}
				}
			}
		} else if (bb != null) {
			if (!getPlayer().isOnLadder() && !getPlayer().isInWater() && !getPlayer().isInLava() && bb.getBoundingBox() != null) {
				Block b = GameUtil.getBlock(bb.getBlockPos());
				double x1 = bb.getBoundingBox().maxX - getPlayer().getEntityBoundingBox().minX;
				double z1 = bb.getBoundingBox().maxZ - getPlayer().getEntityBoundingBox().minZ;
				double x2 = bb.getBoundingBox().minX - getPlayer().getEntityBoundingBox().maxX;
				double z2 = bb.getBoundingBox().minZ - getPlayer().getEntityBoundingBox().maxZ;
				double mx = 0, mz = 0;
				Block shouldbeair = GameUtil.getBlock(bb.getX(), bb.getY() + 1, bb.getZ());
				Block shouldbeair2 = GameUtil.getBlock(bb.getX(), bb.getY(), bb.getZ());

				Block xmin1 = GameUtil.getBlock(bb.getX() - 1, bb.getY(), bb.getZ());
				Block xplus1 = GameUtil.getBlock(bb.getX() + 1, bb.getY(), bb.getZ());
				Block zmin1 = GameUtil.getBlock(bb.getX(), bb.getY(), bb.getZ() - 1);
				Block zplus1 = GameUtil.getBlock(bb.getX(), bb.getY(), bb.getZ() + 1);

				boolean checker = false;

				if (zplus1 == Blocks.air && GameUtil.getBlock(bb.getX(), bb.getY() + 1, bb.getZ() + 1) != Blocks.air && shouldbeair == Blocks.air) {
					checker = true;
				} else if (zmin1 == Blocks.air && GameUtil.getBlock(bb.getX(), bb.getY() + 1, bb.getZ() - 1) != Blocks.air && shouldbeair == Blocks.air) {
					checker = true;
				} else if (xplus1 == Blocks.air && GameUtil.getBlock(bb.getX() + 1, bb.getY() + 1, bb.getZ()) != Blocks.air && shouldbeair == Blocks.air) {
					checker = true;
				} else if (xmin1 == Blocks.air && GameUtil.getBlock(bb.getX() - 1, bb.getY() + 1, bb.getZ()) != Blocks.air && shouldbeair == Blocks.air) {
					checker = true;
				}
				double clipSize = 0.01;

				if (x1 == 0 || x1 == 0.0625 || x1 == 0.125) {
					mx = -clipSize;
				}
				if (z1 == 0 || z1 == 0.0625 || z1 == 0.125) {
					mz = -clipSize;
				}
				if (x2 == 0 || x2 == 0.0625 || x2 == 0.125) {
					mx = clipSize;
				}
				if (z2 == 0 || z2 == 0.0625 || z2 == 0.125) {
					mz = clipSize;
				}

				if (((b == Blocks.stone_slab && bb.getBoundingBox().minY <= getPlayer().posY + 0.5) || checker) && !b.isPassable(getWorld(), bb.getBlockPos()) && b != Blocks.air
						&& !GameUtil.getBlock(bb.getX(), bb.getY() + 2, bb.getZ()).isPassable(getWorld(), bb.getBlockPos())) {
					getPlayer().setJumping(false);
					jumpCancel = true;
					GameUtil.offset(mx, 0, mz);
					if (time.hasDelayRun(222) && getPlayer().onGround) {
						GameUtil.offset(0, getPlayer().isSneaking() ? -0.8 : 0.9, 0);
						getPlayer().motionY = getPlayer().isSneaking() ? -0.2 : 0.229;
						time.reset();
					}
				} else {
					jumpCancel = false;
				}
				if (b == Blocks.stone_slab && bb.getBoundingBox().minY > getPlayer().posY + 1.5) {
					bb.setBoundingBox(null);
				}
			}
		} else if (get != null) {
			if (get.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) get.getPacket();
				Entity var2 = getWorld().getEntityByID(packet.func_149412_c());
				if (var2 == getPlayer() && GameUtil.isInsideBlock()) {
					get.cancel();
				}
			}
		} else if (eiob != null) {
			eiob.cancel();
		} else if (pushOut != null) {
//			pushOut.cancel();
		} else if (jump != null) {
			if (jumpCancel) jump.cancel();
		}
	}
}
