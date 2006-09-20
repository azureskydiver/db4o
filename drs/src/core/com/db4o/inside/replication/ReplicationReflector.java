/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.replication;

import com.db4o.ext.ExtDb4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.MemoryFile;
import com.db4o.reflect.ReflectArray;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;


public class ReplicationReflector {
	private static ReplicationReflector instance = new ReplicationReflector();
	private final Reflector _reflector;
	private final ReflectArray _arrayReflector;

	private ReplicationReflector() {
		ExtObjectContainer tempOcToGetReflector = ExtDb4o.openMemoryFile(new MemoryFile()).ext();
//      FIXME: Find a better way without depending on ExtDb4o.  :P

		_reflector = tempOcToGetReflector.reflector();
		_arrayReflector = _reflector.array();
		tempOcToGetReflector.close();
	}

	public static ReplicationReflector getInstance() {
		return instance;
	}

	public Object[] arrayContents(Object array) {
		int[] dim = _arrayReflector.dimensions(array);
		Object[] result = new Object[volume(dim)];
		_arrayReflector.flatten(array, dim, 0, result, 0); //TODO Optimize add a visit(Visitor) method to ReflectArray or navigate the array to avoid copying all this stuff all the time.
		return result;
	}

	private int volume(int[] dim) {
		int result = dim[0];
		for (int i = 1; i < dim.length; i++) {
			result = result * dim[i];
		}
		return result;
	}

	ReflectClass forObject(Object obj) {
		return _reflector.forObject(obj);
	}

	ReflectClass getComponentType(ReflectClass claxx) {
		return _arrayReflector.getComponentType(claxx);
	}

	int[] arrayDimensions(Object obj) {
		return _arrayReflector.dimensions(obj);
	}

	public Object newArrayInstance(ReflectClass componentType, int[] dimensions) {
		return _arrayReflector.newInstance(componentType, dimensions);
	}

	public int arrayShape(
			Object[] a_flat,
			int a_flatElement,
			Object a_shaped,
			int[] a_dimensions,
			int a_currentDimension) {
		return _arrayReflector.shape(a_flat, a_flatElement, a_shaped, a_dimensions, a_currentDimension);
	}

	public Reflector reflector() {
		return _reflector;
	}

}
