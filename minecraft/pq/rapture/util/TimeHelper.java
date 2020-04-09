package pq.rapture.util;

public class TimeHelper {
	
	private long resetMS = 0l;

	public TimeHelper() {
		resetMS = getCurrentTime();
	}
	
	public void reset() {
		resetMS = getCurrentTime();
	}

	public void addReset(long addedReset) {
		resetMS += addedReset;
	}
	
	public void setReset(long setReset) {
		resetMS = setReset;
	}

	public boolean hasDelayRun(long delay) {
		if (getCurrentTime() >= resetMS + delay)
			return true;
		return false;
	}

	public long getCurrentTime() {
		return (long) (System.nanoTime() / 1E6);
	}

	public static TimeHelper getTimer() {
		return new TimeHelper();
	}
	
	public static boolean hasDelayRun(long resetMS, long delay) {
		return getTimer().getCurrentTime() >= resetMS + delay;
	}
}
