package pq.rapture.module;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.TimeHelper;

public class Velocity extends Module implements HasValues {

	public Velocity() {
		super(Protection.decrypt("4E4F56012E31498E527FEE96E04B6578"), new String[] { "" }, Protection
				.decrypt("4D8A5AFDF3A2994BC173DB3D5BACD9157FBE3FD6134325EE63E8B548D56A60C1"), Type.COMBAT, "NONE", 0xFF9c4bfd);
	}

	private int horzontalVeloc = 100;
	private int verticalVeloc = 100;
	private boolean fighter = true;
	private boolean reverse = false;
	private boolean antivel = false;
	private TimeHelper timer = new TimeHelper();

	@ETarget(eventClasses = { EventPacketGet.class })
	public void onEvent(EventPacketGet e) {
		if (e.getPacket() instanceof S12PacketEntityVelocity) {
			S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
			Entity var2 = getWorld().getEntityByID(packet.func_149412_c());

			if (var2 == getPlayer()) {
				double x = packet.func_149411_d();
				double y = packet.func_149410_e();
				double z = packet.func_149409_f();
				e.cancel();

				if (reverse) {
					x = -x;
					z = -z;
				}

				if (antivel || getMod(Blink.class).getState()) {
					return;
				} else if (fighter && !getMod(Phase.class).inBlock) {
					doFighter((EntityPlayerSP) var2, x, y, z);
				} else if (!getMod(Phase.class).inBlock) {
					double hor = horzontalVeloc / 100.0;
					double dh = 8000 * (1 / hor);
					double ver = verticalVeloc / 100.0;
					double dv = 8000 * (1 / hor);
					var2.setVelocity(0, 0, 0);
					var2.setVelocity(x / dh, y / dv, z / dh);
				}
			}
		}

	}

	private void doFighter(EntityPlayerSP player, double x, double y, double z) {
		boolean triggerbot = getMod(Triggerbot.class).getState() && getMod(Triggerbot.class).entityTarget != null;
		boolean killaura = getMod(KillAura.class).getState() && KillAura.getTarget() != null;
		boolean arrowhit = false;
		for(Entity e : (List<Entity>) getWorld().getEntitiesWithinAABBExcludingEntity(getPlayer(),
				getPlayer().getEntityBoundingBox().expand(3, 3, 3))) {
			if (e instanceof EntityArrow) {
				EntityArrow arrow = (EntityArrow) e;
				if (!arrow.inGround) {
					arrowhit = true;
				}
			}
		}
		boolean enemyinsight = false;
		for(EntityPlayer ep : (List<EntityPlayer>) getWorld().playerEntities) {
			if (ep.isDead || ep.getHealth() < 0 || ep.getCommandSenderName().equals("") || ep.ticksExisted <= 20
					|| getPlayer().getDistanceToEntity(ep) > 4 || !getPlayer().canEntityBeSeen(ep)) continue;
			float[] angles = GameUtil.getAngles(ep, getPlayer());
			double yawDist = GameUtil.getDistanceBetweenAngle(getPlayer().rotationYaw, angles[0]);
			if (yawDist < 40) {
				enemyinsight = true;
			}
		}
		double hor = horzontalVeloc / 100.0;
		double ver = verticalVeloc / 100.0;

		if (triggerbot || killaura || arrowhit || enemyinsight) {
			timer.reset();
			player.setVelocity(0, 0, 0);
			player.setVelocity(x / (11000.0D * (1 / hor)), y / (9000.0D * (1 / ver)), z / (11000.0D * (1 / hor)));
		} else {
			if (timer.hasDelayRun(3000)) {
				player.setVelocity(0, 0, 0);
				player.setVelocity(x / (5000.0D * (1 / hor)), y / (6000.0D * (1 / ver)), z / (5000.0D * (1 / hor)));
			} else {
				player.setVelocity(0, 0, 0);
				player.setVelocity(x / (11000.0D * (1 / hor)), y / (9000.0D * (1 / ver)), z / (11000.0D * (1 / hor)));
			}
		}
	}

	private static final String HORI = Protection.decrypt("E217E6AA5E86324EAAF6DED210B9A472A39808038275387F386ACB4D258EAE0E");
	private static final String VERT = Protection.decrypt("DBBBFFE42161BD58803776CE7630D50FFB8EFA220222E9E70AB1FA11E72817B1");
	private static final String REVERSEVEL = Protection.decrypt("CF192A2A6D566B81F89CA6EE4E4417D815319490761930059D7FEF3DD60AA33A");
	private static final String FIGHTERVEL = Protection.decrypt("107ABF79171008F9C253BC205C99B6FE");
	private static final String ANTI = Protection.decrypt("F2AA38AF8619094D5C8A3F513A6DAD00");

	private static final Value[] PARAMETERS = new Value[] { new Value(FIGHTERVEL, false, true), new Value(REVERSEVEL, false, true),
			new Value(ANTI, false, true), new Value(HORI, 0, 200, 5), new Value(VERT, 0, 200, 5) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(REVERSEVEL))
			return reverse;
		else if (n.equals(HORI))
			return horzontalVeloc;
		else if (n.equals(VERT))
			return verticalVeloc;
		else if (n.equals(FIGHTERVEL))
			return fighter;
		else if (n.equals(ANTI)) return antivel;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(REVERSEVEL))
			reverse = (Boolean) v;
		else if (n.equals(HORI))
			horzontalVeloc = (Integer) v;
		else if (n.equals(VERT))
			verticalVeloc = (Integer) v;
		else if (n.equals(FIGHTERVEL))
			fighter = (Boolean) v;
		else if (n.equals(ANTI)) antivel = (Boolean) v;

	}

}
