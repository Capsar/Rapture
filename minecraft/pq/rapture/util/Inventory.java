package pq.rapture.util;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.potion.PotionEffect;
import pq.rapture.Wrapper;

public class Inventory {
	public static void updateInventory() {
		for (int index = 0; index < 44; index++) {
			try {
				int offset = index < 9 ? 36 : 0;
				Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(index + offset, Minecraft.getMinecraft().thePlayer.inventory.mainInventory[index]));
			} catch (Exception exception) {
			}
		}
	}

	public static void clickSlot(int slot, int mouseButton, int mode) {
		Wrapper.getPlayerController().windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, slot, mouseButton, mode, Minecraft.getMinecraft().thePlayer);
	}

	public static void moveHotbar(int slot, int hotbarslot) {
		if (hotbarslot < 0) hotbarslot = 0;
		if (hotbarslot > 8) hotbarslot = 8;
		Wrapper.getPlayerController().windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, slot, hotbarslot, 2, Wrapper.getPlayer());
	}

	/** @return First hotbar slot number containing the item id. */
	public static int findHotbarItem(int itemID) {
		for (int o = 36; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (Item.getIdFromItem(item.getItem()) == itemID) { return o; }
				}
			}
		}
		return -1;
	}

	public static int findInventoryItem(int itemID) {
		for (int o = 9; o <= 35; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (Item.getIdFromItem(item.getItem()) == itemID) { return o; }
				}
			}
		}
		return -1;
	}

	public static int findWholeInventoryItem(int itemID) {
		for (int o = 9; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (Item.getIdFromItem(item.getItem()) == itemID) { return o; }
				}
			}
		}
		return -1;
	}

	public static int findWholeInventoryItem(ItemStack is) {
		for (int o = 9; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (item == is) { return o; }
				}
			}
		}

		return -1;
	}

	public static int findInventoryPot(String name) {
		for (int o = 9; o <= 35; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (item.getItem() instanceof ItemPotion) {
						ItemPotion pot = (ItemPotion) item.getItem();
						for (PotionEffect pe : (List<PotionEffect>) pot.getEffects(item)) {
							if (pe.getEffectName() == name && pe.toString().contains(", Splash: true")) { return o; }
						}
					}
				}
			}
		}

		return -1;
	}

	public static int findHotbarPot(String name) {
		for (int o = 36; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (item.getItem() instanceof ItemPotion) {
						ItemPotion pot = (ItemPotion) item.getItem();
						for (PotionEffect pe : (List<PotionEffect>) pot.getEffects(item)) {
							if (pe.getEffectName() == name && pe.toString().contains(", Splash: true")) { return o; }
						}
					}
				}
			}
		}

		return -1;
	}

	public static int findAvailableSlot(int itemID) {
		for (int o = 36; o <= 44; o++) {
			ItemStack item = Wrapper.getPlayer().inventoryContainer.getSlot(o).getStack();
			if (item == null) {
				return o;
			} else if (GameUtil.getItemID(item.getItem()) == itemID) { return o; }
		}
		return -1;
	}

	public static int amoutofItem(int itemID) {
		int amount = 0;
		for (int o = 9; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (Item.getIdFromItem(item.getItem()) == itemID) {
						amount++;
					}
				}
			}
		}
		return amount;
	}

	public static int getPotionAmp(ItemStack item) {
		if (item != null) {
			if (item.getItem() instanceof ItemPotion) {
				ItemPotion pot = (ItemPotion) item.getItem();
				for (PotionEffect pe : (List<PotionEffect>) pot.getEffects(item)) {
					if (pe.toString().contains(", Splash: true")) { return pe.getAmplifier() == 0 ? 1 : pe.getAmplifier(); }
				}
			}
		}
		return 1;
	}

	public static int amountofPot(String name) {
		int amount = 0;
		for (int o = 9; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null) {
					if (item.getItem() instanceof ItemPotion) {
						ItemPotion pot = (ItemPotion) item.getItem();
						for (PotionEffect pe : (List<PotionEffect>) pot.getEffects(item)) {
							if (pe.getEffectName() == name && pe.toString().contains(", Splash: true")) {
								amount++;
							}
						}
					}
				}
			}
		}
		return amount;
	}

	public static void swap(int zeroto8) {
		Wrapper.sendPacket(new C09PacketHeldItemChange(zeroto8));
	}

	public static void packetUse(ItemStack item) {
		Wrapper.sendPacket(new C08PacketPlayerBlockPlacement(item));
	}

	public static void normalUse(ItemStack itemStackIn, int slot) {
		Wrapper.addPacket(new C08PacketPlayerBlockPlacement(itemStackIn));
		if (!Wrapper.getPlayer().capabilities.isCreativeMode) {
			itemStackIn.stackSize--;
			if (itemStackIn.stackSize == 0) Wrapper.getPlayer().inventory.mainInventory[slot] = null;
		}
	}

	public static void putSlotInArmorSlot(int slot, int armorSlot) {
		clickSlot(slot, 0, 0);
		switch (armorSlot) {
		case 0:
			// helmet
			clickSlot(5, 0, 0);
			break;
		case 1:
			// chestplate
			clickSlot(6, 0, 0);
			break;
		case 2:
			// leggings
			clickSlot(7, 0, 0);
			break;
		case 3:
			// boots
			clickSlot(8, 0, 0);
			break;
		}
	}

}