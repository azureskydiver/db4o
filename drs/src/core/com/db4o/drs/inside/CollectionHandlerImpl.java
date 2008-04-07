package com.db4o.drs.inside;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

import java.util.Collection;
import java.util.Iterator;

public class CollectionHandlerImpl implements CollectionHandler {

	private final CollectionHandler _mapHandler;

	private final ReflectClass _reflectCollectionClass;
	private final Reflector _reflector;

	public CollectionHandlerImpl() {
		this(ReplicationReflector.getInstance().reflector());
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

	public boolean canHandle(Class c) {
		return canHandle(_reflector.forClass(c));
	}

	public Object emptyClone(Object originalCollection, ReflectClass originalCollectionClass) {
		if (_mapHandler.canHandle(originalCollectionClass))
			return _mapHandler.emptyClone(originalCollection, originalCollectionClass);

		Collection original = (Collection) originalCollection;

		Collection clone = ReplicationPlatform.emptyCollectionClone(original);
		if (null != clone) return clone;
		
		return _reflector.forClass(original.getClass()).newInstance();
	}

	public Iterator4 iteratorFor(Object collection) {
		if (_mapHandler.canHandle(_reflector.forObject(collection)))
			return _mapHandler.iteratorFor(collection);

		Iterable subject = (Iterable) collection;
		return copy(subject).iterator();
	}

	private Collection4 copy(Iterable subject) {
		Collection4 result = new Collection4();
		Iterator it = subject.iterator();
		while (it.hasNext()) result.add(it.next());
		return result;
	}

	public void copyState(Object original, Object destination, CounterpartFinder counterpartFinder) {
		if (_mapHandler.canHandle(original))
			_mapHandler.copyState(original, destination, counterpartFinder);
		else
			ReplicationPlatform.copyCollectionState(original, destination, counterpartFinder);
	}

	public Object cloneWithCounterparts(Object originalCollection, ReflectClass claxx, CounterpartFinder counterpartFinder) {
		if (_mapHandler.canHandle(claxx))
			return _mapHandler.cloneWithCounterparts(originalCollection, claxx, counterpartFinder);

		Collection original = (Collection) originalCollection;
		Collection result = (Collection) emptyClone(originalCollection, claxx);

		copyState(original, result, counterpartFinder);

		return result;
	}
}