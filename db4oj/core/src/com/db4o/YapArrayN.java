/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class YapArrayN extends YapArray {
	
	
    YapArrayN(YapDataType a_handler, boolean a_isPrimitive) {
        super(a_handler, a_isPrimitive);
    }

    final Object[] allElements(Object a_array) {
        int[] dim = Array4.dimensions(a_array);
        Object[] flat = new Object[elementCount(dim)];
        Array4.flatten(a_array, dim, 0, flat, 0);
        return flat;
    }

    final int elementCount(Transaction a_trans, YapReader a_bytes) {
        return elementCount(readDimensions(a_trans, a_bytes, new Class[1]));
    }

    private final int elementCount(int[] a_dim) {
        int elements = a_dim[0];
        for (int i = 1; i < a_dim.length; i++) {
            elements = elements * a_dim[i];
        }
        return elements;
    }

    final byte identifier() {
        return YapConst.YAPARRAYN;
    }

    final int objectLength(Object a_object) {
        int[] dim = Array4.dimensions(a_object);
        return YapConst.OBJECT_LENGTH
            + (YapConst.YAPINT_LENGTH * ((Debug.arrayTypes ? 2 : 1) + dim.length))
            + (elementCount(dim) * i_handler.linkLength());
    }

    final Object read1(YapWriter a_bytes) throws CorruptionException {
		Object[] ret = new Object[1];
		int[] dim = read1Create(a_bytes.getTransaction(), a_bytes, ret);
		if(ret[0] != null){
	        Object[] objects = new Object[elementCount(dim)];
	        for (int i = 0; i < objects.length; i++) {
	            objects[i] = i_handler.read(a_bytes);
	        }
	        Array4.shape(objects, 0, ret[0], dim, 0);
		}
        return ret[0];
    }
    
    
	final Object read1Query(Transaction a_trans, YapReader a_bytes) throws CorruptionException {
		Object[] ret = new Object[1];
		int[] dim = read1Create(a_trans, a_bytes, ret);
        if(ret[0] != null){
			Object[] objects = new Object[elementCount(dim)];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = i_handler.readQuery(a_trans, a_bytes, true);
			}
			Array4.shape(objects, 0, ret[0], dim, 0);
        }
		return ret[0];
	}

    private int[] read1Create(Transaction a_trans, YapReader a_bytes, Object[] obj) {
		Class[] clazz = new Class[1];
		int[] dim = readDimensions(a_trans, a_bytes, clazz);
        if (i_isPrimitive) {
        	obj[0] = Array4.reflector().newInstance(i_handler.getPrimitiveJavaClass(), dim);
        } else {
        	if (clazz[0] != null) {
				obj[0] = Array4.reflector().newInstance(clazz[0], dim);
        	}
        }
        return dim;
    }

    private final int[] readDimensions(Transaction a_trans, YapReader a_bytes, Class[] clazz) {
        int[] dim = new int[readElementsAndClass(a_trans, a_bytes, clazz)];
        for (int i = 0; i < dim.length; i++) {
            dim[i] = a_bytes.readInt();
        }
        return dim;
    }

    final void writeNew1(Object a_object, YapWriter a_bytes) {
        int[] dim = Array4.dimensions(a_object);
        writeClass(a_object, a_bytes);
        a_bytes.writeInt(dim.length);
        for (int i = 0; i < dim.length; i++) {
            a_bytes.writeInt(dim[i]);
        }
        Object[] objects = allElements(a_object);
        for (int i = 0; i < objects.length; i++) {
            i_handler.writeNew(element(objects, i), a_bytes);
        }
    }

    private Object element(Object a_array, int a_position) {
        try {
            return Array4.reflector().get(a_array, a_position);
        } catch (Exception e) {
            return null;
        }
    }
}
