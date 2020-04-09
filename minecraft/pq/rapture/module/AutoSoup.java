package pq.rapture.module;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.Inventory;
import pq.rapture.util.TimeHelper;

public class AutoSoup extends Module implements HasValues {

	private boolean dropBowls = false;
	private float maxhealth = 12;
	private TimeHelper time = TimeHelper.getTimer();
	private static final String EncryptedName = Protection.decrypt("20355C4286081073DE7A6D942156D0DA");

	public AutoSoup() {
		super(EncryptedName, new String[] { "" }, Protection.decrypt("01048B8A5560DED960BF159F7606D4208EFBBEEA551F95CB5F32132CCAE6103B"), Type.COMBAT, "NONE", 0xFFfbf45c);
	}

	@Override
	public boolean onDisable() {
		setName(EncryptedName);
		return super.onDisable();
	}

	@ETarget(eventClasses = { EventPacketGet.class })
	public void onEvent(EventPacketGet get) {
		setName(EncryptedName + " [" + Inventory.amoutofItem(282) + "]");
		if (get.getPacket() instanceof S06PacketUpdateHealth) {
			S06PacketUpdateHealth healthpacket = (S06PacketUpdateHealth) get.getPacket();
			if (healthpacket.getHealth() <= maxhealth) {
				if (time.hasDelayRun(100)) {
					if (Inventory.findHotbarItem(282) != -1 && getPlayer().inventoryContainer.getSlot(Inventory.findHotbarItem(282)).getStack() != null) {
						int oldItem = getPlayer().inventory.currentItem;
						Inventory.swap(Inventory.findHotbarItem(282) - 36);
						Inventory.packetUse(getPlayer().inventoryContainer.getSlot(Inventory.findHotbarItem(282)).getStack());
						if(dropBowls) getPlayer().dropOneItem(true);
						Inventory.swap(oldItem);
						time.reset();
						time.addReset((new Random()).nextInt(50));
					}
					if (Inventory.findAvailableSlot(281) != -1 && Inventory.findInventoryItem(282) != -1) {
						Inventory.moveHotbar(Inventory.findInventoryItem(282), Inventory.findAvailableSlot(281) - 36);
					}
				}
			}
		} else if (get.getPacket() instanceof S00PacketKeepAlive) {
			if (Inventory.amoutofItem(281) > 4) {
				int foundplace = Inventory.findInventoryItem(281);
				Inventory.clickSlot(foundplace, 0, 0);
				Inventory.clickSlot(foundplace, 0, 6);
				Inventory.clickSlot(foundplace, 0, 0);
			} else if (Inventory.amoutofItem(281) > 0 && Inventory.findAvailableSlot(281) != -1 && Inventory.findInventoryItem(282) != -1) {
				Inventory.moveHotbar(Inventory.findInventoryItem(282), Inventory.findAvailableSlot(281) - 36);
			}
		}
	}

	private String DROPBOWLS = "Drop Bowls";
	private final Value[] PARAMETERS = new Value[] { new Value(DROPBOWLS, false, true) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if(n.equals(DROPBOWLS)) return dropBowls;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if(n.equals(DROPBOWLS)) dropBowls = (Boolean) v;
	}

}
