/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.inside.traversal;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;


public class FieldIterators {

	public static Iterator4 persistentFields(ReflectClass claxx) {
		return Iterators.filter(claxx.getDeclaredFields(), new Predicate4() {
			public boolean match(Object candidate) {
				final ReflectField field = (ReflectField) candidate;
				if (field.isStatic()) return false;
				if (field.isTransient()) return false;
				if (Platform4.isTransient(field.getFieldType())) return false;
				return true;
			}
		});
	}

}
