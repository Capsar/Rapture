package pq.rapture.module.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.RaptureClient.KeyBind;
import pq.rapture.module.*;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.protection.SimpleCrypto;
import pq.rapture.util.NumberUtil;

public class ModuleManager {

	public static ArrayList<Module> modules = new ArrayList<Module>();

	public static void initModules() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}

		modules.addAll(Arrays.asList(new ArmorStatus(), new AutoPot(), new AutoSoup(), new Blink(), new BlockESP(), new ChestStealer(), new CreeperFlinter(), new Criticals(), new FastConsume(), new Firion(), new Fly(), new Freecam(), new Glide(),
				new Jesus(), new KillAura(), new NameTags(), new NoSlowDown(), new Phase(), new PotionStatus(), new Sneak(), new Speed(), new SpeedMine(), new Step(), new ReverseStep(), new Tracers(), new Sprint(), new FastPlace(), new WayPoints(),
				new FastLadder(), new ClearLiquid(), new Velocity(), new ArmorSwitcher(), new CivBreak(), new CheckerClimb(), new NarniaPhase(), new NoRotationSet(), new SkipPhase(), new Fullbright(), new ThrowBadPotions(), new ThrowHealPotions(),
				new Triggerbot(), new Ghost(), new AutoFarm(), new FriendManager(), new InventoryPlus(), new HUDHelper(), new Dance(), new OpenClickGUI(), new Rewind()));
	}

	public static ArrayList<Module> getModules(Type type) {
		ArrayList<Module> moduleeers = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.getType() == type) {
				moduleeers.add(m);
			}
		}
		return moduleeers;
	}

	public static JsonArray safeModules(JsonObject root) {
		JsonArray modules = new JsonArray();
		for (Module m : ModuleManager.modules) {
			JsonObject moduleObject = new JsonObject();
			JsonObject dataObject = new JsonObject();
			dataObject.addProperty("KEY", m.getKey());
			dataObject.addProperty("STATE", m.getState());
			dataObject.addProperty("COLOR", m.getColor());
			dataObject.addProperty("DESC", m.getDescription());
			dataObject.addProperty("TYPE", Type.getText(m.getType()));
			dataObject.addProperty("VISIBLE", m.isVisible());
			if (m instanceof HasValues) {
				HasValues hep = (HasValues) m;
				JsonObject newDataObject = new JsonObject();
				for (Value v : hep.getValues()) {
					newDataObject.addProperty(v.getName(), String.valueOf(hep.getValue(v.getName())));
				}
				dataObject.add("VALUES", newDataObject);
			}
			moduleObject.add(m.getName().toLowerCase(), dataObject);
			modules.add(moduleObject);
		}
		return modules;
	}

	public static void loadModules() {
		try (BufferedReader reader = new BufferedReader(new FileReader(RaptureClient.CONFIG_LOCATION))) {
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(reader, JsonObject.class);
			JsonArray modulesArray = root.get("modules").getAsJsonArray();
			for (Object moduleObject : modulesArray) {
				JsonObject moduleCup = (JsonObject) moduleObject;
				for (Map.Entry<String, JsonElement> entry : moduleCup.entrySet()) {
					Module mod = null;
					for (Module m : modules) {
						if (m.getName().equalsIgnoreCase(entry.getKey())) {
							mod = m;
							break;
						}
					}
					if (mod != null) {
						JsonObject settings = entry.getValue().getAsJsonObject();
						for (Map.Entry<String, JsonElement> setting : settings.entrySet()) {
							switch (setting.getKey()) {
							case "KEY":
								mod.setKey(setting.getValue().getAsString());
								break;
							case "STATE":
								if (setting.getValue().getAsBoolean()) mod.toggle(true);
								break;
							case "VISIBLE":
								mod.setVisible(setting.getValue().getAsBoolean());
								break;
							case "VALUES":
								if (mod instanceof HasValues) {
									HasValues hep = (HasValues) mod;
									JsonObject values = setting.getValue().getAsJsonObject();
									for (Map.Entry<String, JsonElement> value : values.entrySet()) {
										hep.setValue(value.getKey(), NumberUtil.getValue(value.getValue()));
									}
								}
								break;
							}
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadExtraModules() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists() || !good.exists()) return;
		} catch (Exception e) {
		}
	}

	public static void keyPressed(KeyBind key) {
		for (Module m : modules) {
			if (m.getKey().equalsIgnoreCase(Keyboard.getKeyName(key.getCode()))) m.toggle(true);
		}
	}

	public static void mousePressed(KeyBind key) {
		for (Module m : modules) {
			if (m.getKey().equalsIgnoreCase(Mouse.getButtonName(key.getCode()))) m.toggle(true);
		}
	}
}
