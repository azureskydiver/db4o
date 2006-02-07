package com.db4o.inside.replication;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

public class CollectionHandlerImpl implements CollectionHandler {

	private final CollectionHandler _mapHandler;

	private final ReflectClass _reflectCollectionClass;
	private final Reflector _reflector;

	public CollectionHandlerImpl() {
		this(new ReplicationReflector().reflector());
	}

	public CollectionHandlerImpl(Reflector reflector) {
		_mapHandler = new MapHandler(reflector);
		_reflector = reflector;
		_reflectCollectionClass = reflector.forClass(Collection.class);
	}

	public boolean canHandle(ReflectClass claxx) {
		if (_mapHandler.canHandle(claxx)) return true;
		return _reflectCollectionClass.isAssignableFrom(claxx);
	}

	public boolean canHandle(Object obj) {
		return canHandle(_reflector.forObject(obj));
	}

	public Object cloneWithCounterparts(Object originalCollection, ReflectClass claxx, CounterpartFinder counterpartFinder) {
		if (_mapHandler.canHandle(claxx))
			return _mapHandler.cloneWithCounterparts(originalCollection, claxx, counterpartFinder);

		Collection original = (Collection) originalCollection;
		Object result;
		if ( (original instanceof PersistentList) || (original instanceof ArrayList)) {
			result = new ArrayList(original.size());
		} else if (original instanceof PersistentSet) {
			result = new HashSet(original.size());
		} else {
			result = _reflector.forClass(original.getClass()).newInstance();
		}
        copyState(original, result, counterpartFinder);
		return result;
	}

	public Iterator4 iteratorFor(Object collection) {
		if (_mapHandler.canHandle(_reflector.forObject(collection)))
			return _mapHandler.iteratorFor(collection);

		Collection subject = (Collection) collection;
		Collection4 result = new Collection4();

		Iterator it = subject.iterator();
		while (it.hasNext()) result.add(it.next());

		return result.iterator();
	}

    public void copyState(Object original, Object destination, CounterpartFinder counterpartFinder) {
        Collection originalCollection = (Collection) original;
        Collection destinationCollection = (Collection) destination;
        destinationCollection.clear();
        Iterator it = originalCollection.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            Object counterpart = counterpartFinder.findCounterpart(element);
            destinationCollection.add(counterpart);
        }
    }


}
