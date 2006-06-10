package com.db4o.config.annotations.reflect;

import com.db4o.config.*;

public class CallConstructorConfigurator extends ClassConfigurator {
	public CallConstructorConfigurator(String className) {
		super(className);
	}

	@Override
	protected void configure(ObjectClass objectClass) {
		objectClass.callConstructor(true);
	}
}
