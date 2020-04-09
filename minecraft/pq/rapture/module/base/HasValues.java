package pq.rapture.module.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface HasValues {

	public class Value {

		public Value(String name, Number min, Number max, float increasement) {
			assert min.getClass().equals(max.getClass());
			this.increasement = increasement;
			this.name = name;
			this.min = min;
			this.max = max;
		}

		public Value(String name, Boolean min, Boolean max) {
			assert min.getClass().equals(max.getClass());
			this.name = name;
			this.min = min;
			this.max = max;
		}

		public Value(String name, Collection list) {
			this.name = name;
			this.list = list;
			this.type = ValueType.SAVING;
		}

		public Value(String name, boolean open, List<Value> values, ValueType type) {
			this.name = name;
			this.isOpen = open;
			this.type = type;
			for (Value v : values) {
				otherValues.add(v);
			}
		}
		
		private final String name;
		private float increasement = 0.1F;
		private ValueType type = ValueType.NORMAL;
		private Object min, max;

		private Collection list;

		private ArrayList<Value> otherValues = new ArrayList<Value>();
		private boolean isOpen = false;

		public boolean isOpen() {
			return isOpen;
		}

		public void setOpen(boolean open) {
			this.isOpen = open;
		}

		public ValueType getType() {
			return type;
		}
		
		public float getIncreasement() {
			return increasement;
		}

		public String getName() {
			return name;
		}

		public Object getMin() {
			return min;
		}

		public Object getMax() {
			return max;
		}

		public Class getVClass() {
			return max.getClass();
		}

		public ArrayList<Value> getOtherValues() {
			return otherValues;
		}

		public enum ValueType {
			NORMAL, MODE, DISPLAYLIST, SAVING;
		}
	}
	
	List<Value> getValues();

	Object getValue(String n);

	void setValue(String n, Object v);

}
