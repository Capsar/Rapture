package pq.rapture.rxdy.event.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pq.rapture.rxdy.event.Event;
import pq.rapture.rxdy.event.filter.Filter;

/**
 * Created by Haze on 4/26/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filters {

    Class<? extends Filter>[] filters();
    Class<? extends Event>[] targets();

}
