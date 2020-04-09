package pq.rapture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.lwjgl.input.Keyboard;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;
import pq.rapture.protection.Protection;
import pq.rapture.protection.SimpleCrypto;
import pq.rapture.render.font.RaptureFontRenderer;
import pq.rapture.render.tab.TabGUI;
import pq.rapture.rxdy.EventKeyPresses;
import pq.rapture.rxdy.EventKeyPresses.State;
import pq.rapture.rxdy.EventMousePresses;
import pq.rapture.rxdy.event.EventManager;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.TimeHelper;

import java.io.*;
import java.util.ArrayList;

public class RaptureClient {

	public static final String clientName = Protection.decrypt("25CC2618615B6B4B9223765CCD024A67");
    public static final String clientVersion = "1.2NHANCET";
    public static final String clientAuthor = "Capsar & Rudy + Friendly";
    public static final File SAVE_LOCATION = Wrapper.getMinecraft().mcDataDir;
    public static final File ASSETS_LOCATION = new File(SAVE_LOCATION, "assets");
    public static final File INDEXES_LOCATION = new File(ASSETS_LOCATION, "indexes");
    public static final File CONFIG_LOCATION = new File(INDEXES_LOCATION, "rconfig.json");
    public static final File ALT_LOCATION = new File(INDEXES_LOCATION, "ralts.json");
    public static String username = "not avaliable";
    public static String clientPrefix = "..";
	public static int enableKey1 = Keyboard.KEY_RCONTROL, enableKey2 = Keyboard.KEY_UP;
	public static boolean isEnabled = false;

	private static int delay = 0;
    private static ArrayList<KeyBind> pressedKeys = new ArrayList<KeyBind>();
    private static ArrayList<KeyBind> allKeys = new ArrayList<KeyBind>();
    private static ArrayList<KeyBind> pressedButtons = new ArrayList<KeyBind>();
    private static ArrayList<KeyBind> mouseButtons = new ArrayList<KeyBind>();
    private static TimeHelper timeHelp = new TimeHelper();

