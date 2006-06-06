package com.db4o.config.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;

/**
 * sets db4o configurations accordingly annotations
 * 
 */
public class Annotate {
	ObjectClass classConfig;

	Class clazz;

	Configuration config;

	public Annotate( Class clazz, Configuration config,ObjectClass classConfig) {
		this.classConfig = classConfig;
		this.clazz = clazz;
		this.config = config;
	}

	/**
	 * the start methode to reflect user class and fields <br> in order to set appropriate configurations
	 * @param 
	 * @return 
	 */
	public ObjectClass reflectAnnotations(Class clazz) {
		try {
			reflectClass(clazz);
			reflectFields(clazz);

		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return classConfig;
	}

	private ObjectClass forceConfig() {
		if (classConfig == null) {
			classConfig = config.objectClass(clazz.getName());
		}
		return classConfig;
	}

	public void reflectClass(Class clazz) {
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation a : annotations) {
			configureCascadeClass(clazz, a);
		}
	}

	private void configureCascadeClass(Class c, Annotation a) {
		if (a.annotationType().equals(Cascade.class)) {
			CascadeType[] type = ((Cascade) a).value();
			for (CascadeType t : type) {
				configureClassCascade(t);
			}
		}
	}

	private void configureClassCascade(CascadeType t) {
		switch (t) {
		case UPDATE:
			forceConfig().cascadeOnUpdate(true);
			break;
		case DELETE:
			forceConfig().cascadeOnDelete(true);
			break;
		case ACTIVATE:
			forceConfig().cascadeOnActivate(true);
			break;

		default:
			break;
		}
	}

	public ObjectClass getClassConfig() {
		return classConfig;
	}

	public void reflectFields(Class clazz) {

		Field[] declaredFields;
		try {
			declaredFields = clazz.getDeclaredFields();
			for (Field f : declaredFields) {
				for (Annotation a : f.getAnnotations()) {
					configureCascadeFields(clazz, f, a);
					configureIndexed(clazz, f, a);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}

	}

	private void configureIndexed(Class c, Field f, Annotation a) {
		if (a.annotationType().equals(Index.class)) {
			forceConfig().objectField(f.getName()).indexed(true);
		}
	}

	private void configureCascadeFields(Class c, Field f, Annotation a) {
		if (a.annotationType().equals(Cascade.class)) {
			CascadeType[] type = ((Cascade) a).value();
			for (CascadeType t : type) {
				configureFieldCascade(f, t);
			}
		}
	}

	private void configureFieldCascade(Field f, CascadeType t) {
		switch (t) {
		case UPDATE:
			forceConfig().objectField(f.getName()).cascadeOnUpdate(true);
			break;
		case DELETE:
			forceConfig().objectField(f.getName()).cascadeOnDelete(true);
			break;
		case ACTIVATE:
			forceConfig().objectField(f.getName()).cascadeOnActivate(true);
			break;

		default:
			break;
		}
	}

}
