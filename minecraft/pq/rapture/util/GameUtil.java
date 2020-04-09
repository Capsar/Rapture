package pq.rapture.util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import pq.rapture.Wrapper;
import pq.rapture.module.FriendManager;
import pq.rapture.module.Jesus;

public class GameUtil extends Wrapper {

	public static int getItemID(Item item) {
		return Item.getIdFromItem(item);
	}

	public static int getBlockID(Block block) {
		return Block.getIdFromBlock(block);
	}

	public static void offset(double x, double y, double z) {
		Wrapper.getPlayer().setPosition(Wrapper.getPlayer().posX + x, Wrapper.getPlayer().posY + y, Wrapper.getPlayer().posZ + z);
	}

	public static void bbOffset(double x, double y, double z) {
		Wrapper.getPlayer().setEntityBoundingBox(Wrapper.getPlayer().getEntityBoundingBox().offset(x, y, z));
	}

	public static boolean isStandingStill() {
		if (getPlayer().motionX != 0) return false;
		if (getPlayer().motionY != -0.0784000015258789) return false;
		if (getPlayer().motionZ != 0) return false;
		return true;
	}

	public static Block getBlock(int x, int y, int z) {
		return getWorld().getChunkFromBlockCoords(new BlockPos(x, y, z)).getBlock(new BlockPos(x, y, z));
	}

	public static Block getBlock(BlockPos pos) {
		return getWorld().getChunkFromBlockCoords(pos).getBlock(pos);
	}

	public static Block getBlock(double x, double y, double z) {
		x = MathHelper.floor_double(x);
		y = MathHelper.floor_double(y);
		z = MathHelper.floor_double(z);
		return getWorld().getChunkFromBlockCoords(new BlockPos(x, y, z)).getBlock(new BlockPos(x, y, z));
	}

