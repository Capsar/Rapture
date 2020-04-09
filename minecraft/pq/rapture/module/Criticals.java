package pq.rapture.module;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;

public class Criticals extends Module {

	private int timer = 0;
	private double fallDist = 0;

	public Criticals() {
		super(Protection.decrypt("19C2693CE1FD9AC5EB7CF5BD048FAACF"), new String[] { "" }, Protection.decrypt("F3772F2860D02ED85CC5C721695D0ED9077365FFC2459B7F89BF2DD0E0F1ECD5"), Type.COMBAT, "NONE", 0xFFf37735);
	}

	@Override
	public boolean onEnable() {
		if (getPlayer() == null) return false;

		sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY + 1.3, getPlayer().posZ,
				getPlayer().rotationYaw, getPlayer().rotationPitch, false));
		fallDist = 1.3;
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		fallDist = 0;
		return super.onDisable();
	}

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPacketSend.class, EventPreMotion.class })
	public void onEvent(EventPacketSend send, EventPreMotion pre) {
		if (pre != null) {
			if (getPlayer().getHealth() <= 0 || getPlayer().isDead) {
				setColor(0xf37735);
				fallDist = 0.0D;
				timer = 0;
			}
		} else if (send != null) {
			if (send.getPacket() instanceof C03PacketPlayer) {
				final C03PacketPlayer player = (C03PacketPlayer) send.getPacket();

				if (getMod(Jesus.class).getState() && GameUtil.getColliding(0) != null) {
					fallDist = 0.0D;
					timer = 0;
					setColor(0xFF808080);
					return;
				}

				if (!isSafe() && getPlayer().motionY < -0.0784000015258789 && getPlayer().fallDistance > 0.2) {
					fallDist += getPlayer().fallDistance;
				}

				boolean mining = getMinecraft().objectMouseOver.typeOfHit == MovingObjectType.BLOCK
						&& getGameSettings().keyBindAttack.isKeyDown();
				if (fallDist >= 3.5D || isSafe() || mining) {
					getPlayer().fallDistance = 0.0F;
					player.setOnGround(true);
					setColor(0xFF808080);
					fallDist = 0.0D;
				} else if (fallDist > 1) {

					player.setOnGround(false);
					setColor(0xFF978712);
					timer = 0;
				} else if (fallDist < 1 && !isSafe() && !GameUtil.isInsideBlock()) {
					if (timer < 10) {
						timer++;
					} else if (!getGameSettings().keyBindAttack.isKeyDown() && getPlayer().onGround
							&& GameUtil.getBlockAbovePlayer(1) == Blocks.air || GameUtil.getBlockAbovePlayer(1) == Blocks.reeds) {
						sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY + 1.15, getPlayer().posZ,
								getPlayer().rotationYaw, getPlayer().rotationPitch, false));
						player.setOnGround(false);
						fallDist = 1.15;
					}
				} else {
					setColor(0xFF808080);
				}
			}
		}
	}

	public boolean isSafe() {
		return getPlayer().isInWater() || getPlayer().isInsideOfMaterial(Material.lava) || getPlayer().isOnLadder()
				|| getPlayer().getActivePotionEffects().contains(Potion.blindness) || getPlayer().ridingEntity != null;
	}
}
