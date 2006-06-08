package com.db4o.config.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * sets cascaded behavior of deletion, activation and/or update <br><br>
 * 
 * Example:<br> using the annotation {@code  @Cascade } without parameter sets cascaded <br> deletion, activation and update behavior.
 *  <br><br> 
 * {@code  @Cascade <br> public class Foo}
 * <br> <br>
 * Example:<br> using the annotation {@code  @Cascade } with parameter CascadeType.ACTIVATE <br> sets cascaded activate behavior.
 * <br><br>
 * {@code  @Cascade}( {CascadeType.ACTIVATE} )  <br> public class Foo
 * 
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.FIELD })
public @interface Cascade {
	CascadeType[] value() default { CascadeType.ACTIVATE, CascadeType.DELETE,
			CascadeType.UPDATE };
}
