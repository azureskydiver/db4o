package com.db4o.config.annotations.reflect;

import com.db4o.config.ObjectClass;

public class GenerateVersionNumbersConfigurator extends ClassConfigurator {

	public GenerateVersionNumbersConfigurator(String className) {
		super(className);
	}

	@Override
	protected void configure(ObjectClass objectClass) {
		objectClass.generateVersionNumbers(true);
	}

}
