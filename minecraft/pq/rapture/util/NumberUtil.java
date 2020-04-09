package pq.rapture.util;

import java.util.ArrayList;

import net.minecraft.util.MathHelper;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.render.tab.TabGUI.TabGuiItem;

import com.google.gson.JsonElement;

public class NumberUtil {

	public static Object increase(TabGuiItem t) {
		HasValues hep = t.editableProperties;
		Value val = t.value;
		Object e = hep.getValue(val.getName());

		if (Integer.class.isAssignableFrom(e.getClass())) {
			int value = (int) e;
			if (value < (int) val.getMax()) return value + MathHelper.floor_float(val.getIncreasement());
		} else if (Float.class.isAssignableFrom(e.getClass())) {
			float value = (float) e;
			if (value < (float) val.getMax()) return value + val.getIncreasement();
		} else if (Double.class.isAssignableFrom(e.getClass())) {
			double value = (double) e;
			if (value < (double) val.getMax()) return value + val.getIncreasement();
		}

		return e;
	}

	public static Object decrease(TabGuiItem t) {
		HasValues hep = t.editableProperties;
		Value val = t.value;
		Object e = hep.getValue(val.getName());

		if (Integer.class.isAssignableFrom(e.getClass())) {
			int value = (int) e;
			if (value > (int) val.getMin()) return value - MathHelper.floor_float(val.getIncreasement());
		} else if (Float.class.isAssignableFrom(e.getClass())) {
			float value = (float) e;
			if (value > (float) val.getMin()) return value - val.getIncreasement();
		} else if (Double.class.isAssignableFrom(e.getClass())) {
			double value = (double) e;
			if (value > (double) val.getMin()) return value - val.getIncreasement();
		}

		return e;
	}

	public static boolean contains(ArrayList<Integer> list, int e) {
		for(int i : list) {
			if (i == e) return true;
		}
		return false;
	}

	public static boolean isInteger(String integer) {
		try {
			Integer.parseInt(integer);
		} catch (NumberFormatException exception) {
			return false;
		}
		return true;
	}

	public static Object getValue(JsonElement jsonElement) {
		Object obj = null;
		try {
			obj = jsonElement.getAsInt();
			if (Integer.class.isAssignableFrom(obj.getClass())) { return (int) obj; }
		} catch (Exception e) {
			try {
				obj = jsonElement.getAsDouble();
				if (Double.class.isAssignableFrom(obj.getClass())) { return (double) obj; }
			} catch (Exception e2) {
				try {
					obj = jsonElement.getAsFloat();
					if (Float.class.isAssignableFrom(obj.getClass())) { return (float) obj; }
				} catch (Exception e3) {
					String ss = jsonElement.getAsString();
					if (ss.equalsIgnoreCase("true"))
						return Boolean.TRUE;
					else if (ss.equalsIgnoreCase("false"))
						return Boolean.FALSE;
					else
						return ss;
				}
			}
		}
		return jsonElement.getAsString();
	}

	public static boolean isBoolean(HasValues values, Value value) {
		try {
			if (values.getValue(value.getName()).getClass().isAssignableFrom(Boolean.class)) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static boolean getBoolean(TabGuiItem tab) {
		return (boolean) tab.editableProperties.getValue(tab.value.getName());
	}

	public static double toDouble(Object currentValue) {
		try {
			return (Double) currentValue;
		} catch (Exception e) {
			try {
				return (Float) currentValue;
			} catch (Exception e2) {
				try {
					return (Integer) currentValue;
				} catch (Exception e3) {
				}
			}
			return 0.0D;
		}
	}
	
	public static double getWidth(double width, double min, double max, double value) {
		if (value >= max) {
			value = max;
		} else if (value <= min) {
			value = min;
		}
		double w = width * (value - min);
		double f = w / (max - min);
		return f;
	}

	public static Object getValueForClickGUI(double width, double min, double max, double newValue, Class objectClass) {
		if (newValue >= max) {
			newValue = max;
		} else if (newValue <= 0) {
			newValue = 0;
		}
		double f = (width * newValue) / max;
		if (Integer.class.isAssignableFrom(objectClass)) { return MathHelper.floor_double(f + min); }
		if (Float.class.isAssignableFrom(objectClass)) { return (float) (Math.round((Double) (f + min) * 100000) / 100000.0D); }
		return f + min;
	}

	public static double getDistance(float left, float right) {
		return left > right ? left - right : right - left;
	}
}
