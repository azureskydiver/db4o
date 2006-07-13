package com.db4o.config.annotations.reflect;

public class PersistedStaticFieldValuesConfigurator extends Db4oConfigurator {
	private String _className;

	public PersistedStaticFieldValuesConfigurator(String name) {
		_className = name;
	}

	@Override
	protected void configure() {
		objectClass(_className).persistStaticFieldValues();
	}

}
