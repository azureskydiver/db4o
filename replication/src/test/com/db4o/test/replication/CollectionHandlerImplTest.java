package com.db4o.test.replication;

import com.db4o.ext.ExtDb4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.MemoryFile;
import com.db4o.inside.replication.CollectionHandlerImpl;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.test.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class CollectionHandlerImplTest {
	private static GenericReflector _reflector;
	private static CollectionHandlerImpl _collectionHandler;

	static {
		ExtObjectContainer tempOcToGetReflector = ExtDb4o.openMemoryFile(new MemoryFile()).ext();
		_reflector = tempOcToGetReflector.reflector();
		tempOcToGetReflector.close();
		_collectionHandler = new CollectionHandlerImpl();
	}

	public void testVector() {
		Vector vector = new Vector();
		Test.ensure(_collectionHandler.canHandle(vector));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(vector)));
	}

	public void testList() {
		List list = new LinkedList();
		Test.ensure(_collectionHandler.canHandle(list));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(list)));
	}

	public void testSet() {
		Set set = new HashSet();
		Test.ensure(_collectionHandler.canHandle(set));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(set)));
	}

	public void testMap() {
		Map map = new HashMap();
		Test.ensure(_collectionHandler.canHandle(map));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(map)));
	}

	public void testString() {
		String str = "abc";
		Test.ensure(!_collectionHandler.canHandle(str));
		Test.ensure(!_collectionHandler.canHandle(_reflector.forObject(str)));
	}
}
