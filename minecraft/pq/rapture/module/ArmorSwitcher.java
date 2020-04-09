package pq.rapture.module;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import pq.rapture.Wrapper;
import pq.rapture.command.base.Command;
import pq.rapture.command.base.CommandManager;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.Inventory;
import pq.rapture.util.TimeHelper;

public class ArmorSwitcher extends Module {

	public ArmorSwitcher() {
		super(Protection.decrypt("EE5E9422A824C71D3AD4DDC7E59CAA4C"), new String[] {
				Protection.decrypt("09249E7A62D24A52ECA1939EAEAB81FE"), Protection.decrypt("2DAA5CEB18A3CADD5A698E08D3C2F7CC") },
				Protection.decrypt("05E611F3FE92065C27BFBE29B7B60AF2D9B3BEB8E48594F0DFF33137F66C8EF5B20C4CAEA5181353097DD30199CE2495"), Type.COMBAT, "NONE", 0xFF);
		CommandManager.commands.add(new Command(this.getAliases(), this.getDescription()) {
			@Override
			protected void onCommand(String[] args, String message) {
				Wrapper.getMod(ArmorSwitcher.class).toggle(true);
			}
		});
		this.setVisible(false);
	}

	int currentPiece = -1;
	int[] armorPieces = new int[] { -1, -1, -1, -1 };
	TimeHelper time = TimeHelper.getTimer();

	@Override
	public boolean onEnable() {
		boolean foundArmor = false;
		for(int o = 9; o <= 44; o++) {
			if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
				ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o).getStack();
				if (item != null && item.getItem() != null && item.getItem() instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) item.getItem();
					armorPieces[armor.armorType] = o;
					currentPiece = 0;
					foundArmor = true;
				}
			}
		}
		return foundArmor;
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onMotion(EventPreMotion pre) {
		if (time.hasDelayRun(120) && currentPiece < 4) {
			int slot = armorPieces[currentPiece];
			Inventory.putSlotInArmorSlot(slot, currentPiece);
			currentPiece++;
			time.reset();
		} else if (time.hasDelayRun(230) && currentPiece == 4) {
			Inventory.clickSlot(armorPieces[0], 1, 0);
			this.toggle(true);
		}
	}
}
