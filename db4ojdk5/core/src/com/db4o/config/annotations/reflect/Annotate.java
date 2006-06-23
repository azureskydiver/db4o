package com.db4o.config.annotations.reflect;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.annotations.CallConstructor;
import com.db4o.config.annotations.Cascade;
import com.db4o.config.annotations.GenerateUUIDs;
import com.db4o.config.annotations.GenerateVersionNumbers;
import com.db4o.config.annotations.Indexed;
import com.db4o.config.annotations.MaximumActivationDepth;
import com.db4o.config.annotations.MinimumActivationDepth;
import com.db4o.config.annotations.PersistStaticFieldValues;
import com.db4o.config.annotations.QueryEvaluationOff;
import com.db4o.config.annotations.StoreTransientFields;
import com.db4o.config.annotations.UpdateDepth;

/**
 * sets db4o configurations accordingly annotations
 * 
 */

public class Annotate {
	Map<Class<? extends Annotation>, Db4oConfiguratorFactory> _configurators;

	Config4Class _classConfig;

	Class _clazz;

	Configuration _config;

	public Annotate(Class clazz, Configuration config, Config4Class classConfig)
			throws Exception {
		this._classConfig = classConfig;
		this._clazz = clazz;
		this._config = config;

		initMap();
	}

	private void initMap() throws NoSuchMethodException {
		_configurators = new HashMap<Class<? extends Annotation>, Db4oConfiguratorFactory>();
//		_configurators.put(Cascade.class, new CascadeConfiguratorFactory());
//		_configurators.put(UpdateDepth.class, new UpdateDepthFactory());
//		_configurators.put(MaximumActivationDepth.class,
//				new MaximumActivationDepthFactory());
//		_configurators.put(MinimumActivationDepth.class,
//				new MinimumActivationDepthFactory());
		_configurators.put(Indexed.class, new IndexedFactory());
//		_configurators.put(CallConstructor.class,
//				new NoArgsClassConfiguratorFactory(
//						CallConstructorConfigurator.class));
//		_configurators.put(QueryEvaluationOff.class,
//				new NoArgsFieldConfiguratorFactory(
//						QueryEvaluationOffConfigurator.class));
//		_configurators.put(GenerateUUIDs.class,
//				new NoArgsClassConfiguratorFactory(
//						GenerateUUIDsConfigurator.class));
//		_configurators.put(GenerateVersionNumbers.class,
//				new NoArgsClassConfiguratorFactory(
//						GenerateVersionNumbersConfigurator.class));
//		_configurators.put(StoreTransientFields.class,
//				new NoArgsClassConfiguratorFactory(
//						StoreTransientFieldsConfigurator.class));
//		_configurators.put(PersistStaticFieldValues.class,
//				new NoArgsClassConfiguratorFactory(
//						PersistStaticFieldValuesConfigurator.class));
	}

	/**
	 * the start methode to reflect user class and fields <br>
	 * in order to set appropriate configurations
	 * 
	 * @param clazz
	 *            Java class to reflect
	 * @return classConfig configurations of class
	 */
	public Config4Class reflectAnnotations(Class clazz) {
		try {
//			reflectClass(clazz);
			reflectFields(clazz);

		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return _classConfig;
	}

	/**
	 * reserved for the next release with the full annotation support
	 */
	private void reflectClass(Class clazz) {
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation a : annotations) {
			applyAnnotation(clazz, a);
		}
	}

	private void reflectFields(Class clazz) {

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
		if (_configurators.containsKey(a.annotationType())) {
			Db4oConfigurator configurator = _configurators.get(
					a.annotationType()).configuratorFor(element, a);
			_classConfig = (Config4Class) configurator.configure(_config);
		}
	}
}
