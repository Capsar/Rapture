package pq.rapture.module;

import java.util.Arrays;
import java.util.List;

import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.protection.Protection;

public class HUDHelper extends Module implements HasValues {

	public static boolean fancyArray = false;
	public static boolean arrayList = true;
	public static boolean coords = true;
	public static boolean watermark = true;
	public static boolean TTFFont = false;

	public HUDHelper() {
		super(Protection.decrypt("0BD6C78F4A97E2888A0667EBEB987546"), new String[] {}, Protection.decrypt("1A69DF05DEA77161451B624B80F0B92C7862B8A12678D46296C7DB5CF40952A4"), Type.RENDER, "NONE", 0xFF00CBF3);
		setVisible(false);
	}

	private final static String FANCY = Protection.decrypt("AE4CEF448CA1C98E3064E2949C02D57415319490761930059D7FEF3DD60AA33A");
	private final static String ARRAYLIST = Protection.decrypt("88501E54E51954C52C38947D0702B295");
	private static final String COORDS = Protection.decrypt("9E606E454BE625CA75465E9909470F30");
	private final static String WATERMARK = Protection.decrypt("C18DB45FFB5DB96CE13D307CF2C4426B");
	private final static String TTFFONT = Protection.decrypt("A4F9CD4636D337A1110C26020EFB6AC3");
	private static final Value[] PARAMETERS = new Value[] { new Value(FANCY, false, true), new Value(ARRAYLIST, false, true),
			new Value(COORDS, false, true), new Value(WATERMARK, false, true), new Value(TTFFONT, false, true) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(FANCY))
			return fancyArray;
		else if (n.equals(ARRAYLIST))
			return arrayList;
		else if (n.equals(COORDS))
			return coords;
		else if (n.equals(WATERMARK))
			return watermark;
		else if (n.equals(TTFFONT))
			return TTFFont;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(FANCY))
			fancyArray = (boolean) v;
		else if (n.equals(ARRAYLIST))
			arrayList = (boolean) v;
		else if (n.equals(COORDS))
			coords = (boolean) v;
		else if (n.equals(WATERMARK))
			watermark = (boolean) v;
		else if (n.equals(TTFFONT))
			TTFFont = (boolean) v;
	}

}