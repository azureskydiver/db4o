package com.db4o.drs.inside.traversal;

import com.db4o.foundation.Iterator4;
import com.db4o.foundation.Queue4;
import com.db4o.reflect.ReflectArray;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;

public class GenericTraverser implements Traverser {
	protected final Reflector _reflector;
	private final ReflectArray _arrayReflector;
	protected final CollectionFlattener _collectionFlattener;
	protected final Queue4 _queue = new Queue4();

	public GenericTraverser(Reflector reflector, CollectionFlattener collectionFlattener) {
		_reflector = reflector;
		_arrayReflector = _reflector.array();
		_collectionFlattener = collectionFlattener;
	}

	public void traverseGraph(Object object, Visitor visitor) {
		queueUpForTraversing(object);
		while (true) {
			Object next = _queue.next();
			if (next == null) return;
			traverseObject(next, visitor);
		}
	}

	protected void traverseObject(Object object, Visitor visitor) {
		if (!visitor.visit(object))
			return;

		ReflectClass claxx = _reflector.forObject(object);
		traverseFields(object, claxx);
	}

	protected void traverseFields(Object object, ReflectClass claxx) {
		ReflectField[] fields;

		fields = claxx.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			ReflectField field = fields[i];
			if (field.isStatic()) continue;
			if (field.isTransient()) continue;
			field.setAccessible(); //TODO Optimize: Change the reflector so I dont have to call setAcessible all the time.

			Object value = field.get(object);
			queueUpForTraversing(value);
		}

		ReflectClass superclass = claxx.getSuperclass();
		if (superclass == null) return;
		traverseFields(object, superclass);
	}

	protected void traverseCollection(Object collection) {
		Iterator4 elements = _collectionFlattener.iteratorFor(collection); //TODO Optimization: visit instead of flattening.
		while (elements.moveNext()) {
			Object element = elements.current();
			if (element == null) continue;
			queueUpForTraversing(element);
		}
	}

	protected void traverseArray(Object array) {
		Object[] contents = contents(array);
		for (int i = 0; i < contents.length; i++) {
			queueUpForTraversing(contents[i]);
		}
	}

	protected void queueUpForTraversing(Object object) {
		if (object == null)
			return;

		ReflectClass claxx = _reflector.forObject(object);
		if (isSecondClass(claxx))
			return;

		if (_collectionFlattener.canHandle(claxx)) {
			traverseCollection(object);
			return;
		}
		
		if (claxx.isArray()) {
			traverseArray(object);
			return;
		}
		
		queueAdd(object);

	}

	protected void queueAdd(Object object) {
		_queue.add(object);
	}

	protected boolean isSecondClass(ReflectClass claxx) {
		//      TODO Optimization: Compute this lazily in ReflectClass;
		if (claxx.isSecondClass()) return true;
		return claxx.isArray() && claxx.getComponentType().isSecondClass();
	}


	final Object[] contents(Object array) { //FIXME Eliminate duplication. Move this to ReflectArray. This logic is in the GenericReplicationSessio too.
		int[] dim = _arrayReflector.dimensions(array);
		Object[] result = new Object[volume(dim)];
		_arrayReflector.flatten(array, dim, 0, result, 0); //TODO Optimize add a visit(Visitor) method to ReflectArray or navigate the array to avoid copying all this stuff all the time.
		return result;
	}

	private int volume(int[] dim) { //FIXME Eliminate duplication. Move this to ReflectArray. This logic is in the GenericReplicationSessio too.
		int result = dim[0];
		for (int i = 1; i < dim.length; i++) {
			result = result * dim[i];
		}
		return result;
	}

	public void extendTraversalTo(Object disconnected) {
		queueUpForTraversing(disconnected);
	}
}