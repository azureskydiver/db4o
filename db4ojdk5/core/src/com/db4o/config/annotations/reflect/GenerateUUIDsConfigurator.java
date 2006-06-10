package com.db4o.config.annotations.reflect;

import com.db4o.config.ObjectClass;

public class GenerateUUIDsConfigurator extends ClassConfigurator {

	public GenerateUUIDsConfigurator(String className) {
		super(className);
	}

	@Override
	protected void configure(ObjectClass objectClass) {
		objectClass.generateUUIDs(true);
	}

}
