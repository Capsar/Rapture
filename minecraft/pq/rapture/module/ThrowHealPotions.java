package pq.rapture.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
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
import pq.rapture.util.Inventory;
import pq.rapture.util.TimeHelper;

public class ThrowHealPotions extends Module implements HasValues {

	public ThrowHealPotions() {
		super(Protection.decrypt("8654B4709B41F476CA087288B4BC69F4"), new String[] { "" }, Protection.decrypt("70DD1DC1B5E0A6097C18F9257954860C4C8CAC65D4F3CFB2D563DF816F06687AAFB62EBFF3D25909FABDF6AD113EDF63"), Type.COMBAT, "NONE", 0xFF12ac19);
		this.setVisible(false);
	}

	TimeHelper time = TimeHelper.getTimer();
	private int currentItem = -1, potionPlace = -1;
	private float yaw, pitch, health;
	private boolean enemyinsight = false, triggerbot = false;
	private ArrayList<Integer> usedPots = new ArrayList<Integer>();
	private int potionDelay = 120, heals = 8;

	@Override
	public boolean onEnable() {
		currentItem = getPlayer().inventory.currentItem;
		health = getPlayer().getHealth();
		potionPlace = findHotbarPot(Potion.heal);
		if (potionPlace != -1 && getPlayer().getHealth() <= 16) {
			getPlayer().inventory.currentItem = potionPlace - 36;
			sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
			time.reset();
			return super.onEnable();
		} else {
			return false;
		}
	}

	@Override
	public boolean onDisable() {
		usedPots.clear();
		getPlayer().inventory.currentItem = currentItem;
		sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
		return super.onDisable();
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		if (pre != null) {
			if (potionPlace == -1 || getPlayer().getHealth() > 16 || health > 16) {
				this.toggle(true);
				return;
			}

			if (time.hasDelayRun(potionDelay)) {
				usedPots.add(Integer.valueOf(potionPlace));
				throwPot();

				potionPlace = findHotbarPot(Potion.heal);
				if (potionPlace != -1 && getPlayer().getHealth() <= 16 && health <= 16) {
					getPlayer().inventory.currentItem = potionPlace - 36;
					sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
				} else {
					this.toggle(true);
				}
			}
		}
	}

	private void throwPot() {
		if (getPlayer().getHeldItem() != null && getPlayer().getHeldItem().getItem() instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) getPlayer().getHeldItem().getItem();
			for(PotionEffect pe : (List<PotionEffect>) potion.getEffects(getPlayer().getHeldItem())) {
				if (pe.getPotionID() == Potion.heal.getId() && pe.toString().contains(", Splash: true")) {
					health += heals * (pe.getAmplifier() == 0 ? 1 : pe.getAmplifier());
				}
			}
			getPlayerController().sendUseItem(getPlayer(), getWorld(), getPlayer().getHeldItem());
		}
		time.reset();
	}

	public int findHotbarPot(Potion pots) {
		for(int o = 36; o <= 44; o++) {
			if (usedPots.contains(Integer.valueOf(o))) continue;

			if (getPlayer().inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = getPlayer().inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (item.getItem() instanceof ItemPotion) {
						ItemPotion pot = (ItemPotion) item.getItem();
						for(PotionEffect pe : (List<PotionEffect>) pot.getEffects(item)) {
							if (pe.getPotionID() == pots.getId() && pe.toString().contains(", Splash: true")) { return o; }
						}
					}
				}
			}
		}

		return -1;
	}

	private static final String DELAY = Protection.decrypt("2DAD5986001726CE9B485CC75FDF8789EA78BAC446E9C377C924B391FED50C85");
	private static final String HEALS = Protection.decrypt("6A64C1073DC0E8B942DB2FC5148FDFC5");


	private static final Value[] PARAMETERS = new Value[] { new Value(DELAY, 0, 300, 5), new Value(HEALS, 0, 20, 1.0F) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(DELAY)) return potionDelay;
		else if(n.equals(HEALS)) return heals;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(DELAY)) potionDelay = (Integer) v;
		else if(n.equals(HEALS)) heals = (Integer) v;

	}

}
