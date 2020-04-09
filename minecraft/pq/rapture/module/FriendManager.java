package pq.rapture.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import pq.rapture.command.base.Command;
import pq.rapture.command.base.CommandManager;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.HasValues.Value.ValueType;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.objects.Friend;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventMousePresses;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.Colors;
import pq.rapture.util.GameUtil;

public class FriendManager extends Module implements HasValues {
	public static CopyOnWriteArrayList<Friend> friendList = new CopyOnWriteArrayList<Friend>();

	public boolean teams = false, middleMouseFriends = false, nameProtect = false;

	public static CopyOnWriteArrayList<Friend> getFriendList() {
		return friendList;
	}

	public FriendManager() {
		super(Protection.decrypt("2B1DB3A72F901E2AD8A0C9204317C58B"), new String[] { Protection.decrypt("002C456DCA058548F3FB0594D1F535A2") }, Protection.decrypt("671480A112166F0673139FD28D511A16D49A6891D8EE6A5CA765859AD54D04DE"), Type.PLAYER,
				"NONE", 0xFFf6dc5c);
		this.setVisible(false);
		this.toggle(false);
		CommandManager.commands.add(new Command(new String[] { "friend", "friends", "fr", "f" }, "Manage your friends") {
			@Override
			protected void onCommand(String[] args, String message) {
				if (args.length == 1) {
					addChat(ChatEnum.NOTIFY, getCommand() + " clear | clear all yo friends.");
					addChat(ChatEnum.NOTIFY, getCommand() + " list | lists all your friends.");
					addChat(ChatEnum.NOTIFY, getCommand() + " <name> | to quickly toggle a friend.");
					addChat(ChatEnum.NOTIFY, getCommand() + " add <name> [alias] | add a friend with a specific alias.");
					addChat(ChatEnum.NOTIFY, getCommand() + " rem <name/alias> | removes a friend from you friendlist.");
					return;
				}
				if (args[1].toLowerCase().equals("clear")) {
					friendList.clear();
					addChat(ChatEnum.NOTIFY, "Poof, all yo friends gone now nigguh.");
				} else if (args[1].toLowerCase().equals("list")) {
					if (!friendList.isEmpty()) {
						String names = "";
						for (Friend f : friendList) {
							names += f.getName() + ", ";
						}
						names = names.substring(0, names.length() - 2);
						addChat(ChatEnum.NOTIFY, friendList.size() + " friends listed: " + names);
					} else {
						addChat(ChatEnum.ERROR, "You have no friends.");
					}
				} else if (args[1].toLowerCase().equals("add")) {
					if (args.length > 2) {
						String username = args[2];
						String alias = args[2];
						if (args.length > 3) {
							alias = "";
							for (int i = 3; i < args.length; i++) {
								alias += args[i] + " ";
							}
							alias = alias.substring(0, alias.length() - 1);
						}

						Friend isFriend = getFriend(username);
						if (isFriend != null) {
							friendList.remove(isFriend);
							friendList.add(new Friend(username, alias));
							addChat(ChatEnum.NOTIFY, "Changed friend \"" + username + "\", to the alias \"" + alias + "\"!");
						} else {
							friendList.add(new Friend(username, alias));
							addChat(ChatEnum.NOTIFY, "Added friend \"" + username + "\" with the alias of \"" + alias + "\"!");
						}
					} else {
						addChat(ChatEnum.ERROR, "Invalid Syntax!");
					}
				} else if (args[1].toLowerCase().equals("rem") || args[1].toLowerCase().equals("remove") || args[1].toLowerCase().equals("del") || args[1].toLowerCase().equals("delete")) {
					if (args.length > 2) {
						String removeName = args[2];
						if (args.length > 3) {
							removeName = "";
							for (int i = 2; i < args.length; i++) {
								removeName += args[i] + " ";
							}
							removeName = removeName.substring(0, removeName.length() - 1);
						}

						if (friendList.contains(getFriend(removeName))) {
							friendList.remove(getFriend(removeName));
							addChat(ChatEnum.NOTIFY, "Removed friend \"" + removeName + "\"!");
						} else {
							addChat(ChatEnum.NOTIFY, "You're not friends with \"" + removeName + "\"!");
						}
					} else {
						addChat(ChatEnum.ERROR, "Invalid Syntax!");
					}
				} else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("rem")) {
					for (EntityPlayer player : (List<EntityPlayer>) getWorld().playerEntities) {
						if (player.getCommandSenderName().equalsIgnoreCase(args[1])) {
							if (getFriend(player.getCommandSenderName()) == null) friendList.add(new Friend(player.getCommandSenderName(), player.getCommandSenderName()));
							else friendList.remove(getFriend(player.getCommandSenderName()));
						}
					}
					if (getFriend(args[1]) == null) friendList.add(new Friend(args[1], args[1]));
					else friendList.remove(getFriend(args[1]));
					addChat(ChatEnum.NOTIFY, getFriend(args[1]) == null ? "Removed \'" + args[1] + "\' from your friendist" : "Added \'" + args[1] + "\' to your friendist");

				}
			}
		});
	}

	@Override
	public boolean onDisable() {
		return false;
	}

	@ETarget(eventClasses = { EventMousePresses.class })
	public void onEvent(EventMousePresses event) {
		EventMousePresses clicked = (EventMousePresses) event;
		if (clicked.getState().equals(EventMousePresses.State.ONPRESS) && middleMouseFriends) {
			if (clicked.getMouseCode() == 2) {
				if (GameUtil.getEntityOnMouseCurser(5) != null) {
					Entity entity = GameUtil.getEntityOnMouseCurser(5);
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if (getFriend(player.getCommandSenderName()) == null) friendList.add(new Friend(player.getCommandSenderName(), player.getCommandSenderName()));
						else friendList.remove(getFriend(player.getCommandSenderName()));
					}
				}
			}
		}
	}

	public static boolean isFriend(String name) {
		for (Friend f : friendList) {
			if (f.getName().toLowerCase().equals(Colors.stripColor(name.toLowerCase()))) { return true; }
		}
		return false;
	}

	public static Friend getFriend(String name) {
		for (Friend friend : friendList) {
			if (friend.getName().equalsIgnoreCase(name)) { return friend; }
		}
		for (Friend friend : friendList) {
			if (friend.getAlias().equalsIgnoreCase(name)) { return friend; }
		}
		return null;
	}

	public static boolean sameTeam(EntityLivingBase entity) {
		if (getMod(FriendManager.class).teams) {
			if (entity.isOnSameTeam(getPlayer())) { return true; }
			return false;
		} else {
			return false;
		}
	}

	private String SAVINGFRIENDS = "Saved_Friends", MIDDLEMOUSE = "MiddleMouseFriends", TEAMS = "Teams", FRIENDS = "Friends", NAMEPROTECT = "Name Protect";

	List<Value> values = Arrays.asList(new Value[] { new Value(MIDDLEMOUSE, false, true), new Value(TEAMS, false, true), new Value(NAMEPROTECT, false, true), new Value(SAVINGFRIENDS, friendList) });
	private Value friendsDisplay = new Value(FRIENDS, false, new ArrayList<Value>(), ValueType.DISPLAYLIST);

	@Override
	public List<Value> getValues() {
		List<Value> tempList = new ArrayList<Value>();
		tempList.addAll(values);

		friendsDisplay.getOtherValues().clear();
		List<Value> friendSList = new ArrayList<Value>();
		for (Friend f : friendList) {
			friendsDisplay.getOtherValues().add(new Value(f.getName(), false, false));
		}
		tempList.add(friendsDisplay);
		return tempList;
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(NAMEPROTECT)) {
			return nameProtect;
		} else if (n.equals(TEAMS)) {
			return teams;
		} else if (n.equals(MIDDLEMOUSE)) {
			return middleMouseFriends;
		} else if (n.equals(SAVINGFRIENDS)) {
			String s = ",";
			for (Friend friend : friendList) {
				String friendData = friend.getName() + ";" + friend.getAlias();
				s += friendData + ",";
			}
			return s;
		} else {
			return false;
		}
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(NAMEPROTECT)) {
			nameProtect = (Boolean) v;
		} else if (n.equals(TEAMS)) {
			teams = (Boolean) v;
		} else if (n.equals(MIDDLEMOUSE)) {
			middleMouseFriends = (Boolean) v;
		} else if (n.equals(SAVINGFRIENDS)) {
			friendList.clear();
			String[] obj = String.valueOf(v).split(",");
			for (String s : obj) {
				if (!s.equals("")) {
					try {
						String[] friendData = s.split(";");
						Friend newFriend = new Friend(friendData[0], friendData[1]);
						friendList.add(newFriend);
					} catch (Exception oi) {
						break;
					}
				}
			}
		}
	}
}
