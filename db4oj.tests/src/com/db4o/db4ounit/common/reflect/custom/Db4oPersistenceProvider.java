package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.foundation.*;
import com.db4o.foundation.io.File4;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.reflect.jdk.*;

/**
 * Custom class information is stored to db4o itself as
 * a CustomClassRepository singleton.
 */
public class Db4oPersistenceProvider implements PersistenceProvider {

	static class MyContext {

		public final CustomClassRepository repository;
		public final ObjectContainer metadata;
		public final ObjectContainer data;

		public MyContext(CustomClassRepository repository, ObjectContainer metadata, ObjectContainer data) {
			this.repository = repository;
			this.metadata = metadata;
			this.data = data;
		}
	}

	public void createEntryClass(PersistenceContext context, String className,
			String[] fieldNames, String[] fieldTypes) {
		logMethodCall("createEntryClass", context, className);
		
		CustomClassRepository repository = repository(context);
		repository.defineClass(className, fieldNames, fieldTypes);
		updateMetadata(context, repository);
	}

	public void createIndex(PersistenceContext context, String className, String fieldName) {
		CustomField field = customClass(context, className).customField(fieldName);
		field.indexed(true);
		updateMetadata(context, field);
		restart(context);
	}

	private void restart(PersistenceContext context) {
		closeContext(context);
		initContext(context);
	}

	public int delete(PersistenceContext context, String className, Object uid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void dropEntryClass(PersistenceContext context, String className) {
		// TODO Auto-generated method stub

	}

	public void dropIndex(PersistenceContext context, String className,
			String fieldName) {
		// TODO Auto-generated method stub

	}

	public void initContext(PersistenceContext context) {
		logMethodCall("initContext", context);

		ObjectContainer metadata = openMetadata(context.url());
		CustomClassRepository repository = initializeClassRepository(metadata);
		CustomReflector reflector = new CustomReflector(repository);
		ObjectContainer data = openData(reflector, context.url());
		context.setProviderContext(new MyContext(repository, metadata, data));
	}

	public void insert(PersistenceContext context, PersistentEntry entry) {
		logMethodCall("insert", context, entry);

		// clone the entry because clients are allowed to reuse
		// entry objects
		dataContainer(context).set(clone(entry));
	}

	public Iterator4 select(PersistenceContext context, PersistentEntryTemplate template) {
		logMethodCall("select", context, template);

		Query query = queryFromTemplate(context, template);
		return new ObjectSetIterator(query.execute());
	}

	public void update(PersistenceContext context, PersistentEntry entry) {
		// TODO Auto-generated method stub

	}

	private void addClassConstraint(PersistenceContext context, Query query, PersistentEntryTemplate template) {
		query.constrain(customClass(context, template.className));
	}

	private CustomClass customClass(PersistenceContext context, String className) {
		return repository(context).forName(className);
	}

	private Constraint addFieldConstraint(Query query, PersistentEntryTemplate template, int index) {
		return query.descend(template.fieldNames[index])
					.constrain(template.fieldValues[index]);
	}

	private void addFieldConstraints(Query query, PersistentEntryTemplate template) {
		if (template.fieldNames.length == 0) {
			return;
		}
		/*Constraint c = */ addFieldConstraint(query, template, 0);
//		for (int i=1; i<template.fieldNames.length; ++i) {
//			c = c.and(addFieldConstraint(query, template, i));
//		}
	}

	private PersistentEntry clone(PersistentEntry entry) {
		return new PersistentEntry(entry.className, entry.uid, entry.fieldValues);
	}

	public void closeContext(PersistenceContext context) {
		logMethodCall("closeContext", context);

		MyContext customContext = my(context);
		if (null != customContext) {
			customContext.metadata.close();
			customContext.data.close();
			context.setProviderContext(null);
		}
	}

	private MyContext my(PersistenceContext context) {
		return ((MyContext) context.getProviderContext());
	}

	private Configuration dataConfiguration(CustomReflector reflector) {
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(reflector);
		configureCustomClasses(config, reflector);
		return config;
	}

	private void configureCustomClasses(Configuration config, CustomReflector reflector) {
		Iterator4 classes = reflector.customClasses();
		while (classes.moveNext()) {
			CustomClass cc = (CustomClass)classes.current();
			configureFields(config, cc);
		}
	}

	private void configureFields(Configuration config, CustomClass cc) {
		Iterator4 fields = cc.customFields();
		while (fields.moveNext()) {
			CustomField field = (CustomField)fields.current();
			config.objectClass(cc).objectField(field.getName()).indexed(field.indexed());
		}
	}

	public ObjectContainer dataContainer(PersistenceContext context) {
		return my(context).data;
	}

	private CustomClassRepository initializeClassRepository(ObjectContainer container) {
		CustomClassRepository repository = queryClassRepository(container);
		if (repository == null) {
			log("Initializing new class repository.");
			repository = new CustomClassRepository();
			store(container, repository);
		} else {
			log("Found existing class repository: " + repository);
		}
		return repository;
	}

	private Configuration metaConfiguration() {
		Configuration config = Db4o.newConfiguration();
		config.exceptionsOnNotStorable(true);
		config.reflectWith(Platform4.reflectorForType(CustomClassRepository.class));
		cascade(config, CustomClassRepository.class);
		cascade(config, Hashtable4.class);
//		cascade(config, HashtableObjectEntry.class);
//		cascade(config, CustomClass.class);
//		cascade(config, CustomField.class);
		return config;
	}

	private void cascade(Configuration config, Class klass) {
		config.objectClass(klass).cascadeOnUpdate(true);
		config.objectClass(klass).cascadeOnActivate(true);
	}

	private ObjectContainer metadataContainer(PersistenceContext context) {
		return my(context).metadata;
	}

	private String metadataFile(String fname) {
		return fname + ".metadata";
	}

	private ObjectContainer openData(CustomReflector reflector, String fname) {
		return Db4o.openFile(dataConfiguration(reflector), fname);
	}

	private ObjectContainer openMetadata(String fname) {
		return Db4o.openFile(metaConfiguration(), metadataFile(fname));
	}

	public void purge(String url) {
		File4.delete(url);
		File4.delete(metadataFile(url));
	}

	private CustomClassRepository queryClassRepository(ObjectContainer container) {
		ObjectSet found = container.query(CustomClassRepository.class);
		if (!found.hasNext()) {
			return null;
		}
		return (CustomClassRepository)found.next();
	}

	private Query queryFromTemplate(PersistenceContext context, PersistentEntryTemplate template) {
		Query query = dataContainer(context).query();
		addClassConstraint(context, query, template);
		addFieldConstraints(query, template);
		return query;
	}

	private CustomClassRepository repository(PersistenceContext context) {
		return my(context).repository;
	}

	private void store(ObjectContainer container, Object obj) {
		container.set(obj);
		container.commit();
	}

	private void updateMetadata(PersistenceContext context, Object metadata) {
		store(metadataContainer(context), metadata);
	}

	private void log(String message) {
		Logger.log("Db4oPersistenceProvider: " + message);
	}

	private void logMethodCall(String methodName, Object arg) {
		Logger.logMethodCall("Db4oPersistenceProvider", methodName, arg);
	}

	private void logMethodCall(String methodName, Object arg1, Object arg2) {
		Logger.logMethodCall("Db4oPersistenceProvider", methodName, arg1, arg2);
	}

}

