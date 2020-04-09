package pq.rapture.rxdy.event;

/**
 * Created by Haze on 4/15/2015.
 */
public class Event {

    private boolean cancelled;

    public boolean isCancelled()
    {
        return this.cancelled;
    }
    
    public void cancel() {
    	this.cancelled = true;
    }

}
