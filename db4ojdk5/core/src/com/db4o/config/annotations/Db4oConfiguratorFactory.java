package com.db4o.config.annotations;

import java.lang.annotation.*;
import java.lang.reflect.*;

public interface Db4oConfiguratorFactory {
	Db4oConfigurator configuratorFor(AnnotatedElement element,Annotation annotation);
}
