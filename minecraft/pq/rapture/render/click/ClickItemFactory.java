package pq.rapture.render.click;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import pq.rapture.Wrapper;
import pq.rapture.Wrapper.ChatEnum;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.HasValues.Value.ValueType;
import pq.rapture.module.base.Module;
import pq.rapture.render.GUIUtil;
import pq.rapture.util.FontUtil;
import pq.rapture.util.NumberUtil;

public class ClickItemFactory {

	public static class ClickItem {
		private Module module;
		public boolean showProperties = false;
		private float height;
		private boolean changingKeyBind;

		public ClickItem(Module module, int height) {
			this.module = module;
			this.height = height;
		}

		public Module getModule() {
			return this.module;
		}

		public float getHeight() {
			return height;
		}

		public void drawItem(int mouseX, int mouseY, float height) {
			double moduleWidth = ClickGUI.getInstance().leftBoxWidth;
			double keyBindWidth = ClickGUI.getInstance().editableBoxWidth;

			GUIUtil.drawRect(5, height, moduleWidth, height + this.getHeight(),
					(mouseX > 5 && mouseX < moduleWidth && mouseY < height + this.getHeight() && mouseY > height && mouseY < Wrapper.getScaledResolution().getScaledHeight() && !ClickGUI.getInstance().isDraggingIndicator) ? this.module == ClickGUI.getInstance().foundModule ? 0x8099E9FF : 0x807b7b7b : 0xFF232323);
			FontUtil.drawScaledString(this.getModule().getName(), 10, height + 2, this.getModule().getState() ? this.getModule().getColor() : 0xFFffffff, 1.1F, true);
			if (this.showProperties) {
				GUIUtil.drawRect(moduleWidth + 1, height, moduleWidth + keyBindWidth + 1, height + this.getHeight(), 0xFF232323);
				FontUtil.drawScaledString("Key: ", moduleWidth + 3, height + 3, 0xFFFFFFFF, 0.95F, true);
				FontUtil.drawScaledString(this.changingKeyBind ? "[?]" : "[" + this.module.getKey() + "]", moduleWidth + 1 + (Wrapper.getFontRenderer().getStringWidth("Key: ") * 0.95F), height + 4, 0xFFFFFFFF, 0.8F, true);
			}
		}

		public void handleClick(int mouseX, int mouseY, float height, int type) {
			double moduleWidth = ClickGUI.getInstance().leftBoxWidth;
			double keyBindWidth = ClickGUI.getInstance().editableBoxWidth;
			if (type == 0) {
				if (mouseX > 5 && mouseX < moduleWidth && mouseY < height + this.getHeight() && mouseY > height && mouseY < height + this.getHeight() && !ClickGUI.getInstance().isDraggingIndicator) {
					this.getModule().toggle(true);
				}
				if (this.showProperties && mouseX > moduleWidth && mouseX < moduleWidth + keyBindWidth + 1 && mouseY < height + this.getHeight() && mouseY > height && mouseY < height + this.getHeight() && !ClickGUI.getInstance().isDraggingIndicator) {
					this.changingKeyBind = true;
				}
			} else if (type == 1) {
				if (mouseX > 5 && mouseX < 100 && mouseY < height + this.getHeight() && mouseY > height && mouseY < height + this.getHeight() && !ClickGUI.getInstance().isDraggingIndicator) {
					this.showProperties = !this.showProperties;
				} else {
					this.showProperties = false;
				}
			} else {
				if (this.changingKeyBind) {
					this.getModule().setKey(Mouse.getButtonName(type));
					Wrapper.addChat(ChatEnum.NOTIFY, this.getModule().getName() + " is now bound to " + this.getModule().getKey() + ".");
					this.changingKeyBind = false;
				}
			}
		}

		public void handleDrag(int mouseX, int mouseY, float height) {

		}

		public void handleRelease(int mouseX, int mouseY, float height) {

		}

		public boolean handleKeyPress(int keyCode) {
			if (this.changingKeyBind) {
				this.getModule().setKey(keyCode == 1 ? "NONE" : Keyboard.getKeyName(keyCode));
				Wrapper.addChat(ChatEnum.NOTIFY, this.getModule().getName() + " is now bound to " + this.getModule().getKey() + ".");
				this.changingKeyBind = false;
				return true;
			}
			return false;
		}
	}

	public static class ClickItemSpecial extends ClickItem {

		double firstX = -1, firstY = -1;

		public ClickItemSpecial(Module module, int height) {
			super(module, height);
		}

