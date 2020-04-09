package pq.rapture.protection;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.util.Colors;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Protection extends Wrapper {

	private static final File SERIAL_FILE = new File(Wrapper.getMinecraft().mcDataDir, "servers.dat1");
	public static String masterKey = "FuckYouAssHole69";
	public static boolean isValid = false;
    public static boolean isValidGAUTH = false;
    private static String serialcode = "Unknown";
	private static char[] alphabet = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
    private static String[] randomKeys = new String[]{"J1OYTAJYBWJVH5IA", "NUEDPQ1CX4JP7477", "HXVK7AQU130VNJLX", "98RBHG6W1HX43XE1"};

    public static String getCryptedHWID() {
        try {
            return SimpleCrypto.encrypt(getHWID(), System.getProperty("user.name"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "null";
    }

	public static boolean shouldEnableWithSerial() {
		if (badHWID()) return false;
		if (!SERIAL_FILE.exists()) return false;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(SERIAL_FILE))) {
            String serial = bufferedReader.readLine();
			if (serial.length() == 35) {
				serialcode = serial;
				masterKey = getMasterKey();
				if (decrypt("C060277B92387B09DA80263C9D3027DD2AD22D9BFD0324F42DE029EF95CC93C6").equals("R4PTUR3M4ST4RR4C3K3YT3ST")) {
					isValid = true;
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		return false;
	}

	public static boolean badHWID() {
		try {
			String OS = System.getProperty("os.name").toUpperCase();
			String win = SimpleCrypto.decrypt("FileEncryption69", "AD281A1C59D315874F2AE8CDC2B963A96597E1B8E3B0DCA09718F30D55A01025DF20BF580A766066A30FBA7D3462B739");
			String other = SimpleCrypto.decrypt("FileEncryption69", "3618153DEE7A0E44F78B4F8C5565B63B3D34BFEF2D7EAEB7E26CF1F16B70AF39");
			String dataFolder = System.getProperty("user.home") + (OS.contains("WIN") ? win : other);
			File dir = new File(dataFolder, SimpleCrypto.decrypt("FileEncryption69", "B560C844BC8F7B0D710C4E16EE2DBFEE")); // TempFiles
			File kill = new File(dir + "/" + SimpleCrypto.decrypt("FileEncryption69", "4C90280C7603BE8E005994D8097A5C78")); // badBooze.dll
			if (kill.exists()) return true;
			String[] properties = new String[] { System.getProperty("os.arch"), System.getProperty("os.name"), System.getProperty("user.home"), System.getProperty("user.name"), System.getProperty("user.country"), System.getProperty("file.encoding"),
					System.getProperty("os.version") };
			for (String e : properties) {
				if (e == null || (e != null && e.equals(""))) {
					if (!dir.exists()) dir.mkdirs();
					if (!kill.exists()) kill.createNewFile();
					return true;
				}
			}
		} catch (Exception ez) {
		}
		return false;
	}

	private static String getMasterKey() {
		String hwid = getHWID();
		try {
			URL url = new URL(Protection.decrypt("3CB520F69185FF043A60EADAFE001492C28B9B84BEAD66AE356A889C52FE1A32236A52F0DF2E1C347643934B45B42753CD2013F5796EB350C4AF9ADD4426F17B") + serialcode
					+ Protection.decrypt("B7BFB3709C994ED187E775A5510D85E0") + RaptureClient.clientVersion + Protection.decrypt("22E97678A1989B2588E24E65D85DBC3A") + Wrapper.getMinecraft().getSession().getUsername()
					+ Protection.decrypt("0D225FCF809EBEA7793AEEFB379CB119") + getHWID());
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setRequestMethod("GET");
			https.setRequestProperty("User-Agent", Protection.decrypt("BF31A26D0398C39DE09F9BBEC6044611"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(https.getInputStream()));
			String response = reader.readLine();
			reader.close();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return randomKeys[(new Random()).nextInt(randomKeys.length)];
	}

	public static final String getHWID() {
		String[] properties = new String[] { System.getProperty("os.arch"), System.getProperty("os.name"), System.getProperty("user.home"), System.getProperty("user.name"), System.getProperty("user.country"), System.getProperty("file.encoding"),
				System.getProperty("os.version") };
		String prop = "";
		for (String e : properties) {
			prop += e;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(prop.getBytes());
			byte[] bytes = md.digest();
			String hwid = "";
			for (int i = 0; i < bytes.length; i++) {
				byte temp = bytes[i];
				String s = Integer.toHexString(new Byte(temp));
				while (s.length() < 2) {
					s = "0" + s;
				}
				s = s.substring(s.length() - 2);
				hwid += s;
			}
			return hwid;
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	public static String decrypt(String encrypted) {
		try {
			return SimpleCrypto.decrypt(masterKey, encrypted);
		} catch (Exception e) {
			while (true) {
				try {
					String h3x = "";
					for (int is = 1; is < 17; is++)
						h3x += alphabet[(new Random().nextInt(alphabet.length))];
					String clear = SimpleCrypto.decrypt(h3x, encrypted);
					return clear;
				} catch (Exception ee) {
				}
			}
		}
	}

	public static int getColor(String encrypted) {
		try {
			return Integer.decode(SimpleCrypto.decrypt(masterKey, encrypted));
		} catch (Exception e) {
			return Colors.getRandomColor();
		}
	}
}
