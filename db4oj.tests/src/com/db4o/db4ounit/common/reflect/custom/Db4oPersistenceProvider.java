package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

/**
 * Custom class information is stored to db4o itself as
 * a CustomClassRepository singleton.
 */
public class Db4oPersistenceProvider implements PersistenceProvider {

	private CustomReflector _reflector = new CustomReflector();
	private CustomClassRepository _repository;
	
	public void initContext(PersistenceContext context) {
		ObjectContainer container = openFile(context.url());
		initializeClassRepository(container);
		context.setProviderContext(container);
	}

	private ObjectContainer openFile(String fname) {
		return Db4o.openFile(configuration(), fname);
	}

	private void initializeClassRepository(ObjectContainer container) {
		_repository = queryClassRepository(container);
		if (_repository == null) {
			_repository = new CustomClassRepository();
			updateRepository(container);
		}
		_reflector.initialize(_repository);
	}

	private void updateRepository(ObjectContainer container) {
		store(container, _repository);
	}

	private CustomClassRepository queryClassRepository(ObjectContainer container) {
		ObjectSet found = container.query(CustomClassRepository.class);
		if (!found.hasNext()) {
			return null;
		}
		return (CustomClassRepository)found.next();
	}

	public void closeContext(PersistenceContext context) {
		ObjectContainer container = container(context);
		if (null != container) {
			container.close();
			context.setProviderContext(null);
		}
	}

	public void createEntryClass(PersistenceContext context, String className,
			String[] fieldNames, String[] fieldTypes) {
		
		_repository.defineClass(className, fieldNames, fieldTypes);
		updateRepository(context);
	}

	private void updateRepository(PersistenceContext context) {
		updateRepository(container(context));
	}

	public void createIndex(PersistenceContext context, String className,
			String fieldName) {
		// TODO Auto-generated method stub

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

	public void insert(PersistenceContext context, PersistentEntry entry) {
		// clone the entry because clients are allowed to reuse
		// entry objects
		container(context).set(clone(entry));
	}

	private PersistentEntry clone(PersistentEntry entry) {
		// don't need to clone the array because arrays are treated as value types by db4o
		return new PersistentEntry(entry.className, entry.uid, entry.fieldValues);
	}

	public Iterator4 select(PersistenceContext context, PersistentEntryTemplate template) {
		Query query = queryFromTemplate(context, template);
		return new ObjectSetIterator(query.execute());
	}

	private Query queryFromTemplate(PersistenceContext context, PersistentEntryTemplate template) {
		Query query = container(context).query();
		addClassConstraint(query, template);
		addFieldConstraints(query, template);
		return query;
	}

	private void addClassConstraint(Query query, PersistentEntryTemplate template) {
		query.constrain(_repository.forName(template.className));
	}

	private void addFieldConstraints(Query query, PersistentEntryTemplate template) {
		if (template.fieldNames.length == 0) {
			return;
		}
		Constraint c = addFieldConstraint(query, template, 0);
//		for (int i=1; i<template.fieldNames.length; ++i) {
//			c = c.and(addFieldConstraint(query, template, i));
//		}
	}

	private Constraint addFieldConstraint(Query query, PersistentEntryTemplate template, int index) {
		return query.descend(template.fieldNames[index])
					.constrain(template.fieldValues[index]);
	}

	public void update(PersistenceContext context, PersistentEntry entry) {
		// TODO Auto-generated method stub

	}

	private Configuration configuration() {
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(_reflector);
		return config;
	}

	private ObjectContainer container(PersistenceContext context) {
		return ((ObjectContainer) context.getProviderContext());
	}

	private void store(ObjectContainer container, Object obj) {
		container.set(obj);
		container.commit();
	}

}
