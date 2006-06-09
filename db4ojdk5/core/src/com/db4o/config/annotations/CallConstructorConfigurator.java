package com.db4o.config.annotations;

public class CallConstructorConfigurator extends Db4oConfigurator {

	private String _className;

	public CallConstructorConfigurator(String className) {
		_className=className;
	}

	@Override
	protected void configure() {
		objectClass(_className).callConstructor(true);
	}
}
