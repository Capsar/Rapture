package pq.rapture.module;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class KillAura extends Module implements HasValues {

	private final static String REACH = Protection.decrypt("BE1C56CB10A6E7595EC3A68EA635EBE9");
	private final static String ATTACKS_PER_SECOND = Protection.decrypt("FAE395C4231DD1E228F78C89878EA583216DB67040030EFADB829AA282B368E3");
	private static final String KILL_POV = Protection.decrypt("55473AA9335AA24679A6FCEE52EB6CB9");
	private final static String RANDOMDELAY = Protection.decrypt("A354634222BF6665159E01D937937429");
	private final static String MOBS = Protection.decrypt("F5DC0D10C18E98704745E8DFF09A959A");
	private final static String PLAYERS = Protection.decrypt("94C60094F80889AA27229C267D201C40");
	private final static String ARMORBREAKER = Protection.decrypt("11F3E0FDE926112436699CC7279C0606");
	private final static String SPAWNDELAY = Protection.decrypt("3C5BE81AFAD5AA55849646B96BEFBA62");
	private final static String INVISIBLES = "Invisible Entities";
	private final static String ENTITIES_IN_ROUND = "Entities In Round";
	private static final Value[] PARAMETERS = new Value[]{new Value(REACH, 3.0, 6.0, 0.1F), new Value(ATTACKS_PER_SECOND, 1, 20, 1), new Value(KILL_POV, 20, 180, 5), new Value(RANDOMDELAY, 20, 200, 5), new Value(SPAWNDELAY, 0, 30, 2),
			new Value(ENTITIES_IN_ROUND, 1, 6, 1), new Value(INVISIBLES, true, false), new Value(MOBS, false, true), new Value(PLAYERS, false, true), new Value(ARMORBREAKER, false, true)};
	private static EntityLivingBase target;
	public CopyOnWriteArrayList<EntityLivingBase> targets = new CopyOnWriteArrayList<EntityLivingBase>();
	TimeHelper timeHelp = TimeHelper.getTimer();
	TimeHelper lookedAtMobTime = TimeHelper.getTimer();
	private boolean players = true;
	private boolean mobs = false;
	private double reach = 3.8;
	private int aps = 10;
	private int killaurapov = 180;
	private int randomDelay = 60;
	private int spawnDelay = 10;
	private boolean invisibles = false;
	private int entitiesInRound = 1;
	private boolean armorbreaker = false;
	private boolean justBlocked = false;
	private float spitch, syaw;

	public KillAura() {
		super(Protection.decrypt("9B540A40DE0889458444AF639E7F9C81"), new String[]{}, Protection.decrypt("4778F2437433AEA738B435692441D8A9E064CC030D75A6EFD1ECCC2EE0B5EEC2535585213BD26F116995BFDEBF4B6E61"), Type.COMBAT, "R", 0xFFd11141);
	}

	public static EntityLivingBase getTarget() {
		return target;
	}

	@Override
	public boolean onDisable() {
		this.targets.clear();
		return super.onDisable();
	}

	@ETarget(eventClasses = EventRenderGlobal.class)
	public void onRender(EventRenderGlobal eventRenderGlobal) {
		if (target != null) {
			double eposX = target.lastTickPosX + (target.posX - target.lastTickPosX) * (double) getMinecraft().timer.renderPartialTicks;
			double eposY = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) getMinecraft().timer.renderPartialTicks;
			double eposZ = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * (double) getMinecraft().timer.renderPartialTicks;
			double x = eposX - getRenderManager().viewerPosX;
			double y = eposY - getRenderManager().viewerPosY;
			double z = eposZ - getRenderManager().viewerPosZ;

			GL11.glPushMatrix();
			RenderUtil.preRender();
			GL11.glLineWidth(2F);
			GL11.glColor4d(0.4, 0.2, 0.950, 0.75);
			GL11.glBegin(GL11.GL_LINES);

			GL11.glVertex3d(x - 0.5, y, z - 0.5);
			GL11.glVertex3d(x - 0.5, y, z + 0.5);

			GL11.glVertex3d(x + 0.5, y, z + 0.5);
			GL11.glVertex3d(x + 0.5, y, z - 0.5);

			GL11.glVertex3d(x + 0.5, y, z + 0.5);
			GL11.glVertex3d(x - 0.5, y, z + 0.5);

			GL11.glVertex3d(x + 0.5, y, z - 0.5);
			GL11.glVertex3d(x - 0.5, y, z - 0.5);

			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);

			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);

			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);

			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);

			GL11.glEnd();

			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glColor4d(0.4, 0.2, 0.9, 0.25);
			GL11.glBegin(GL11.GL_QUADS);

			GL11.glVertex3d(x - 0.5, y, z - 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x + 0.5, y, z - 0.5);

			GL11.glVertex3d(x - 0.5, y, z + 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x + 0.5, y, z + 0.5);

			GL11.glVertex3d(x + 0.5, y, z - 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x + 0.5, y, z + 0.5);

			GL11.glVertex3d(x - 0.5, y, z - 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x - 0.5, y, z + 0.5);

			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x + 0.5, y + target.getEyeHeight() + 0.35, z + 0.5);
			GL11.glVertex3d(x - 0.5, y + target.getEyeHeight() + 0.35, z - 0.5);

			GL11.glVertex3d(x + 0.5, y, z - 0.5);
			GL11.glVertex3d(x + 0.5, y, z + 0.5);
			GL11.glVertex3d(x - 0.5, y, z + 0.5);
			GL11.glVertex3d(x - 0.5, y, z - 0.5);

			GL11.glEnd();

			GL11.glEnable(GL11.GL_CULL_FACE);

			RenderUtil.postRender();
			GL11.glPopMatrix();
		}
	}

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventPostMotion.class })
	public void onEvent(EventPreMotion pre, EventPostMotion post) {
		if (pre != null) {
			if (!GameUtil.isStandingStill() && justBlocked) {
				sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
				justBlocked = false;
			}

			boolean domobs = true;
			if (players) {
				if (targets.isEmpty()) {
					for (Entity e : (List<Entity>) getWorld().loadedEntityList) {
						if (!(e instanceof EntityPlayer) || !GameUtil.isAttackable(e, reach, getMod(FriendManager.class).teams, armorbreaker, spawnDelay, invisibles) || targets.contains(e)) {
							continue;
						}
						targets.add((EntityLivingBase) e);
						domobs = false;
					}
				}
			}

			if (mobs && domobs) {
				if (targets.isEmpty()) {
					for (Entity e : (List<Entity>) getWorld().loadedEntityList) {
						if (e instanceof EntityPlayer || !(e instanceof EntityLivingBase) || !GameUtil.isAttackable(e, reach, getMod(FriendManager.class).teams, armorbreaker, spawnDelay, invisibles) || targets.contains(e)) {
							continue;
						}
						targets.add((EntityLivingBase) e);
					}
				}
			}

			if (!targets.isEmpty()) {
				for (EntityLivingBase e : targets) {
					if (!GameUtil.isAttackable(e, reach, getMod(FriendManager.class).teams, armorbreaker, spawnDelay, invisibles)) targets.remove(e);
				}

				targets.sort((o1, o2) -> getEntityType(o1).compareTo(getEntityType(o2)));

				for (int i = 0; i < targets.size(); i++) {
					target = GameUtil.getClosestEntityToCursor(targets, syaw, killaurapov, true);
					if (GameUtil.isAttackable(target, reach, getMod(FriendManager.class).teams, armorbreaker, spawnDelay, invisibles)) {
						break;
					}
					targets.remove(target);
				}
				if (target == null) return;

				float change[] = GameUtil.getAngles(target, getPlayer());
				float randomYaw = (float) (Math.sin(new Random().nextDouble()) * 5);
				float randomPitch = (float) (Math.sin(new Random().nextDouble()) * 10);
				this.syaw = GameUtil.updateRotation(this.syaw, change[0], 25 + new Random().nextInt(10));
				this.spitch = GameUtil.updateRotation(this.spitch, change[1], 10 + new Random().nextInt(10));
				if (!AutoPot.isHealing) {
					PacketUtil.addPlayerLookPacket(getPlayer().posX, getPlayer().posY, getPlayer().posZ, this.syaw, this.spitch, getPlayer().onGround);
					pre.cancel();
				}
				if (GameUtil.getDistanceBetweenAngle(this.syaw, change[0]) < new Random().nextInt(5) && target != null && aps != 0 && timeHelp.hasDelayRun(320 / (aps)) && !AutoPot.isHealing && lookedAtMobTime.hasDelayRun(new Random().nextInt(5))) {
					if (target instanceof EntityPlayer) {
						EntityPlayer playerTarget = (EntityPlayer) target;
						ItemStack[] armors = playerTarget.inventory.armorInventory;
						boolean hasArmor = false;
						for (int i = 0; i < 4; i++)
							if (armors[i] != null) hasArmor = true;
						if (hasArmor && armorbreaker) doArmorBreaker();
						else {
							attack(target);
							for (int x = 0; x < entitiesInRound - 1; x++) {
								for (int i = 0; i < targets.size(); i++) {
									target = GameUtil.getClosestEntityToCursor(targets, syaw, 50, false);
									if (GameUtil.isAttackable(target, reach, getMod(FriendManager.class).teams, armorbreaker, spawnDelay, invisibles)) {
										attack(target);
										break;
									}
									targets.remove(target);
								}
							}
						}
					} else if (!(target instanceof EntityPlayer)) {
						attack(target);
						for (int x = 0; x < entitiesInRound - 1; x++) {
							for (int i = 0; i < targets.size(); i++) {
								target = GameUtil.getClosestEntityToCursor(targets, syaw, 50, false);
								if (GameUtil.isAttackable(target, reach, getMod(FriendManager.class).teams, armorbreaker, spawnDelay, invisibles)) {
									attack(target);
									break;
								}
								targets.remove(target);
							}
						}
					}

					if (!GameUtil.isStandingStill() && getPlayer().isSprinting()) {
						sendPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SPRINTING));
					} else {
						sendPacket(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SPRINTING));
					}
				} else if (GameUtil.getDistanceBetweenAngle(this.syaw, change[0]) > 5) {
					lookedAtMobTime.reset();
				}
			} else {
				this.syaw = getPlayer().rotationYaw;
				this.spitch = getPlayer().rotationPitch;
				target = null;
			}
		} else if (post != null) {
			if (getPlayer().getHeldItem() != null && getPlayer().getHeldItem().getItem() instanceof ItemSword && getPlayer().isBlocking() && !GameUtil.isStandingStill()) {
				sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
				justBlocked = true;
			}
		}
	}

	private void doArmorBreaker() {
		if (getPlayer().inventory.getItemStack() == null) {
			ItemStack firstItem = getPlayer().getHeldItem();
			float beginningDamage = firstItem == null ? 1 : (firstItem.getItem() instanceof ItemSword) ? GameUtil.getArmorDamage(firstItem) : 1;
			int worstSlot = 0;
			Map<ItemStack, Integer> betterSwords = new TreeMap<>(new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					if (GameUtil.getArmorDamage(o1) < GameUtil.getArmorDamage(o2)) return -1;
					else return 1;
				}
			});
			for (int o = 9; o <= 35; o++) {
				if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
					ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
					if (item != null) {
						betterSwords.put(item, o);
					}
				}
			}
			for (Map.Entry<ItemStack, Integer> entry : betterSwords.entrySet()) {
				Inventory.clickSlot(entry.getValue(), 0, 0);
				break;
			}
		}

		Inventory.clickSlot(getPlayer().inventory.currentItem + 36, 1, 0);
		attack(target);
	}

	private void attack(EntityLivingBase e) {
		if (GameUtil.isStandingStill() && justBlocked) {
			sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
			justBlocked = false;
		}
		Double x = null, y = null, z = null;
		Boolean onGround = null;
/*		if(getPlayer().getDistanceToEntity(e) > 3.9) {
			x = getPlayer().posX;
			y = getPlayer().posY;
			z = getPlayer().posZ;
			onGround = getPlayer().onGround;
			float  direction = getPlayer().rotationYaw +
					(getPlayer().moveForward < 0 ? 180 : 0) +
					(getPlayer().moveStrafing > 0 ? (-90 * (getPlayer().moveForward < 0 ? -0.5F : (getPlayer().moveForward > 0 ? 0.5F : 1))) : 0) -
					(getPlayer().moveStrafing < 0 ? (-90 * (getPlayer().moveForward < 0 ? -0.5F : (getPlayer().moveForward > 0 ? 0.5F : 1))) : 0),
					xDir = (float) Math.cos((direction + 90) * Math.PI / 180),
					zDir = (float) Math.sin((direction + 90) * Math.PI / 180);
			addPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + (xDir * 0.3), getPlayer().posY, getPlayer().posZ + (zDir * 0.3), getPlayer().onGround));
		}*/
		getPlayer().swingItem();
		addPacket(new C02PacketUseEntity(e, C02PacketUseEntity.Action.ATTACK));
/*		if(getPlayer().getDistanceToEntity(e) > 3.9 && x != null){
			addPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, onGround));
		}*/
		if (!e.isBurning()) e.setFire(1);
		timeHelp.reset();
		timeHelp.addReset((new Random()).nextInt(randomDelay));
		targets.remove(e);

		boolean jumpCritical = getPlayer().fallDistance > 0.0F && !getPlayer().onGround && !getPlayer().isOnLadder() && !getPlayer().isInWater() && !getPlayer().isPotionActive(Potion.blindness) && getPlayer().ridingEntity == null;
		float enchantmentCritical = EnchantmentHelper.func_152377_a(getPlayer().getHeldItem(), ((EntityLivingBase) e).getCreatureAttribute());
		if (jumpCritical && e instanceof EntityLivingBase) getPlayer().onCriticalHit(e);
		if (enchantmentCritical > 0.0F) getPlayer().onEnchantmentCritical(e);

		if (getPlayer().getHeldItem() != null && getPlayer().getHeldItem().getItem() instanceof ItemSword && getPlayer().isBlocking() && GameUtil.isStandingStill()) {
			sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
			justBlocked = true;
		}
	}

	public EntityType getEntityType(EntityLivingBase e) {
		if (e instanceof EntityMob) {
			return EntityType.MOB;
		} else if (e instanceof EntityPlayer) {
			return EntityType.PLAYER;
		} else if (e instanceof EntityCreature) {
			return EntityType.ANIMAL;
		} else {
			return EntityType.UNKNOWN;
		}
	}

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(ATTACKS_PER_SECOND)) return aps;
		else if (n.equals(REACH)) return reach;
		else if (n.equals(KILL_POV)) return killaurapov;
		else if (n.equals(RANDOMDELAY)) return randomDelay;
		else if (n.equals(MOBS)) return mobs;
		else if (n.equals(PLAYERS)) return players;
		else if (n.equals(ARMORBREAKER)) return armorbreaker;
		else if (n.equals(SPAWNDELAY)) return spawnDelay;
		else if (n.equals(INVISIBLES)) return invisibles;
		else if (n.equals(ENTITIES_IN_ROUND)) return entitiesInRound;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(ATTACKS_PER_SECOND)) aps = (Integer) v;
		else if (n.equals(REACH)) reach = Math.round((Double) v * 10) / 10.0D;
		else if (n.equals(KILL_POV)) killaurapov = (Integer) v;
		else if (n.equals(RANDOMDELAY)) randomDelay = (Integer) v;
		else if (n.equals(MOBS)) mobs = (Boolean) v;
		else if (n.equals(PLAYERS)) players = (Boolean) v;
		else if (n.equals(ARMORBREAKER)) armorbreaker = (Boolean) v;
		else if (n.equals(SPAWNDELAY)) spawnDelay = (Integer) v;
		else if (n.equals(INVISIBLES)) invisibles = (Boolean) v;
		else if (n.equals(ENTITIES_IN_ROUND)) entitiesInRound = (Integer) v;
	}

	public enum EntityType {
		PLAYER, MOB, ANIMAL, UNKNOWN
	}

}
