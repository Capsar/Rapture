package pq.rapture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import pq.rapture.command.base.CommandManager;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;
import pq.rapture.render.font.RaptureFontRenderer;
import pq.rapture.render.tab.TabGUI;
import pq.rapture.util.Colors;

public class Wrapper {

	private static ModuleManager modulemanager = new ModuleManager();
	private static CommandManager commandManager = new CommandManager();
	private static RaptureFontRenderer font;
	private static TabGUI tabGui;
	public static float partialTicks;

	public static ModuleManager getModuleManager() {
		return modulemanager;
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}

	public static ItemRenderer getItemRenderer() {
		return getMinecraft().getItemRenderer();
	}

	public static GameSettings getGameSettings() {
		return getMinecraft().gameSettings;
	}

	public static FontRenderer getFontRenderer() {
		return getMinecraft().fontRendererObj;
	}

	public static ScaledResolution getScaledResolution() {
		return new ScaledResolution(getMinecraft(), getMinecraft().displayWidth, getMinecraft().displayHeight);
	}

	public static EntityPlayerSP getPlayer() {
		return getMinecraft().thePlayer;
	}

	public static WorldRenderer getWorldRenderer() {
		return Tessellator.getInstance().getWorldRenderer();
	}

	public static WorldClient getWorld() {
		return getMinecraft().theWorld;
	}

	public static RenderItem getRenderItem() {
		return getMinecraft().getRenderItem();
	}

	public static PlayerControllerMP getPlayerController() {
		return getMinecraft().playerController;
	}

	public static RenderManager getRenderManager() {
		return getMinecraft().getRenderManager();
	}

	public static RenderGlobal getGlobalRenderer() {
		return getMinecraft().renderGlobal;
	}

	public static NetHandlerPlayClient getSendQueue() {
		return getMinecraft().thePlayer != null ? getMinecraft().thePlayer.sendQueue : null;
	}

	public static void addPacket(Packet packet) {
		getSendQueue().addToSendQueue(packet);
	}

	public static void sendPacket(Packet packet) {
		getSendQueue().getNetworkManager().sendPacket(packet);
	}

	public static TabGUI getTabGui() {
		return tabGui;
	}

	public static RaptureFontRenderer getFont() {
		return font;
	}

	public static <T extends Module> T getMod(Class<T> module) {
		getModuleManager();
		for (final Module m : ModuleManager.modules) {
			if (module.isInstance(m))
				return module.cast(m);
		}
		return null;
	}

	public static void sendChat(String MSG) {
		sendPacket(new C01PacketChatMessage(MSG));
	}

	public static void setTabGui(TabGUI tabgui) {
		tabGui = tabgui;
	}

	public static void setFont(RaptureFontRenderer font) {
		Wrapper.font = font;
	}
	
	public static void addChat(ChatEnum mode, String message) {
		String type = "";
		switch (mode) {
		case ERROR:
			type = Colors.GRAY + "[" + Colors.RED + "Error" + Colors.GRAY + "] " + Colors.WHITE;
			break;
		case COMMAND:
			type = Colors.GRAY + "[" + Colors.BLUE + RaptureClient.getName() + Colors.GRAY + "] " + Colors.WHITE + RaptureClient.getPrefix();
			break;
		case NOTIFY:
			type = Colors.GRAY + "[" + Colors.BLUE + RaptureClient.getName() + Colors.GRAY + "] " + Colors.WHITE;
			break;
		}

		String MSG = Colors.GRAY + type + message;
		IChatComponent chat = new ChatComponentText(MSG);
		getPlayer().addChatMessage(chat);
	}

	public enum ChatEnum {
		ERROR, COMMAND, NOTIFY;
	}

}