	public static Block getColliding(int radius) {
		if (!getMod(Jesus.class).getState()) { return null; }
		int mx = radius;
		int mz = radius;
		int max = radius;
		int maz = radius;

		if (getPlayer().motionX > 0.01) {
			mx = 0;
		} else if (getPlayer().motionX < -0.01) {
			max = 0;
		}
		if (getPlayer().motionZ > 0.01) {
			mz = 0;
		} else if (getPlayer().motionZ < -0.01) {
			maz = 0;
		}

		int xmin = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minX - mx);
		int ymin = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY - 1);
		int zmin = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minZ - mz);
		int xmax = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minX + max);
		int ymax = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY + 1);
		int zmax = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minZ + maz);
		for (int x = xmin; x <= xmax; ++x) {
			for (int y = ymin; y <= ymax; ++y) {
				for (int z = zmin; z <= zmax; ++z) {
					Block block = GameUtil.getBlock(x, y, z);
					if (block instanceof BlockLiquid && !getPlayer().isInsideOfMaterial(Material.lava) && !getPlayer().isInsideOfMaterial(Material.water)) { return block; }
				}
			}
		}
		return null;
	}

	public static boolean isInsideBlock() {
		for (int x = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minX); x < MathHelper.floor_double(getPlayer().getEntityBoundingBox().maxX) + 1; x++) {
			for (int y = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY); y < MathHelper.floor_double(getPlayer().getEntityBoundingBox().maxY) + 1; y++) {
				for (int z = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minZ); z < MathHelper.floor_double(getPlayer().getEntityBoundingBox().maxZ) + 1; z++) {
					final Block block = getBlock(x, y, z);
					if (block == null || block instanceof BlockAir || block instanceof BlockLiquid) {
						continue;
					}
					final AxisAlignedBB boundingBox = block.getCollisionBoundingBox(getWorld(), new BlockPos(x, y, z), getWorld().getBlockState(new BlockPos(x, y, z)));
					if (boundingBox != null && getPlayer().getEntityBoundingBox().intersectsWith(boundingBox)) return true;
				}
			}
		}
		return false;
	}

	public static Block getBlockAbovePlayer(double y) {
		int x = MathHelper.floor_double(getPlayer().posX);
		int yz = MathHelper.floor_double(getPlayer().posY + y);
		int z = MathHelper.floor_double(getPlayer().posZ);
		return getBlock(x, yz, z);
	}

	public static Block getBlockAbovePlayer() {
		for (int i = 0; i < 256; i++) {
			int x = MathHelper.floor_double(getPlayer().posX);
			int yz = MathHelper.floor_double(getPlayer().getEntityBoundingBox().maxY + i);
			int z = MathHelper.floor_double(getPlayer().posZ);
			if (getBlock(x, yz, z) != Blocks.air) { return getBlock(x, yz, z); }
		}
		return Blocks.air;
	}

	public static Block getBlockBelowPlayer() {
		for (int i = 0; i < 256; i++) {
			int x = MathHelper.floor_double(getPlayer().posX);
			int yz = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY - i);
			int z = MathHelper.floor_double(getPlayer().posZ);
			if (getBlock(x, yz, z) != Blocks.air) return getBlock(x, yz, z);
		}
		return Blocks.air;
	}

	public static Block getBlockBelowPlayer(double y) {
		int x = MathHelper.floor_double(getPlayer().posX);
		int yz = MathHelper.floor_double(getPlayer().getEntityBoundingBox().minY - y);
		int z = MathHelper.floor_double(getPlayer().posZ);
		return getBlock(x, yz, z);
	}

	public static int getBlockMetadata(int x, int y, int z) {
		return getWorld().getChunkFromBlockCoords(new BlockPos(x, y > 0 ? y : 1, z)).getBlockMetadata(new BlockPos(x, y > 0 ? y : 1, z));
	}

	public static boolean inLiquid() {
		if (getWorld().handleMaterialAcceleration(getPlayer().getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D), Material.water, getPlayer())
				|| getWorld().handleMaterialAcceleration(getPlayer().getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D), Material.lava, getPlayer()) || getPlayer().isInWater()) { return true; }
		return false;
	}

	public static boolean isAttackable(Entity e, double range, boolean team, boolean armorbreaker, int spawnDelay, boolean invisibles) {
		if (e instanceof EntityLivingBase) {
			EntityLivingBase base = (EntityLivingBase) e;
			//if invis is on, only attack if its not invisible
			boolean vis = invisibles ? !e.isInvisible() : true;
			boolean normal = vis && !base.isDead && base.getHealth() > 0 && getPlayer().getDistanceToEntity(e) < range && !base.getCommandSenderName().equals("") && base.ticksExisted > spawnDelay && getWorld().getEntityByID(base.getEntityId()) != null
					&& !e.getCommandSenderName().equals(getPlayer().getCommandSenderName());
			boolean hurtResist = e.hurtResistantTime <= ((EntityLivingBase) e).maxHurtResistantTime - 3;
			normal = armorbreaker ? normal : normal;
			if (e instanceof EntityPlayer) {
				if (team) {
					if (!((EntityPlayer) e).isOnSameTeam(getPlayer())) {
						EntityPlayer player = (EntityPlayer) e;
						if (!FriendManager.isFriend(player.getCommandSenderName()) && normal) {
							return true;
						}
					}
				} else {
					EntityPlayer player = (EntityPlayer) e;
					if (!FriendManager.isFriend(player.getCommandSenderName()) && normal) {
						return true;
					}
				}
			} else if (normal) { return true; }
		}
		return false;
	}

	public static float[] getAngles(Entity targetentity, Entity forentity) {
		double x = targetentity.posX - forentity.posX;
		double z = targetentity.posZ - forentity.posZ;
		double y = (targetentity.getEntityBoundingBox().maxY - 0.4) - (forentity.getEntityBoundingBox().maxY - 0.4);
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
		return (new float[] { yaw, pitch });
	}

	public static float[] getAngles(double bx, double by, double bz, Entity forentity) {
		double x = bx - forentity.posX;
		double y = by - (forentity.getEntityBoundingBox().maxY - 0.4);
		double z = bz - forentity.posZ;
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
		return (new float[] { yaw, pitch });
	}

	public static float getDistanceBetweenAngle(float clientyaw, float otheryaw) {
		float angle = Math.abs(otheryaw - clientyaw) % 360.0F;
		if (angle > 180.0F) {
			angle = 360.0F - angle;
		}
		return Math.abs(angle);
	}

	public static float[] getYawAndPitch(Entity paramEntityPlayer) {
		double d1 = paramEntityPlayer.posX - getPlayer().posX;
		double d2 = paramEntityPlayer.posZ - getPlayer().posZ;
		double d3 = getPlayer().posY + 0.12D - (paramEntityPlayer.posY + 1.82D);
		double d4 = MathHelper.sqrt_double(d1 + d2 + d3);
		float f1 = (float) (Math.atan2(d2, d1) * 180.0D / Math.PI) - 90.0F;
		float f2 = (float) (Math.atan2(d3, d4) * 180.0D / Math.PI);

		return new float[] { f1, f2 };
	}

	public static boolean entityInFOV(Entity e, double fov) {
		float[] arrayOfFloat = getYawAndPitch(e);
		double d2g = getDistanceBetweenAngle(arrayOfFloat[0], getPlayer().rotationYaw);
		if (d2g <= fov) { return true; }
		return false;
	}

	@SuppressWarnings("unchecked")
	public static List<EntityLivingBase> entitiesInFov(float fov, double range, boolean invisibles) {
		return (List<EntityLivingBase>) getWorld().loadedEntityList.stream().filter(xEnd -> xEnd instanceof EntityLivingBase).filter(ent -> isAttackable((EntityLivingBase) ent, fov, true, false, 0, invisibles)).filter(ent -> entityInFOV((EntityLivingBase) ent, fov))
				.collect(Collectors.toList());
	}

	public static EntityLivingBase getTargetFromList(List<EntityLivingBase> entitiesInRange) {
		if (entitiesInRange == null || entitiesInRange.size() == 0) return null;
		double closestRange = 0.0F, farthestRange = 0.0F;

		for (EntityLivingBase ent : entitiesInRange) {
			double range = getPlayer().getDistanceSqToEntity(ent);
			if (range < closestRange) closestRange = range;
			if (range > farthestRange) farthestRange = range;
		}
		double middleDistanceRange = farthestRange - closestRange;
		double middlesEntityRange = farthestRange;

		for (EntityLivingBase entityLivingBase : entitiesInRange) {
			double range = getPlayer().getDistanceToEntity(entityLivingBase);
			if (middleDistanceRange - range <= 0) middlesEntityRange = range;
		}

		EntityLivingBase middleEntity = null;

		for (EntityLivingBase e : entitiesInRange) {
			double range = getPlayer().getDistanceToEntity(e);
			if (middlesEntityRange - range < middleDistanceRange) middleEntity = e;
		}
		return middleEntity;
	}

	public static EntityLivingBase getClosestEntityToCursor(CopyOnWriteArrayList<EntityLivingBase> targets, float syaw, int killaurapov, boolean normalRotation) {
		double distance = 180;
		float tempYaw = 180;
		double range = 6;
		EntityLivingBase tempEntity = null;

		ArrayList<EntityLivingBase> yawList = new ArrayList<EntityLivingBase>();
		for (EntityLivingBase living : targets) {
			final float[] angles = getAngles(living, Wrapper.getPlayer());
			final float yawDist = getDistanceBetweenAngle(syaw, angles[0]);
			final float yawNormalDist = getDistanceBetweenAngle(getPlayer().rotationYaw, angles[0]);
			if ((normalRotation ? yawNormalDist : yawDist) > killaurapov) {
				continue;
			}
			if (yawDist < distance) {
				distance = yawDist;
				tempYaw = angles[0];
			}
		}

		for (EntityLivingBase living : targets) {
			final float[] angles = getAngles(living, Wrapper.getPlayer());
			final float yawDist = getDistanceBetweenAngle(tempYaw, angles[0]);
			if (yawDist < 10) {
				yawList.add(living);
			}
		}

		for (EntityLivingBase living : yawList) {
			final double curRange = getPlayer().getDistanceToEntity(living);
			if (curRange < range) {
				range = curRange;
				tempEntity = living;
			}
		}

		return tempEntity;
	}

	public static float updateRotation(float currentYaw, float targetYaw, float maxRotation) {
		float diff = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw);
		if (diff > maxRotation) {
			diff = maxRotation;
		}
		if (diff < -maxRotation) {
			diff = -maxRotation;
		}
		return currentYaw + diff;
	}

	public static void smoothAim(EntityLivingBase e, int rate, boolean alsoPitch) {
		double xDist = e.posX - getPlayer().posX;
		double yDist = (e.posY + e.getEyeHeight() + 0.2) - getPlayer().posY;
		double zDist = e.posZ - getPlayer().posZ;
		double dist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);

		float yaw = (float) (Math.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(yDist, dist) * 180.0D / Math.PI);
		if (alsoPitch) getPlayer().rotationPitch = updateRotation(getPlayer().rotationPitch, pitch, rate);
		getPlayer().rotationYaw = updateRotation(getPlayer().rotationYaw, yaw, rate);

	}

	public static void loadRenderers() {
		getWorld().markBlockRangeForRenderUpdate(getPlayer().getPosition().add(-100, -100, -100), getPlayer().getPosition().add(100, 100, 100));

	}

	public static boolean noMovingInput() {
		return getPlayer().moveForward == 0 && getPlayer().moveStrafing == 0 && !getPlayer().movementInput.jump;
	}

	public static Entity getEntityOnMouseCurser(double range) {
		Entity tempEntity = null;
		MovingObjectPosition object = getPlayer().rayTrace(range, getMinecraft().timer.renderPartialTicks);
		double tempRange = range;
		Vec3 var6 = getPlayer().getPositionEyes(getMinecraft().timer.renderPartialTicks);

		if (object != null) {
			tempRange = object.hitVec.distanceTo(var6);
		}

		Vec3 playerVec = getPlayer().getLook(getMinecraft().timer.renderPartialTicks);
		Vec3 extendedVec = var6.addVector(playerVec.xCoord * range, playerVec.yCoord * range, playerVec.zCoord * range);
		tempEntity = null;
		Vec3 var9 = null;
		float var10 = 1.0F;
		List var11 = Wrapper.getWorld().getEntitiesWithinAABBExcludingEntity(getPlayer(), getPlayer().getEntityBoundingBox().addCoord(playerVec.xCoord * range, playerVec.yCoord * range, playerVec.zCoord * range).expand(var10, var10, var10));
		double var12 = tempRange;

		for (int var14 = 0; var14 < var11.size(); ++var14) {
			Entity var15 = (Entity) var11.get(var14);

			if (var15.canBeCollidedWith()) {
				float var16 = var15.getCollisionBorderSize();
				AxisAlignedBB var17 = var15.getEntityBoundingBox().expand(var16, var16, var16);
				MovingObjectPosition var18 = var17.calculateIntercept(var6, extendedVec);

				if (var17.isVecInside(var6)) {
					if (0.0D < var12 || var12 == 0.0D) {
						tempEntity = var15;
						var9 = var18 == null ? var6 : var18.hitVec;
						var12 = 0.0D;
					}
				} else if (var18 != null) {
					double var19 = var6.distanceTo(var18.hitVec);

					if (var19 < var12 || var12 == 0.0D) {
						if (var15 == getPlayer().ridingEntity) {
							if (var12 == 0.0D) {
								tempEntity = var15;
								var9 = var18.hitVec;
							}
						} else {
							tempEntity = var15;
							var9 = var18.hitVec;
							var12 = var19;
						}
					}
				}
			}
		}

		return tempEntity;
	}

	public static void attackEffectOnEntity(EntityLivingBase livingbase) {
		if (livingbase.canAttackWithItem()) {
			if (!livingbase.hitByEntity(getPlayer())) {
				float attackDamage = (float) getPlayer().getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
				int sprinterInt = 0;
				float damageInt = 0.0F;

				if (livingbase instanceof EntityLivingBase) {
					damageInt = EnchantmentHelper.func_152377_a(getPlayer().getHeldItem(), livingbase.getCreatureAttribute());
					sprinterInt += EnchantmentHelper.getKnockbackModifier(livingbase);
				}

				if (getPlayer().isSprinting()) {
					++sprinterInt;
				}

				if (attackDamage > 0.0F || damageInt > 0.0F) {
					boolean canCrit = getPlayer().fallDistance > 0.0F && !getPlayer().onGround && !getPlayer().isOnLadder() && !getPlayer().isInWater() && !getPlayer().isPotionActive(Potion.blindness) && getPlayer().ridingEntity == null
							&& livingbase instanceof EntityLivingBase;
					if (canCrit) {
						getPlayer().onCriticalHit(livingbase);
					}

					if (damageInt > 0.0F) {
						getPlayer().onEnchantmentCritical(livingbase);
					}

					if (livingbase instanceof EntityLivingBase) {
						getPlayer().addStat(StatList.damageDealtStat, Math.round(attackDamage * 10.0F));
					}
				}
			}
		}
	}

	public static float getArmorDamage(ItemStack mainItem) {
		try {
			ItemSword swordItem = (ItemSword) mainItem.getItem();
			float enchantmentDamage = EnchantmentHelper.func_152377_a(mainItem, getPlayer().getCreatureAttribute());
			float totalDamage = 1 + (swordItem.getDamageVsEntity() * 2) + enchantmentDamage;
			return totalDamage;
		} catch (Exception e) {
			return 1;
		}
	}
}
