package pq.rapture.module;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.Inventory;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.TimeHelper;

public class AutoPot extends Module {

	static boolean isHealing;
	private TimeHelper getTime = new TimeHelper(), putTime = new TimeHelper();
	private float health = 20;
	private float maxhealth = 12;
	private final static String encryptedName = Protection.decrypt("3B83DBCFCAEF2ADA6872F9A1651AB8B8");

	public AutoPot() {
		super(encryptedName, new String[] { "" }, Protection.decrypt("F4502458BF30054A91CD4BB66A27F0A76EE32B9A3DC130DA8CF674184AD733AA"),
				Type.COMBAT, "NONE", 0xFFecbd80);
	}

	@Override
	public boolean onDisable() {
		isHealing = false;
		setName(encryptedName);
		return super.onDisable();
	}

	@ETarget(eventClasses = { EventPacketSend.class })
	public void onEvent(EventPacketSend send) {
		EventPacketSend e = send;
		if (e.getPacket() instanceof C03PacketPlayer) {
			setName(encryptedName + " [" + Inventory.amountofPot("potion.heal") + "]");

			C03PacketPlayer player = (C03PacketPlayer) e.getPacket();
			if (getPlayer().getHealth() <= maxhealth) {
				isHealing = true;
				if (getTime.hasDelayRun(200) && getPlayer().onGround) {
					if (Inventory.findHotbarPot("potion.heal") != -1) {
						int oldItem = getPlayer().inventory.currentItem;
						ItemStack item = getPlayer().inventoryContainer.getSlot(Inventory.findHotbarPot("potion.heal")).getStack();
						Inventory.swap(Inventory.findHotbarPot("potion.heal") - 36);
						double mX = getPlayer().motionX * 3;
						double mZ = getPlayer().motionZ * 3;
						float[]	angles = GameUtil.getAngles(getPlayer().posX + mX, getPlayer().getEntityBoundingBox().minY, getPlayer().posZ + mX, getPlayer());
						PacketUtil.sendPlayerLookPacket(getPlayer().posX, getPlayer().posY + 1.3, getPlayer().posZ, angles[0], angles[1], getPlayer().onGround);
						getPlayerController().updateController();
						Inventory.normalUse(item, Inventory.findHotbarPot("potion.heal") - 36);
						getPlayer().setHealth(getPlayer().getHealth() + 7);
						Inventory.swap(oldItem);
						PacketUtil.sendPlayerPacket(true);
						getPlayerController().updateController();
						isHealing = false;
						getTime.reset();
						e.cancel();
					}
				}
			} else {
				getTime.reset();
				isHealing = false;
			}
			if (putTime.hasDelayRun(150) && getMinecraft().inGameHasFocus) {
				if (Inventory.findInventoryPot("potion.heal") != -1 && Inventory.findAvailableSlot(-1) != -1) {
					Inventory.moveHotbar(Inventory.findInventoryPot("potion.heal"), Inventory.findAvailableSlot(-1) - 36);
					putTime.reset();
				}
			}
		}
	}
	
	
}
