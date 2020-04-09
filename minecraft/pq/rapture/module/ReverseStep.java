package pq.rapture.module;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockHalfStoneSlab;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBoundingBox;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.TimeHelper;

public class ReverseStep extends Module {

	public ReverseStep() {
		super(Protection.decrypt("3523B1B848BF3BBD9303C14CE1DE0A0F"), new String[] {}, Protection.decrypt("6953FBDCE339276E056AD15D0CB87AFB4EBFF1F854CA5358860D067E345455550C945C05D5B2CDB56B03201D03CF0C0D"), Type.MOVEMENT, "NONE", 0xFFfccb63);
	}

	TimeHelper timeHelp = TimeHelper.getTimer();
	private boolean isInAirBecauseOfJump = false;

	// private double

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onPreMotion(EventPreMotion e) {
		if (getPlayer().motionY > -0.0784000015258789 && !getPlayer().onGround && !isInAirBecauseOfJump) {
			isInAirBecauseOfJump = true;
		} else if (getPlayer().onGround) {
			isInAirBecauseOfJump = false;
		}
		if (getPlayer().fallDistance < 1) {
			boolean goDownFast = false;
			for (int i = 0; i > -4; i--) {
				for (AxisAlignedBB bb : (List<AxisAlignedBB>) getWorld().getCollidingBoundingBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(0, i, 0))) {
					Block b = GameUtil.getBlock(bb.minX, bb.minY, bb.minZ);
					if (bb != null && b != Blocks.air && b != Blocks.web) {
						goDownFast = true;
					}
				}
			}
			if (goDownFast && !isInAirBecauseOfJump && !getPlayer().onGround) {
				getPlayer().motionY-= 1.5;
			}
		}
	}
}