	public RaptureClient() {
		try {
			try {
				String OS = System.getProperty("os.name").toUpperCase();
                username = System.getProperty("user.name");
                String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
				String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
				String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
				File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
				File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
				File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
				if (kill.exists()) return;
				if (!good.exists()) {
					dir.mkdirs();
					good.createNewFile();
				}

				if (!INDEXES_LOCATION.exists()) {
					INDEXES_LOCATION.mkdirs();
				}
				if (!CONFIG_LOCATION.exists()) {
					CONFIG_LOCATION.createNewFile();
				}
				if (!ALT_LOCATION.exists()) {
					ALT_LOCATION.createNewFile();
				}
			} catch (Exception files) {
				return;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) isEnabled = true;
			EventManager.getInstance().register(this, EventKeyPresses.class);
			EventManager.getInstance().register(this, EventMousePresses.class);
			ModuleManager.initModules();
			Wrapper.getCommandManager().loadCommands();
			Wrapper.setTabGui(new TabGUI());
			Wrapper.setFont(RaptureFontRenderer.createFont());
			if (isEnabled) loadClient();
			for(int i = 0; i < 260; i++)
				allKeys.add(new KeyBind(i, new TimeHelper()));
			for(int i = 0; i < 20; i++)
				mouseButtons.add(new KeyBind(i, new TimeHelper()));
			try (BufferedReader reader = new BufferedReader(new FileReader(RaptureClient.CONFIG_LOCATION))) {
				Gson gson = new Gson();
				JsonObject root = gson.fromJson(reader, JsonObject.class);
				JsonObject client = root.get("client").getAsJsonObject();
				RaptureClient.enableKey1 = client.get("ENABLEKEY1").getAsInt();
				RaptureClient.enableKey2 = client.get("ENABLEKEY2").getAsInt();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						String OS = System.getProperty("os.name").toUpperCase();
						String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A9F2279DE4DA9757243AAD09B059BF5072");
						String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
						String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
						File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
						File good = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "CEDDCD460DEC39AB865CF71CE7EDC9FF")); // boozeloaded.dll
						if (good.exists()) good.delete();
					} catch (Exception ez) {
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getName() {
		return clientName;
	}

	public static String getVersion() {
		return clientVersion;
	}

	public static String getAuthor() {
		return clientAuthor;
	}

	public static String getPrefix() {
		return clientPrefix;
	}

    public static void loadClient() {
        if (!Protection.decrypt("C060277B92387B09DA80263C9D3027DD2AD22D9BFD0324F42DE029EF95CC93C6").equals("R4PTUR3M4ST4RR4C3K3YT3ST"))
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(RaptureClient.CONFIG_LOCATION))) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            JsonObject client = root.get("client").getAsJsonObject();
            RaptureClient.clientPrefix = client.get("CHATPREFIX").getAsString();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ModuleManager.loadModules();
    }

    public static void unloadClient() {
        safeClient();
        for (Module m : ModuleManager.modules) {
            if (m.getState()) {
                m.toggle(false);
            }
		}
	}

    public static void safeClient() {
        if (!Protection.decrypt("C060277B92387B09DA80263C9D3027DD2AD22D9BFD0324F42DE029EF95CC93C6").equals("R4PTUR3M4ST4RR4C3K3YT3ST"))
			return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RaptureClient.CONFIG_LOCATION))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject root = new JsonObject();
            JsonObject client = new JsonObject();
            client.addProperty("NAME", RaptureClient.getName());
            client.addProperty("AUTHOR", RaptureClient.getAuthor());
            client.addProperty("VERSION", RaptureClient.getVersion());
            client.addProperty("CHATPREFIX", RaptureClient.getPrefix());
            client.addProperty("ENABLEKEY1", RaptureClient.enableKey1);
            client.addProperty("ENABLEKEY2", RaptureClient.enableKey2);
            root.add("client", client);

            root.add("modules", ModuleManager.safeModules(root));
            writer.write(gson.toJson(root));
            writer.close();
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

    @ETarget(eventClasses = {EventMousePresses.class})
    public void mousePressed(EventMousePresses mouse) {
        KeyBind button = mouseButtons.get(mouse.getMouseCode());
        if (mouse.getState().equals(EventMousePresses.State.ONPRESS)) {
            if (Keyboard.isKeyDown(button.getCode()) && !pressedButtons.contains(button)) {
                pressedButtons.add(button);
                button.getTime().reset();
            }
        } else if (mouse.getState().equals(EventMousePresses.State.UNPRESS)) {
            if (pressedButtons.contains(button)) {
                pressedButtons.remove(button);
            }
        }
        if (!isEnabled) return;
        if (mouse.getState().equals(EventMousePresses.State.ONPRESS)) {
            ModuleManager.mousePressed(button);
        }
    }

    @ETarget(eventClasses = {EventKeyPresses.class})
    public void keyPressed(EventKeyPresses press) {
        KeyBind key = allKeys.get(press.getKeyCode());
        if (press.getState().equals(State.ONPRESS)) {
            if (Keyboard.isKeyDown(key.getCode()) && !pressedKeys.contains(key)) {
                pressedKeys.add(key);
                key.getTime().reset();
            }
            if (Keyboard.isKeyDown(enableKey1) && key.getCode() == enableKey2) {
                isEnabled = !isEnabled;
                if (isEnabled) {
                    loadClient();
                } else {
                    unloadClient();
                }
            }
        } else if (press.getState().equals(State.UNPRESS)) {
            if (pressedKeys.contains(key)) {
                pressedKeys.remove(key);
            }
        }
        if (!isEnabled) return;

        if (press.getState().equals(State.ONPRESS)) {
            Wrapper.getTabGui().keyPressed(key);
            ModuleManager.keyPressed(key);
        } else if (press.getState().equals(State.WHILE)) {
            for (KeyBind keyz : pressedKeys) {
                if (keyz.time.hasDelayRun(500)) {
                    Wrapper.getTabGui().onKeyDown(keyz);
                }
            }
        }
	}

	public class KeyBind {

		private int code;
		private TimeHelper time;

		public KeyBind(int code, TimeHelper time) {
			this.code = code;
			this.time = time;
		}

		public int getCode() {
			return code;
		}

		public TimeHelper getTime() {
			return time;
		}

		public void setTime(TimeHelper time) {
			this.time = time;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + code;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			KeyBind other = (KeyBind) obj;
			if (!getOuterType().equals(other.getOuterType())) return false;
			if (code != other.code) return false;
			return true;
		}

		private RaptureClient getOuterType() {
			return RaptureClient.this;
		}
	}

}
