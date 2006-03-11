package com.db4o.test.replication;

import com.db4o.inside.replication.CollectionHandlerImpl;
import com.db4o.inside.replication.ReplicationReflector;
import com.db4o.reflect.Reflector;
import com.db4o.test.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class CollectionHandlerImplTest {
	private Reflector _reflector = ReplicationReflector.getInstance().reflector();
	private CollectionHandlerImpl _collectionHandler = new CollectionHandlerImpl();

	public CollectionHandlerImplTest() {

	}

	public void test() {
		tstVector();
		tstList();
		tstSet();
		tstMap();
		tstString();

		_reflector = null;
		_collectionHandler = null;
	}

	public void tstVector() {
		Vector vector = new Vector();
		Test.ensure(_collectionHandler.canHandle(vector));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(vector)));
		Test.ensure(_collectionHandler.canHandle(Vector.class));
	}

	public void tstList() {
		List list = new LinkedList();
		Test.ensure(_collectionHandler.canHandle(list));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(list)));
		Test.ensure(_collectionHandler.canHandle(List.class));
	}

	public void tstSet() {
		Set set = new HashSet();
		Test.ensure(_collectionHandler.canHandle(set));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(set)));
		Test.ensure(_collectionHandler.canHandle(Set.class));
	}

	public void tstMap() {
		Map map = new HashMap();
		Test.ensure(_collectionHandler.canHandle(map));
		Test.ensure(_collectionHandler.canHandle(_reflector.forObject(map)));
		Test.ensure(_collectionHandler.canHandle(Map.class));
	}

	public void tstString() {
		String str = "abc";
		Test.ensure(!_collectionHandler.canHandle(str));
		Test.ensure(!_collectionHandler.canHandle(_reflector.forObject(str)));
		Test.ensure(!_collectionHandler.canHandle(String.class));
	}
}
