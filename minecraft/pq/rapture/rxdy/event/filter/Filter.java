package pq.rapture.rxdy.event.filter;

import pq.rapture.rxdy.event.Event;

/**
 * Created by Haze on 4/26/2015.
 */
public interface Filter {

     boolean isEventAcceptable(Event e);

     boolean isAcceptable(Event e);

}
