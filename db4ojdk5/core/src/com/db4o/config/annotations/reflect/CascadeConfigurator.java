package com.db4o.config.annotations.reflect;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.annotations.CascadeType;

public class CascadeConfigurator extends Db4oConfigurator {
	String _className;
	String _fieldName;
	CascadeType[] _cascadeTypes;


	public CascadeConfigurator(String className, String fieldName, CascadeType[] cascadeTypes) {
		this._className = className;
		this._fieldName = fieldName;
		this._cascadeTypes = cascadeTypes;
	}

	protected void configure() {
		ObjectClass objectClass=objectClass(_className);
		Config4Abstract objectConfig=(_fieldName==null ? (Config4Abstract)objectClass : (Config4Abstract)objectClass.objectField(_fieldName));
		for (CascadeType t : _cascadeTypes) {
			configureCascade(objectConfig, t);
		}
	}
	
	private void configureCascade(Config4Abstract objectConfig, CascadeType t) {
		switch (t) {
		case UPDATE:
			objectConfig.cascadeOnUpdate(true);
			break;
		case DELETE:
			objectConfig.cascadeOnDelete(true);
			break;
		case ACTIVATE:
			objectConfig.cascadeOnActivate(true);
			break;
		default:
			break;
		}
	}

}