		public void drawItem(int mouseX, int mouseY, float height) {
			super.drawItem(mouseX, mouseY, height);
			GUIUtil.drawRect(7, height, 0, height + this.getHeight(), this.getModule().getState() ? this.getModule().getColor() : 0xFFFFFFFF);

			if (this.showProperties) {
				HasValues values = (HasValues) this.getModule();
				float xOffset = ClickGUI.getInstance().leftBoxWidth + 1;
				float width = ClickGUI.getInstance().editableBoxWidth;
				for (HasValues.Value value : values.getValues()) {
					if (NumberUtil.isBoolean(values, value) || value.getType() == ValueType.SAVING) continue;
					if (Wrapper.getFontRenderer().getStringWidth(value.getName() + " " + values.getValue(value.getName())) * 0.8 > width)
						width = Wrapper.getFontRenderer().getStringWidth(value.getName() + " " + values.getValue(value.getName())) * 0.84F;
				}
				boolean drawUp = ((height > Wrapper.getScaledResolution().getScaledHeight() / 2) ? true : false);
				float incrementalY = drawUp ? -14 : 14;
				float itemHeight = this.getHeight();

				for (HasValues.Value value : values.getValues()) {
					if (value.getType() == ValueType.SAVING) continue;
					if (NumberUtil.isBoolean(values, value)) {
						boolean valuee = (Boolean) values.getValue(value.getName());
						if (drawUp) {
							GUIUtil.drawRect(xOffset, height + incrementalY, width + xOffset, height + incrementalY + this.getHeight(), 0xFF232323);
							FontUtil.drawScaledString(value.getName(), xOffset + 2, height + 3.5 + incrementalY, valuee ? this.getModule().getColor() : 0xFFFFFFFF, 0.8F, true);
							incrementalY -= itemHeight + 1;
						} else {
							GUIUtil.drawRect(xOffset, height + incrementalY, width + xOffset, height + incrementalY + this.getHeight(), 0xFF232323);
							FontUtil.drawScaledString(value.getName(), xOffset + 2, height + 3.5 + incrementalY, valuee ? this.getModule().getColor() : 0xFFFFFFFF, 0.8F, true);
							incrementalY += itemHeight + 1;
						}
					} else {
						double min = NumberUtil.toDouble(value.getMin());
						double max = NumberUtil.toDouble(value.getMax());
						double valuee = NumberUtil.toDouble(values.getValue(value.getName()));
						double sliderWidth = NumberUtil.getDistance(xOffset + 2, xOffset + width);
						float sliderSize = 8;
						if (drawUp) {
							incrementalY -= sliderSize;
							GUIUtil.drawRect(xOffset, height + incrementalY, width + xOffset, height + incrementalY + itemHeight + sliderSize, 0xFF232323);
							FontUtil.drawScaledString(value.getName() + " " + values.getValue(value.getName()), xOffset + 2, height + 2 + incrementalY, 0xFFffffff, 0.8F, true);

							GUIUtil.drawSexyRect(xOffset + 2, height + incrementalY + 10, width + xOffset - 2, height + incrementalY + itemHeight + sliderSize - 2, 0xFF434343, 0xFF636363);
							GUIUtil.drawRect(xOffset + 2, height + incrementalY + 10, xOffset + (double) NumberUtil.getWidth(sliderWidth, min, max, valuee), height + incrementalY + itemHeight + sliderSize - 2, 0xFF232323);
							incrementalY -= itemHeight + 1;
						} else {
							GUIUtil.drawRect(xOffset, height + incrementalY, width + xOffset, height + incrementalY + itemHeight + sliderSize, 0xFF232323);
							FontUtil.drawScaledString(value.getName() + " " + values.getValue(value.getName()), xOffset + 2, height + 2 + incrementalY, 0xFFffffff, 0.8F, true);

							GUIUtil.drawSexyRect(xOffset + 2, height + incrementalY + 10, width + xOffset - 2, height + incrementalY + itemHeight + sliderSize - 2, 0xFF434343, 0xFF636363);
							GUIUtil.drawRect(xOffset + 2, height + incrementalY + 10, xOffset + (double) NumberUtil.getWidth(sliderWidth, min, max, valuee), height + incrementalY + itemHeight + sliderSize - 2, 0xFF232323);
							incrementalY += itemHeight + sliderSize + 1;
						}
					}
				}
			}
		}

