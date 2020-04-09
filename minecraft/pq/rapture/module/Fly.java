package pq.rapture.module;

import org.lwjgl.input.Keyboard;

import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;

public class Fly extends Module {

	public Fly() {
		super(Protection.decrypt("8759438803009408182C5EFC8DACB876"), new String[] { "" }, Protection.decrypt("45FFF7FC708281CB7B60AFB2D390F24A"), Type.MOVEMENT, "NONE", 0xFFf6dc5c);
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		getPlayer().onGround = false;
		getPlayer().motionX = 0.0D;
		getPlayer().motionY = 0.0D;
		getPlayer().motionZ = 0.0D;

		if (getGameSettings().keyBindJump.isKeyDown() && getMinecraft().inGameHasFocus) {
			getPlayer().motionY += 3;
		} else if (getGameSettings().keyBindSneak.isKeyDown() && getMinecraft().inGameHasFocus) {
			getPlayer().motionY -= 3;
		}
		double d7 = getPlayer().rotationYaw + 90F;
		boolean flag4 = Keyboard.isKeyDown(17) && getMinecraft().inGameHasFocus;
		boolean flag6 = Keyboard.isKeyDown(31) && getMinecraft().inGameHasFocus;
		boolean flag8 = Keyboard.isKeyDown(30) && getMinecraft().inGameHasFocus;
		boolean flag10 = Keyboard.isKeyDown(32) && getMinecraft().inGameHasFocus;

		if (flag4) {
			if (flag8) {
				d7 -= 45D;
			} else if (flag10) {
				d7 += 45D;
			}
		} else if (flag6) {
			d7 += 180D;
			if (flag8) {
				d7 += 45D;
			} else if (flag10) {
				d7 -= 45D;
			}
		} else if (flag8) {
			d7 -= 90D;
		} else if (flag10) {
			d7 += 90D;
		}
		if (flag4 || flag8 || flag6 || flag10) {
			getPlayer().motionX = Math.cos(Math.toRadians(d7));
			getPlayer().motionZ = Math.sin(Math.toRadians(d7));
		}
	}
	

}
