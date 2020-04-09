package pq.rapture.module;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.TimeHelper;

public class FastLadder extends Module implements HasValues {

	public FastLadder() {
		super(Protection.decrypt("04D11B90AD6276F828A811962B7DCA06"), new String[] { "" }, Protection
				.decrypt("FD6F58D20B41D2C96E85B87A9ACC21565FB339D51D273E5607CE716A49D2C898"), Type.MOVEMENT, "NONE", 0xFFcab34f);
	}

	private int timer, timerf;
	private boolean wasOnLadder;
	private boolean tpUp = false;

	@ETarget(eventClasses = { EventPreMotion.class })
	public void preMotion(EventPreMotion pre) {
		if (getPlayer().onGround && getPlayer().isOnLadder() && getPlayer().isCollidedHorizontally && tpUp) {
			for(int y = 1; y < 11; y++) {
				if (GameUtil.getBlockAbovePlayer(y) == Blocks.ladder && GameUtil.getBlockAbovePlayer(y + 2) != Blocks.ladder
						&& GameUtil.getBlockAbovePlayer(y + 2) != Blocks.air) {
					GameUtil.offset(0, y - 0.2, 0);
					break;
				} else if (y == 10 && GameUtil.getBlockAbovePlayer(y - 1) == Blocks.ladder) {
					GameUtil.offset(0, y - 0.2, 0);
				}
			}
		}

		if (!getPlayer().isOnLadder() && !getPlayer().isCollidedHorizontally && wasOnLadder) {
			timer++;
			if (timer >= 4) {
				wasOnLadder = false;
				timer = 0;
			}
		}
		if (getPlayer().isOnLadder() && getPlayer().isCollidedHorizontally) {
			wasOnLadder = true;
			timerf++;
		} else {
			timerf = 0;
		}
		if (timerf >= 2 && getPlayer().isCollidedHorizontally
				&& (GameUtil.getBlockAbovePlayer(1) == Blocks.ladder || GameUtil.getBlockAbovePlayer(1) == Blocks.vine)) {
			GameUtil.offset(0, getPlayer().motionY * 1.4, 0);
		}
		if (getPlayer().isOnLadder() && getPlayer().motionY <= -0.22540000438690183
				&& (GameUtil.getBlockAbovePlayer(-2) == Blocks.ladder || GameUtil.getBlockAbovePlayer(-2) == Blocks.vine)) {
			GameUtil.offset(0, getPlayer().motionY / 1.8, 0);

		}
		if (getPlayer().isCollidedHorizontally && wasOnLadder && GameUtil.getBlockAbovePlayer(1) == Blocks.ladder
				&& !getPlayer().isOnLadder()) {
			getPlayer().motionY = 0.2D;
		}

	}

	private String vertical = Protection.decrypt("B50B055344C4BA3D221476CD03D93B73");

	@Override
	public List<Value> getValues() {
		return Arrays.asList(new Value(vertical, false, true));
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(vertical)) { return tpUp; }
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(vertical)) {
			tpUp = (Boolean) v;
		}
	}
}
