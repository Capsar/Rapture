package pq.rapture.module;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.TimeHelper;

public class Speed extends Module implements HasValues {

	public static boolean slowdown = false;

	public Speed() {
		super(Protection.decrypt("E6A03D5DBC63DC605DF2C1C6B06436B0"), new String[] { "s" }, Protection.decrypt("89C3CFB152A518371EE2651C49FFA50AE2AB976EE484F7567CF2ACA5A413F3DF"), Type.MOVEMENT, "NONE", 0xFF00b159);
	}

	int time = 0, jumpTime = 0, speedHalp = 0;
	TimeHelper timeHelp = TimeHelper.getTimer();
	static TimeHelper slowHelp = TimeHelper.getTimer();
	private double speed = 2.25;
	private double slow = 1.35;
	private boolean newSpeed = true;

	@ETarget(eventClasses = { EventPreMotion.class })
	public void preMotion(EventPreMotion pre) {
		double speed = this.speed;
		double devide = this.slow;

		if (!getPlayer().onGround || getGameSettings().keyBindJump.isKeyDown()) {
			timeHelp.reset();
			if (jumpTime == 0) {
				getPlayer().motionX /= (devide / 1.15F);
				getPlayer().motionZ /= (devide / 1.15F);
				jumpTime++;
			}
			time = 0;
		} else if (timeHelp.hasDelayRun(200) && !GameUtil.isStandingStill()) {
			boolean slowStep = false;
			for (AxisAlignedBB bb : (List<AxisAlignedBB>) getWorld().getCollidingBoundingBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(getPlayer().motionX, 0.0D, getPlayer().motionZ))) {
				Block b = GameUtil.getBlock(bb.minX, bb.minY, bb.minZ);
				if (bb != null && b != Blocks.web && b != Blocks.ladder && b != Blocks.vine && bb.maxY - bb.minY > 0.5) {
					slowStep = true;
				}
			}

			if (slowdown || slowStep || GameUtil.getColliding(0) != null) {
				if (slowdown) speed *= 0.62;
				else if (slowStep) speed *= 0.70;
				else if (GameUtil.getColliding(0) != null) speed *= 0.75;
				if (slowHelp.hasDelayRun(500)) {
					slowdown = false;
				}
			}
			if (GameUtil.getBlockBelowPlayer() == Blocks.ice || GameUtil.getBlockBelowPlayer() == Blocks.packed_ice) {
				Blocks.ice.slipperiness = 0.45F;
				Blocks.packed_ice.slipperiness = 0.45F;
			} else {
				time++;
				if (newSpeed) {
					if (this.time == 1) {
						getPlayer().motionX *= speed;
						getPlayer().motionZ *= speed;
					} else if (this.time >= 2) {
						getPlayer().motionX /= devide;
						getPlayer().motionZ /= devide;
						this.time = 0;
					}
				} else {
					switch (time) {
					case 1:
						jumpTime = 0;
						break;
					case 2:
						getPlayer().motionX *= speed;
						getPlayer().motionZ *= speed;
						break;
					case 3:
						getPlayer().motionX /= devide;
						getPlayer().motionZ /= devide;
						break;
					case 4:
						time = 0;
						break;
					}
				}
			}
		}

	}

	private final String SPEED = Protection.decrypt("E6A03D5DBC63DC605DF2C1C6B06436B0");
	private final String SLOW = Protection.decrypt("F1B97F6CF062CD00477168537D8DD25A");
	private final String NEWSPEED = Protection.decrypt("FFBBF03EEA6647B11660E395FF537B21");
	private final Value[] PARAMETERS = new Value[] { new Value(SPEED, 0.0, 4.0, 0.01F), new Value(SLOW, 1.0, 3.0, 0.01F), new Value(NEWSPEED, false, true) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(SPEED)) return speed;
		if (n.equals(SLOW)) return slow;
		if (n.equals(NEWSPEED)) return newSpeed;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(SPEED)) speed = Math.round((Double) v * 100) / 100.0D;
		if (n.equals(SLOW)) slow = Math.round((Double) v * 100) / 100.0D;
		if (n.equals(NEWSPEED)) newSpeed = (Boolean) v;
	}

	public static void slowdown() {
		slowdown = true;
		slowHelp.reset();
	}

}
