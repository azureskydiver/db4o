/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;

public class IndependentConfigurationSource implements ConfigurationSource {

	public Configuration config() {
		return Db4o.newConfiguration();
	}

}
