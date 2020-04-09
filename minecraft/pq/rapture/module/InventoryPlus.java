package pq.rapture.module;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.objects.moreinventory.CustomGuiInventory;
//import pq.rapture.module.objects.moveinventory.CustomGuiInventory;
import pq.rapture.protection.Protection;
import pq.rapture.render.click.ClickGUI;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventTick;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;

public class InventoryPlus extends Module {

	public InventoryPlus() {
		super(Protection.decrypt("99F2C0408428F46E82CA121F38892C9C"), new String[] { "" }, Protection.decrypt("19DCB8C198B64EA13026BA421B038CD306633312FB41F5D3BC167ED7DF4C412F675AE8ADF5FF485F99C4C474325DE379"), Type.PLAYER, "NONE", 0xFF1a6761);
	}

	private final ItemStack[] items = new ItemStack[4];

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventPacketSend.class })
	public void onEvent(EventPreMotion pre, EventPacketSend send) {
		if (send != null) {
			if (send.getPacket() instanceof C0DPacketCloseWindow) {
				if (getPlayer().openContainer.windowId == 0) send.cancel();
			}
		} else if (pre != null) {
			if (getMinecraft().currentScreen instanceof GuiInventory) {
				getMinecraft().displayGuiScreen(new CustomGuiInventory(getPlayer()));
				ContainerPlayer containerPlayer = (ContainerPlayer) getPlayer().inventoryContainer;
				for (int index = 0; index < this.items.length; index++) {
					containerPlayer.craftMatrix.setInventorySlotContents(index, this.items[index]);
					this.items[index] = null;
				}
			} else if (getMinecraft().currentScreen instanceof CustomGuiInventory) {
				ContainerPlayer containerPlayer = (ContainerPlayer) getPlayer().inventoryContainer;
				for (int index = 0; index < this.items.length; index++) {
					this.items[index] = containerPlayer.craftMatrix.getStackInSlot(index);
				}
			}

			if (!(getMinecraft().currentScreen instanceof GuiChat) && (getMinecraft().currentScreen != null) && !(getMinecraft().currentScreen instanceof ClickGUI)) {
				float changeAmount = 4;

				if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) getPlayer().rotationPitch += changeAmount;
				if (Keyboard.isKeyDown(Keyboard.KEY_UP)) getPlayer().rotationPitch -= changeAmount;
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) getPlayer().rotationYaw -= changeAmount;
				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) getPlayer().rotationYaw += changeAmount;

				if (getPlayer().rotationPitch > 90) getPlayer().rotationPitch = 90;
				if (getPlayer().rotationPitch < -90) getPlayer().rotationPitch = -90;

				KeyBinding[] moveKeys = { getGameSettings().keyBindForward, getGameSettings().keyBindBack, getGameSettings().keyBindLeft, getGameSettings().keyBindRight, getGameSettings().keyBindJump, getGameSettings().keyBindSprint };
				for (KeyBinding bind : moveKeys)
					KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
			}
		}
	}

	@Override
	public boolean onDisable() {
		sendPacket(new C0DPacketCloseWindow(-1));
		return super.onDisable();
	}
}
