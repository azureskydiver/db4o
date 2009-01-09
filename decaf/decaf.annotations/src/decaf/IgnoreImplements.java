package decaf;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface IgnoreImplements {

	Platform value() default Platform.ALL;
	
	Class<?>[] interfaces() default {};

}
