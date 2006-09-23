/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;

public class GlobalConfigurationSource implements ConfigurationSource {

	private final Configuration _config=Db4o.newConfiguration();
	
	public Configuration config() {
		return _config;
	}

}
