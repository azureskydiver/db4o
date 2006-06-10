package com.db4o.config.annotations.reflect;

import com.db4o.config.ObjectClass;

public class PersistStaticFieldValuesConfigurator extends ClassConfigurator {

	public PersistStaticFieldValuesConfigurator(String className) {
		super(className);
	}

	@Override
	protected void configure(ObjectClass objectClass) {
		objectClass.persistStaticFieldValues();
	}

}
