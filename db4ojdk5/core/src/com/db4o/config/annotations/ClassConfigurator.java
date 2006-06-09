package com.db4o.config.annotations;

import com.db4o.config.*;

public abstract class ClassConfigurator extends Db4oConfigurator {
	private String _className;
	
	protected ClassConfigurator(String className) {
		_className=className;
	}
	
	@Override
	protected void configure() {
		configure(objectClass(_className));
	}
	
	protected abstract void configure(ObjectClass objectClass);
}
