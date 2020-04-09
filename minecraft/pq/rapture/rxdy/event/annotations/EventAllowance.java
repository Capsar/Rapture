package pq.rapture.rxdy.event.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pq.rapture.rxdy.event.utility.EventAllowanceEnum;

/**
 * Created by Haze on 5/26/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventAllowance {

    EventAllowanceEnum allowance() default EventAllowanceEnum.ACCEPT_ONLY_ALL;

}
