package pq.rapture.render.tab;

import java.util.ArrayList;

public class TabList<E> extends ArrayList<E> {

	@Override
	public E get(int index) {
		if (this.size() == 1) return super.get(0);
		if (index < 0) return super.get(this.size() - 1);
		if (index >= this.size()) return super.get(0);

		return super.get(index);
	}

}
