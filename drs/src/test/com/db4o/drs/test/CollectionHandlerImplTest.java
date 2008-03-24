/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.db4o.drs.inside.CollectionHandlerImpl;
import com.db4o.drs.inside.ReplicationReflector;
import com.db4o.reflect.Reflector;
import db4ounit.Assert;

public class CollectionHandlerImplTest extends DrsTestCase {

	private final Reflector _reflector = ReplicationReflector.getInstance().reflector();
	private final CollectionHandlerImpl _collectionHandler = new CollectionHandlerImpl();

	public void testVector() {
		Vector vector = new Vector();
		Assert.isTrue(_collectionHandler.canHandle(vector));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(vector)));
		Assert.isTrue(_collectionHandler.canHandle(Vector.class));
	}

	public void testList() {
		List list = new LinkedList();
		Assert.isTrue(_collectionHandler.canHandle(list));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(list)));
		Assert.isTrue(_collectionHandler.canHandle(List.class));
	}
	
	public void testSet() {
		Set set = new HashSet();
		Assert.isTrue(_collectionHandler.canHandle(set));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(set)));
		Assert.isTrue(_collectionHandler.canHandle(Set.class));
	}

	public void testMap() {
		Map map = new HashMap();
		Assert.isTrue(_collectionHandler.canHandle(map));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(map)));
		Assert.isTrue(_collectionHandler.canHandle(Map.class));
	}

	public void testString() {
		String str = "abc";
		Assert.isTrue(!_collectionHandler.canHandle(str));
		Assert.isTrue(!_collectionHandler.canHandle(_reflector.forObject(str)));
		Assert.isTrue(!_collectionHandler.canHandle(String.class));
	}

}
