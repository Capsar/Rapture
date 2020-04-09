package pq.rapture.module;

import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.Inventory;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.TimeHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Haze on 6/28/2015.
 */
public class AutoFarm extends Module {

	private Comparator<BlockPos> blockComparator;
	private TimeHelper timer, bonemealTimer;
	private float oldHeadYaw, oldYaw, oldPitch;
	private BlockPos currentBreakBlock = null;

	public AutoFarm() {
		super(Protection.decrypt("49883C209627BC7804DF8E0FD15FE25C"), null, Protection.decrypt("BB626381FE923A32B983C05CE06E7D12"), Type.PLAYER, "NONE", 0xFFA281B1);
		blockComparator = (o1, o2) -> (int) getPlayer().getDistance(o1.getX() + 0.5, o1.getY() + 0.85, o1.getZ() + 0.5)
				- (int) getPlayer().getDistance(o2.getX() + 0.5, o2.getY() + 0.85, o2.getZ() + 0.5);
		timer = new TimeHelper();
		bonemealTimer = new TimeHelper();
	}

	public boolean isBreaking, isPlanting;

	private int helper = 0, helper2 = 0;

	@ETarget(eventClasses = EventPreMotion.class)
	public void onPre(EventPreMotion pre) {
		/* to do list */
		/*
		 * steps for autofarming 1. check inventory - done 2. check blocks
		 * around you - 3. check state of blocks above farmland 4. farm ->
		 * replant 5. if ^ is true loop
		 */
		if (getPlayer().inventory.getCurrentItem() == null) {
			Map<BlockPos, Block> blocksAroundYou = getBlocksInRadius(4.3);
			/* now we got all the blocks, check their state */
			Map<BlockPos, Block> crops = new TreeMap<>(blockComparator);
			for(Map.Entry<BlockPos, Block> entry : blocksAroundYou.entrySet()) {
				if (entry.getValue() instanceof BlockFarmland) {
					Block cropBlock = GameUtil.getBlock(entry.getKey().getX(), entry.getKey().getY() + 1, entry.getKey().getZ());
					if (cropBlock instanceof BlockCrops) {
						BlockCrops cropBlockX = (BlockCrops) cropBlock;
						if (cropBlockX.getMetaFromState(getWorld().getBlockState(entry.getKey().add(0, 1, 0))) == 7) {
							crops.put(new BlockPos(entry.getKey().getX(), entry.getKey().getY() + 1, entry.getKey().getZ()), cropBlock);
						}
					}
				}
			}

			for(Map.Entry<BlockPos, Block> entry : crops.entrySet()) {
				pre.cancel();
				hit(entry.getKey());
				break;
			}
		} else {
			if (getPlayer().inventory.getCurrentItem().getItem() instanceof ItemSeedFood
					|| getPlayer().inventory.getCurrentItem().getItem() instanceof ItemSeeds) {
				Map<BlockPos, Block> blocksAroundYou = getBlocksInRadius(4.3);
				/* now we got all the blocks, check their state */
				Map<BlockPos, Block> crops = new TreeMap<>(blockComparator);
				for(Map.Entry<BlockPos, Block> entry : blocksAroundYou.entrySet()) {
					if (entry.getValue() instanceof BlockFarmland) {
						Block airBlock = GameUtil.getBlock(entry.getKey().getX(), entry.getKey().getY() + 1, entry.getKey().getZ());
						if (airBlock == Blocks.air) {
							crops.put(new BlockPos(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()), airBlock);
						}
					}
				}

				for(Map.Entry<BlockPos, Block> entry : crops.entrySet()) {
					if (timer.hasDelayRun(56 + new Random().nextInt(10))) {
						pre.cancel();
						float[] rot = GameUtil.getAngles(entry.getKey().getX() + 0.5, entry.getKey().getY() + 0.85,
								entry.getKey().getZ() + 0.5, getPlayer());
						PacketUtil.sendLookPacket(rot[0], rot[1], getPlayer().onGround);
						interact(entry.getKey());
						getWorld().setBlockState(entry.getKey().add(0, 1, 0), Blocks.carrots.getDefaultState());
						timer.reset();
					}
					break;
				}
			} else if (getPlayer().getHeldItem().getItem() instanceof ItemDye && getPlayer().getHeldItem().getItemDamage() == 15) {
				Map<BlockPos, Block> blocksAroundYou = getBlocksInRadius(4);
				Map<BlockPos, Block> crops = new TreeMap<>(blockComparator);
				for(Map.Entry<BlockPos, Block> entry : blocksAroundYou.entrySet()) {
					Block cropBlock = GameUtil.getBlock(entry.getKey().getX(), entry.getKey().getY() + 1, entry.getKey().getZ());
					if (cropBlock instanceof BlockCrops
							&& cropBlock.getMetaFromState(getWorld().getBlockState(entry.getKey().add(0, 1, 0))) != 7) {
						crops.put(entry.getKey().add(0, 1, 0), cropBlock);
					}
				}

				for(Map.Entry<BlockPos, Block> entry : crops.entrySet()) {
					if (timer.hasDelayRun(120 + new Random().nextInt(60))) {
						pre.cancel();
						float[] rot = GameUtil.getAngles(entry.getKey().getX() + 0.5, entry.getKey().getY() + 0.85,
								entry.getKey().getZ() + 0.5, getPlayer());
						PacketUtil.sendLookPacket(rot[0], rot[1], getPlayer().onGround);
						for(int iz = 0; iz < 2; iz++)
							interact(entry.getKey());
						getWorld().setBlockState(entry.getKey(), entry.getValue().getStateFromMeta(7));
						timer.reset();
					}
					break;
				}
			}
		}
	}

	private void interact(BlockPos pos) {
		getPlayer().swingItem();
        /* move to block */
        sendPacket(new C08PacketPlayerBlockPlacement(pos, EnumFacing.UP.getIndex(), getPlayer().getHeldItem(), 0.5F, 0.25F, 0.5F));
	}

	private void hit(BlockPos key) {
		float[] rot = GameUtil.getAngles(key.getX() + 0.5, key.getY() + 0.1, key.getZ() + 0.5, getPlayer());
		PacketUtil.sendLookPacket(rot[0], rot[1], getPlayer().onGround);
		getPlayer().swingItem();
		sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, key, EnumFacing.UP));
		getPlayerController().onPlayerDestroyBlock(key, EnumFacing.UP);
	}

	public Map<BlockPos, Block> getBlocksInRadius(double radius) {
		/* loop through x coords */
		Map<BlockPos, Block> map = new HashMap<>();
		for(int x = -(int) radius; x < radius; x++) {
			for(int z = -(int) radius; z < radius; z++) {
				int posX = MathHelper.floor_double(getPlayer().posX) + x;
				int posY = MathHelper.floor_double(getPlayer().posY) - 1;
				int posZ = MathHelper.floor_double(getPlayer().posZ) + z;
				BlockPos pos = new BlockPos(posX, posY, posZ);
				if (Math.sqrt(getPlayer().getDistanceSqToCenter(pos)) > radius) continue;

				Block block = getWorld().getBlockState(pos).getBlock();
				map.put(pos, block);
			}

		}
		return map;
	}

}
