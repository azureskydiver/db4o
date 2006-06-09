package com.db4o.config.annotations;

import com.db4o.config.*;

public class CascadeConfigurator extends Db4oConfigurator {
	String className;
	String fieldName;
	CascadeType[] cascadeTypes;


	public CascadeConfigurator(String className, String fieldName, CascadeType[] cascadeTypes) {
		this.className = className;
		this.fieldName = fieldName;
		this.cascadeTypes = cascadeTypes;
	}

	protected void configure() {
		ObjectClass objectClass=objectClass(className);
		ObjectConfig objectConfig=(fieldName==null ? objectClass : objectClass.objectField(fieldName));
		for (CascadeType t : cascadeTypes) {
			configureCascade(objectConfig, t);
		}

	}
	
	private void configureCascade(ObjectConfig objectConfig, CascadeType t) {
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
