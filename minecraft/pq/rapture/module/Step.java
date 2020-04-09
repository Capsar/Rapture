package pq.rapture.module;

import java.util.Arrays;

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
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.TimeHelper;

public class Step extends Module {

	public Step() {
		super(Protection.decrypt("1D2CB91019FB134D959D6D370D0235CD"), new String[] {}, Protection.decrypt("1A5D9D6AC3751EAF44740025650C963A"), Type.MOVEMENT, "NONE", 0xFFfccb63);
	}

	TimeHelper timeHelp = TimeHelper.getTimer();

	@ETarget(eventClasses = { EventBoundingBox.class })
	public void onEventBoundingBox(EventBoundingBox e) {
		if (e.getBoundingBox() == null) return;
		if (!getPlayer().onGround) return;
		if (e.getBlock() instanceof BlockStairs) {
			BlockStairs stairs = (BlockStairs) e.getBlock();
			if (MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY) == e.getY()
					&& String.valueOf(getPlayer().posY).contains(".5")) return;
		}
		if (MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY) - 0.01D >= e.getY()) return;
		if (MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY) != e.getY()) return;
		if (e.getBlock() == Blocks.air || e.getBlock() == Blocks.ladder || e.getBlock() == Blocks.vine || e.getBlock() instanceof BlockSlab)
			return;
		if (!getPlayer().isCollidedVertically) return;
		if (e.getBoundingBox().maxY - getPlayer().getEntityBoundingBox().minY < 0.6) return;

		if (!getGameSettings().keyBindJump.isKeyDown()) {
			getPlayer().stepHeight = 1.25F;
			Speed.slowdown();
			boolean hasLayerOnTop = GameUtil.getBlock(e.getBlockPos().add(0, 1, 0)) == Blocks.carpet;
			double offset = e.getBoundingBox().maxY + (hasLayerOnTop ? 0.1275: 0.065);
			e.setBoundingBox(new AxisAlignedBB(e.getBoundingBox().minX, e.getBoundingBox().minY, e.getBoundingBox().minZ, e
					.getBoundingBox().maxX, offset, e.getBoundingBox().maxZ));
			if(getPlayer().isInLava() || getPlayer().isInWater()) {
				GameUtil.offset(0, 0.5, 0);
				PacketUtil.sendPlayerPacket(true);
			}
			timeHelp.reset();
		} else {
			getPlayer().stepHeight = 0.6F;
		}
	}

	private boolean isOnSnow(EntityPlayerSP player) {
		boolean onSnow = false;
		int y = (int) player.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
		for(int x = MathHelper.floor_double(player.getEntityBoundingBox().minX); x < MathHelper
				.floor_double(player.getEntityBoundingBox().maxX) + 1; x++) {
			for(int z = MathHelper.floor_double(player.getEntityBoundingBox().minZ); z < MathHelper.floor_double(player
					.getEntityBoundingBox().maxZ) + 1; z++) {
				Block block = GameUtil.getBlock(x, y, z);
				if (block != null && block != Blocks.air) {
					if (block instanceof BlockSnow || block instanceof BlockSnowBlock) {
						onSnow = true;
					}
				}
			}
		}
		return onSnow;
	}

	@Override
	public boolean onDisable() {
		getPlayer().stepHeight = 0.6F;
		return super.onDisable();
	}

}
