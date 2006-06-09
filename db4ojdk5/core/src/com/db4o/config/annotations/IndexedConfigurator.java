package com.db4o.config.annotations;

public class IndexedConfigurator extends Db4oConfigurator {

	private String _className;
	private String _fieldName;

	public IndexedConfigurator(String className, String fieldName) {
		_className=className;
		_fieldName=fieldName;
	}

	@Override
	protected void configure() {
		objectClass(_className).objectField(_fieldName).indexed(true);
	}

}
