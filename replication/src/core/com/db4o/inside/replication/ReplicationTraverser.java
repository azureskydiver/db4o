package com.db4o.inside.replication;

import com.db4o.inside.traversal.CollectionFlattener;
import com.db4o.inside.traversal.GenericTraverser;
import com.db4o.inside.traversal.TraversedField;
import com.db4o.inside.traversal.Visitor;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;

public class ReplicationTraverser extends GenericTraverser {
	protected Object currentFieldOwner;
	protected String currentFieldName;

	public ReplicationTraverser(Reflector reflector, CollectionFlattener collectionFlattener) {
		super(reflector, collectionFlattener);
	}

	protected void traverseObject(Object object, Visitor visitor) {
		if (!visitor.visit(object)) {
			return;
		}

		if (object instanceof TraversedField) {
			//do nothing
		} else {
			ReflectClass claxx = _reflector.forObject(object);
			traverseFields(object, claxx);
		}
	}

	protected void traverseFields(Object object, ReflectClass claxx) {

		ReflectField[] fields;

		fields = claxx.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			ReflectField field = fields[i];
			currentFieldOwner = object;
			currentFieldName = field.getName();
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

	protected void queueUpForTraversing(Object object) {
		if (object == null) return;
		ReflectClass claxx = _reflector.forObject(object);
		if (isSecondClass(claxx)) return;

		if (_collectionFlattener.canHandle(claxx)) {
			if (currentFieldName != null && currentFieldOwner != null) {
				_queue.add(new TraversedField(currentFieldOwner, currentFieldName, object));
				currentFieldName = null;
				currentFieldOwner = null;
			} else {
				_queue.add(object);
			}
			traverseCollection(object);
		} else {
			if (claxx.isArray()) {
				traverseArray(object);
			} else {
				_queue.add(object);
			}
		}
	}
}
