package com.db4o.config.annotations;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;

/**
 * sets db4o configurations accordingly annotations
 * 
 */
public class Annotate {
	Map<Class<? extends Annotation>,Db4oConfiguratorFactory> configurators;
	
	Config4Class classConfig;

	Class clazz;

	Configuration config;
	
	public Annotate( Class clazz, Configuration config,Config4Class classConfig) throws Exception {
		this.classConfig = classConfig;
		this.clazz = clazz;
		this.config = config;
		
		configurators=new HashMap<Class<? extends Annotation>, Db4oConfiguratorFactory>();
		configurators.put(Cascade.class, new CascadeConfiguratorFactory());
		configurators.put(Index.class, new NoArgsFieldConfiguratorFactory(IndexedConfigurator.class));
		configurators.put(CallConstructor.class, new NoArgsClassConfiguratorFactory(CallConstructorConfigurator.class));
	}

	/**
	 * the start methode to reflect user class and fields <br> in order to set appropriate configurations
	 * @param clazz Java class to reflect
	 * @return classConfig configurations of class
	 */
	public Config4Class reflectAnnotations(Class clazz) {
		try {
			reflectClass(clazz);
			reflectFields(clazz);

		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return classConfig;
	}

	public void reflectClass(Class clazz) {
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation a : annotations) {
			applyAnnotation(clazz, a);
		}
	}

	public void reflectFields(Class clazz) {

		Field[] declaredFields;
		try {
			declaredFields = clazz.getDeclaredFields();
			for (Field f : declaredFields) {
				for (Annotation a : f.getAnnotations()) {
					applyAnnotation(f, a);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}

	}

	private void applyAnnotation(AnnotatedElement element, Annotation a) {
		if(configurators.containsKey(a.annotationType())) {
			Db4oConfigurator configurator=configurators.get(a.annotationType()).configuratorFor(element, a);
			classConfig=(Config4Class)configurator.configure(config);
		}
	}
}
