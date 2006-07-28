/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

import java.lang.annotation.*;
import java.lang.reflect.*;

public interface Db4oConfiguratorFactory {
	Db4oConfigurator configuratorFor(AnnotatedElement element,Annotation annotation);
}
