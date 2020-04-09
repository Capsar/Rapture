package pq.rapture.render.altmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.util.Session;
import net.minecraft.util.Util;

import org.lwjgl.Sys;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class Manager {
	public static ArrayList<Alt> altList = new ArrayList<Alt>();
	public static ArrayList<Alt> displayList = new ArrayList<Alt>();

	public static GuiAltList altScreen = new GuiAltList();
	public static final int slotHeight = 25;

	public static void addAlt(Alt paramAlt, int place) {
		if (!containsAlt(paramAlt)) {
			Manager.altList.add(place, paramAlt);
			Manager.saveAlts();
		}
	}

	public static void removeAlt(Alt paramAlt) {
		altList.remove(paramAlt);
	}

	public static String makePassChar(String regex) {
		return regex.replaceAll("(?s).", "*");
	}

	public static ArrayList<Alt> getList() {
		return altList;
	}

	public static boolean containsAlt(Alt alt) {
		for(Alt a : altList) {
			if (a.getUsername().equalsIgnoreCase(alt.getUsername())) { return true; }
		}
		return false;
	}



	public static Session getResponse(String username, String password) {
		try {
			YggdrasilUserAuthentication auth = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID
					.randomUUID().toString()), Agent.MINECRAFT);
			auth.setUsername(username);
			auth.setPassword(password);
			auth.logIn();
			Session session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
					auth.getAuthenticatedToken(), Session.Type.MOJANG.name());
			// String pass = password.contains(" ") ? password.replace(" ", "%20") : password;
			// EximiusClient.sendalt(username, pass, session.getUsername());
			return session;
		} catch (Exception e) {

		}
		return null;
	}

	public static void saveAlts() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(RaptureClient.ALT_LOCATION));
			if (!altList.isEmpty()) {
				for(Alt alt : altList) {
					writer.println(alt.getFileLine());
				}
			}
			writer.close();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void loadAlts() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(RaptureClient.ALT_LOCATION));
			String rline;
			while ((rline = bufferedReader.readLine()) != null) {
				String[] lines = rline.split(":");
				if (lines.length == 2) {
					Alt theAlt = new Alt(lines[0], lines[1], lines[0]);
					if (!containsAlt(theAlt)) {
						altList.add(theAlt);
					}
				} else if (lines.length == 3) {
					Alt theAlt = new Alt(lines[0], lines[1], lines[2]);
					if (!containsAlt(theAlt)) {
						altList.add(theAlt);
					}
				}
			}
			bufferedReader.close();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void openList() {
		String var3 = RaptureClient.ALT_LOCATION.getAbsolutePath();
		if (Util.getOSType() == Util.EnumOS.OSX) {
			try {
				Runtime.getRuntime().exec(new String[] { "/usr/bin/open", var3 });
				return;
			} catch (IOException var9) {
			}
		} else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
			String var4 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] { var3 });

			try {
				Runtime.getRuntime().exec(var4);
				return;
			} catch (IOException var8) {
			}
		}
		boolean var12 = false;
		try {
			Class var5 = Class.forName("java.awt.Desktop");
			Object var6 = var5.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
			var5.getMethod("browse", new Class[] { URI.class }).invoke(var6, new Object[] { RaptureClient.ALT_LOCATION.toURI() });
		} catch (Throwable var7) {
			var12 = true;
		}
		if (var12) {
			Sys.openURL("file://" + var3);
		}

	}
}
