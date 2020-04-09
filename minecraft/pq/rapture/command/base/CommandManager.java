package pq.rapture.command.base;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.client.network.NetworkPlayerInfo;
import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.Wrapper.ChatEnum;
import pq.rapture.command.Bind;
import pq.rapture.command.Help;
import pq.rapture.command.Visibility;
import pq.rapture.module.FriendManager;
import pq.rapture.module.objects.Friend;
import pq.rapture.protection.SimpleCrypto;
import pq.rapture.rxdy.EventSendChatMessage;
import pq.rapture.rxdy.event.EventManager;
import pq.rapture.rxdy.event.annotations.ETarget;

public class CommandManager {

	public static ArrayList<Command> commands = new ArrayList<>();
	
	public void loadCommands() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
			} catch (Exception e) {e.printStackTrace();}
		EventManager.getInstance().register(this, EventSendChatMessage.class);
		commands.add(new Help());
		commands.add(new Bind());
		commands.add(new Visibility());
	}
	
	
	
	@ETarget(eventClasses = { EventSendChatMessage.class })
	public final void onSendChatMessage(EventSendChatMessage event) {
		String text = event.getMessage();
		
		for(Friend friend: FriendManager.friendList) {
			if(text.toLowerCase().contains("-" + friend.getAlias().toLowerCase())) {
				for(Object o: Wrapper.getSendQueue().func_175106_d()) {
					if(o instanceof NetworkPlayerInfo) {
						NetworkPlayerInfo info = (NetworkPlayerInfo) o;
						String name = info.getGameProfile().getName();
						if(friend.getName().equalsIgnoreCase(name)) {
							text = (text.replaceAll("-(?i)" + friend.getAlias(), friend.getName()));
							event.setMessage(text);
						}
					}
				}
			}
		}
		
		if (text.startsWith(RaptureClient.getPrefix()) && RaptureClient.isEnabled) {
			String[] args = text.substring(RaptureClient.getPrefix().length()).split(" ");
			boolean foundMod = false;
			for(Command c : commands) {
				if (c.isAlias(args[0])) {
					foundMod = true;
					event.cancel();
					c.onCommand(args, text);
					break;
				}
			}
			if (!foundMod) {
				Wrapper.addChat(ChatEnum.ERROR, "No command found, type .help for all commands.");
				event.cancel();
			}
		}
	}
}
