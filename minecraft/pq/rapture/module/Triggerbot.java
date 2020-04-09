package pq.rapture.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventMove;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.Inventory;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.TimeHelper;

public class Triggerbot extends Module implements HasValues {

	public Triggerbot() {
		super(Protection.decrypt("BE3C2FD41FCD5463EB67109BF7E96C54"), new String[] { "" }, Protection.decrypt("F23B9985F3B3C29F674B705FD2901C1404B67B159D37E78140AC89CC63FF71E7"), Type.COMBAT, "NONE", 0xFF12ac19);
	}

	public static boolean goingToTarget = false;
	public int aps = 5;
	public int aimSpeed = 2;
	public int aimAssistSpeed = 2;
	public int aimLimit = 60;
	public double attackRange = 4.2;
	public double smoothRange = 4.4;
	public boolean swordOnly = true;
	public boolean smoothAssist = true;
	public boolean blockHit = true;
	public EntityPlayer entityTarget;
	TimeHelper time = new TimeHelper();

	@Override
	public boolean onDisable() {
		goingToTarget = false;
		return super.onDisable();
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		if (pre != null) {
			if (GameUtil.getEntityOnMouseCurser(attackRange) != null) {
				Entity entity = GameUtil.getEntityOnMouseCurser(attackRange);
				if (entity instanceof EntityPlayer) {
					entityTarget = (EntityPlayer) entity;
					boolean canAttack = GameUtil.isAttackable(entityTarget, attackRange, FriendManager.sameTeam(entityTarget), false, 10, false);
					boolean shouldAim = shouldAim(5);
					if (canAttack && shouldAim) GameUtil.smoothAim(entityTarget, aimSpeed, false);
					if (aps != 0 && canAttack && time.hasDelayRun((1000 / aps))) {
						time.setReset(time.getCurrentTime() - new Random().nextInt(150));
						boolean wasBlock = false;
						if (blockHit && getPlayer().isBlocking())  {
							wasBlock = true;
							getPlayerController().onStoppedUsingItem(getPlayer());
						}
						getPlayer().swingItem();
						PacketUtil.addPacket(new C02PacketUseEntity(entityTarget, Action.ATTACK));
						GameUtil.attackEffectOnEntity(entityTarget);
						if(wasBlock) {
							getPlayerController().sendUseItem(getPlayer(), getWorld(), getPlayer().getHeldItem());
						}
					}
				}
			} else if (entityTarget != null) {
				boolean canAttack = GameUtil.isAttackable(entityTarget, attackRange, FriendManager.sameTeam(entityTarget), false, 10, false)
						&& getPlayer().getDistanceToEntity(entityTarget) <= smoothRange
						&& getPlayer().canEntityBeSeen(entityTarget);
				boolean shouldStopToAim = shouldAim(aimLimit);
				if (!canAttack || aimAssistSpeed == 0) {
					entityTarget = null;
					return;
				} else if (canAttack) {
					if (shouldStopToAim) {
						entityTarget = null;
						return;
					}
					if (smoothAssist) {
						GameUtil.smoothAim(entityTarget, aimAssistSpeed, false);
					}
				}
			}
		}
	}

	private boolean shouldAim(int degree) {
		float[] angles = GameUtil.getAngles(entityTarget, getPlayer());
		float yawDist = GameUtil.getDistanceBetweenAngle(getPlayer().rotationYaw, angles[0]);
		if (yawDist > degree) { return true; }
		return false;
	}

	private boolean shouldDisable() {
		if (getPlayer() == null) return true;
		if (getPlayer().isUsingItem()) return true;
		if (!getMinecraft().inGameHasFocus) return true;
		if (getPlayer().getHealth() <= 0) return true;
		if (swordOnly && getPlayer().getHeldItem() == null) return true;
		if (swordOnly && !(getPlayer().getHeldItem().getItem() instanceof ItemSword)) return true;
		if (getPlayer().isDead) this.toggle(true);
		return false;
	}

	private String APS = "Attacks per second", AIMSPEED = "Aim Speed", AIMASSISTSPEED = "Aim Assist Speed", ATTACKRANGE = "Attack Range", AIMLIMIT = "Aim Limit", SMOOTHRANGE = "Smooth Range", SWORDONLY = "Swords only",
			SMOOTHAIM = "Smooth aim assist", BLOCKHIT = "Block hit";
	private List<Value> values = Arrays.asList(new Value[] { new Value(APS, 0, 20, 1), new Value(ATTACKRANGE, 3.0, 6.0, 0.1F), new Value(SMOOTHRANGE, 3.0, 6.0, 0.1F), new Value(AIMSPEED, 0, 30, 1), new Value(AIMASSISTSPEED, 0, 30, 1),
			new Value(AIMLIMIT, 1, 180, 1), new Value(SMOOTHAIM, false, true), new Value(SWORDONLY, false, true), new Value(BLOCKHIT, false, true) });

	@Override
	public List<Value> getValues() {
		return values;
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(APS)) return aps;
		else if (n.equals(AIMSPEED)) return aimSpeed;
		else if (n.equals(AIMASSISTSPEED)) return aimAssistSpeed;
		else if (n.equals(ATTACKRANGE)) return attackRange;
		else if (n.equals(SMOOTHRANGE)) return smoothRange;
		else if (n.equals(SWORDONLY)) return swordOnly;
		else if (n.equals(SMOOTHAIM)) return smoothAssist;
		else if (n.equals(BLOCKHIT)) return blockHit;
		else if (n.equals(AIMLIMIT)) return aimLimit;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(APS)) aps = (Integer) v;
		else if (n.equals(AIMSPEED)) aimSpeed = (Integer) v;
		else if (n.equals(AIMASSISTSPEED)) aimAssistSpeed = (Integer) v;
		else if (n.equals(AIMLIMIT)) aimLimit = (Integer) v;
		else if (n.equals(ATTACKRANGE)) attackRange = (Math.round((Double) v * 10) / 10.0D);
		else if (n.equals(SMOOTHRANGE)) smoothRange = (Math.round((Double) v * 10) / 10.0D);
		else if (n.equals(SWORDONLY)) swordOnly = (Boolean) v;
		else if (n.equals(SMOOTHAIM)) smoothAssist = (Boolean) v;
		else if (n.equals(BLOCKHIT)) blockHit = (Boolean) v;
	}

}
