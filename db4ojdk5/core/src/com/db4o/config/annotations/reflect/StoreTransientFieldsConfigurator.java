package com.db4o.config.annotations.reflect;

import com.db4o.config.ObjectClass;

public class StoreTransientFieldsConfigurator extends ClassConfigurator {

	public StoreTransientFieldsConfigurator(String className) {
		super(className);
	}

	@Override
	protected void configure(ObjectClass objectClass) {
		objectClass.storeTransientFields(true);
	}

}
