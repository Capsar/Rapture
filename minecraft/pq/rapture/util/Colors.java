package pq.rapture.util;

import java.util.Random;
import java.util.regex.Pattern;

import pq.rapture.protection.SimpleCrypto;

public class Colors {
	public static String BLACK = "\2470";
	public static String DARK_BLUE = "\2471";
	public static String DARK_GREEN = "\2472";
	public static String DARK_AQUA = "\2473";
	public static String DARK_RED = "\2474";
	public static String DARK_PURPLE = "\2475";
	public static String GOLD = "\2476";
	public static String GRAY = "\2477";
	public static String DARK_GRAY = "\2478";
	public static String BLUE = "\2479";
	public static String GREEN = "\247a";
	public static String AQUA = "\247b";
	public static String RED = "\247c";
	public static String LIGHT_PURPLE = "\247d";
	public static String YELLOW = "\247e";
	public static String WHITE = "\247f";
	public static String RANDOM = "\247k";
	public static String BOLD = "\247l";
	public static String STRIKETHROUGH = "\247m";
	public static String UNDERLINE = "\247n";
	public static String ITALIC = "\247o";
	public static String RESET = "\247r";

	public static char[] COLOR_ARRAY = { 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf("\247") + "[A-F0-9KLMNOR]");

	public static String stripColor(final String input) {
		if (input == null) { return null; }

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static int getRandomColor() {
		char[] letters = "012345678".toCharArray();
		String color = "0x";
		for(int i = 0; i < 6; i++)
			color += letters[new Random().nextInt(letters.length)];
		return Integer.decode(color); 
	}

}