		public void handleClick(int mouseX, int mouseY, float height, int type) {
			super.handleClick(mouseX, mouseY, height, type);
			if (this.showProperties) {
				HasValues values = (HasValues) this.getModule();
				float xOffset = ClickGUI.getInstance().leftBoxWidth + 1;
				float width = ClickGUI.getInstance().editableBoxWidth;
				for (HasValues.Value value : values.getValues()) {
					if (NumberUtil.isBoolean(values, value) || value.getType() == ValueType.SAVING) continue;
					if (Wrapper.getFontRenderer().getStringWidth(value.getName() + " " + values.getValue(value.getName())) * 0.8 > width)
						width = Wrapper.getFontRenderer().getStringWidth(value.getName() + " " + values.getValue(value.getName())) * 0.8F;
				}
				boolean drawUp = ((height > Wrapper.getScaledResolution().getScaledHeight() / 2) ? true : false);
				float incrementalY = drawUp ? -14 : 14;
				float itemHeight = this.getHeight();

				for (HasValues.Value value : values.getValues()) {
					if (value.getType() == ValueType.SAVING) continue;
					if (NumberUtil.isBoolean(values, value)) {
						boolean valuee = (Boolean) values.getValue(value.getName());
						if (GUIUtil.mouseInsideBox(mouseX, mouseY, xOffset, height + incrementalY, width + xOffset, height + incrementalY + this.getHeight())) values.setValue(value.getName(), !valuee);
						if (drawUp) {
							incrementalY -= itemHeight + 1;
						} else {
							incrementalY += itemHeight + 1;
						}
					} else {
						firstX = mouseX;
						firstY = mouseY;
						double min = NumberUtil.toDouble(value.getMin());
						double max = NumberUtil.toDouble(value.getMax());
						double currentValue = NumberUtil.toDouble(values.getValue(value.getName()));
						double sliderWidth = NumberUtil.getDistance(xOffset + 2, xOffset + width);
						float sliderSize = 8;
						if (drawUp) {
							incrementalY -= sliderSize;
							if (GUIUtil.mouseInsideBox(mouseX, mouseY, xOffset + 2, height + incrementalY + 10, width + xOffset - 2, height + incrementalY + itemHeight + sliderSize - 2)) {
								Object valuee = NumberUtil.getValueForClickGUI(max - min, min, width, mouseX - xOffset, value.getVClass());
								values.setValue(value.getName(), valuee);
							}
							incrementalY -= itemHeight + 1;
						} else {
							if (GUIUtil.mouseInsideBox(mouseX, mouseY, xOffset + 2, height + incrementalY + 10, width + xOffset - 2, height + incrementalY + itemHeight + sliderSize - 2)) {
								Object valuee = NumberUtil.getValueForClickGUI(max - min, min, sliderWidth, mouseX - (xOffset + 2), value.getVClass());
								values.setValue(value.getName(), valuee);
							}
							incrementalY += itemHeight + sliderSize + 1;
						}
					}
				}
			}
		}

		public void handleDrag(int mouseX, int mouseY, float height) {
			super.handleDrag(mouseX, mouseY, height);
			if (this.showProperties) {
				HasValues values = (HasValues) this.getModule();
				float xOffset = ClickGUI.getInstance().leftBoxWidth + 1;
				float width = ClickGUI.getInstance().editableBoxWidth;
				for (HasValues.Value value : values.getValues()) {
					if (NumberUtil.isBoolean(values, value) || value.getType() == ValueType.SAVING) continue;
					if (Wrapper.getFontRenderer().getStringWidth(value.getName() + " " + values.getValue(value.getName())) * 0.8 > width)
						width = Wrapper.getFontRenderer().getStringWidth(value.getName() + " " + values.getValue(value.getName())) * 0.8F;
				}
				boolean drawUp = ((height > Wrapper.getScaledResolution().getScaledHeight() / 2) ? true : false);
				float incrementalY = drawUp ? -14 : 14;
				float itemHeight = this.getHeight();

				for (HasValues.Value value : values.getValues()) {
					if (value.getType() == ValueType.SAVING) continue;
					if (NumberUtil.isBoolean(values, value)) {
						if (drawUp) {
							incrementalY -= itemHeight + 1;
						} else {
							incrementalY += itemHeight + 1;
						}
					} else {
						double min = NumberUtil.toDouble(value.getMin());
						double max = NumberUtil.toDouble(value.getMax());
						double currentValue = NumberUtil.toDouble(values.getValue(value.getName()));
						double sliderWidth = NumberUtil.getDistance(xOffset + 2, xOffset + width);
						float sliderSize = 8;
						if (drawUp) {
							incrementalY -= sliderSize;
							if (GUIUtil.mouseInsideBox(firstX, firstY, xOffset + 2, height + incrementalY + 10, width + xOffset - 2, height + incrementalY + itemHeight + sliderSize - 2)) {
								Object valuee = NumberUtil.getValueForClickGUI(max - min, min, width, mouseX - xOffset, value.getVClass());
								values.setValue(value.getName(), valuee);
							}
							incrementalY -= itemHeight + 1;
						} else {
							if (GUIUtil.mouseInsideBox(firstX, firstY, xOffset + 2, height + incrementalY + 10, width + xOffset - 2, height + incrementalY + itemHeight + sliderSize - 2)) {
								Object valuee = NumberUtil.getValueForClickGUI(max - min, min, sliderWidth, mouseX - (xOffset + 2), value.getVClass());
								values.setValue(value.getName(), valuee);
							}
							incrementalY += itemHeight + sliderSize + 1;
						}
					}
				}
			}
		}

		public void handleRelease(int mouseX, int mouseY, float height) {
			super.handleRelease(mouseX, mouseY, height);
			firstX = -1;
			firstY = -1;
		}
	}

	public static ClickItem getItem(Module module, float height) {
		if (module instanceof HasValues) return new ClickItemFactory.ClickItemSpecial(module, 4 + Wrapper.getFontRenderer().FONT_HEIGHT);
		return new ClickItem(module, 4 + Wrapper.getFontRenderer().FONT_HEIGHT);
	}

}
