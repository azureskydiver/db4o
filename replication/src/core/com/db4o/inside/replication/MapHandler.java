package com.db4o.inside.replication;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import org.hibernate.collection.PersistentMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapHandler implements CollectionHandler {

	private final ReflectClass _reflectMapClass;
	private final Reflector _reflector;

	public MapHandler(Reflector reflector) {
		_reflector = reflector;
		_reflectMapClass = reflector.forClass(Map.class);
	}

	public boolean canHandle(ReflectClass claxx) {
		return _reflectMapClass.isAssignableFrom(claxx);
	}

	public boolean canHandle(Object obj) {
		return canHandle(_reflector.forObject(obj));
	}

	public boolean canHandle(Class c) {
		return canHandle(_reflector.forClass(c));
	}

	public Iterator4 iteratorFor(final Object collection) {
		Map map = (Map) collection;
		Collection4 result = new Collection4();

		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			result.add(entry.getKey());
			result.add(entry.getValue());
		}

		return result.iterator();
	}

	public Object emptyClone(Object original, ReflectClass originalCollectionClass) {

		Map originalMap = (Map) original;
		Map result;

		if (originalMap instanceof PersistentMap) {
			result = new HashMap(originalMap.size());
		} else {
			result = (Map) _reflector.forClass(originalMap.getClass()).newInstance();
		}

		//copyState(originalMap, result, elementCloner);

		return result;
	}

	public void copyState(Object original, Object destination, CounterpartFinder counterpartFinder) {

		Map originalMap = (Map) original;
		Map destinationMap = (Map) destination;

		destinationMap.clear();

		Iterator it = originalMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object keyClone = counterpartFinder.findCounterpart(entry.getKey());
			Object valueClone = counterpartFinder.findCounterpart(entry.getValue());
			destinationMap.put(keyClone, valueClone);
		}

	}


}
