package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

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

	public void closeContext(PersistenceContext context, boolean purge) {
		logMethodCall("closeContext", context, new Boolean(purge));

		closeContext(context);
		if (purge) {
			purge(context.url());
		}
	}

	public void createEntryClass(PersistenceContext context, String className,
			String[] fieldNames, String[] fieldTypes) {
		logMethodCall("createEntryClass", context, className);
		repository(context).defineClass(className, fieldNames, fieldTypes);
		updateRepository(context);
	}

	public void createIndex(PersistenceContext context, String className,
			String fieldName) {
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
		query.constrain(repository(context).forName(template.className));
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

	private void closeContext(PersistenceContext context) {
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

	private Configuration dataConfiguration(Reflector reflector) {
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(reflector);
		return config;
	}

	private ObjectContainer dataContainer(PersistenceContext context) {
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
		config.objectClass(CustomClassRepository.class).cascadeOnUpdate(true);
		config.objectClass(CustomClassRepository.class).cascadeOnActivate(true);
		return config;
	}

	private ObjectContainer metadataContainer(PersistenceContext context) {
		return my(context).metadata;
	}

	private String metadataFile(String fname) {
		return fname + ".metadata";
	}

	private ObjectContainer openData(Reflector reflector, String fname) {
		return Db4o.openFile(dataConfiguration(reflector), fname);
	}

	private ObjectContainer openMetadata(String fname) {
		return Db4o.openFile(metaConfiguration(), metadataFile(fname));
	}

	private void purge(String url) {
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

	private void updateRepository(PersistenceContext context) {
		store(metadataContainer(context), repository(context));
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
