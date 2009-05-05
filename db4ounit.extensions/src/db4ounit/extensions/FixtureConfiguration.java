/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.config.*;

public interface FixtureConfiguration {
	String getLabel();
	void configure(Class clazz, Configuration config);
}
