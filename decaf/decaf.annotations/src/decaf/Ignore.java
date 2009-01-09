package decaf;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface Ignore {

	Platform value() default Platform.ALL;

}
