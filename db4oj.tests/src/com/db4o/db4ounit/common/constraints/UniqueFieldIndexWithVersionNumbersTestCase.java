package com.db4o.db4ounit.common.constraints;

import com.db4o.config.ConfigScope;
import com.db4o.config.Configuration;

public class UniqueFieldIndexWithVersionNumbersTestCase extends
		UniqueFieldIndexTestCase {

	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.generateVersionNumbers(ConfigScope.GLOBALLY);
	}
	
	public static void main(String[] args) {
		new UniqueFieldIndexWithVersionNumbersTestCase().runAll();
	}
}
