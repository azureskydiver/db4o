/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.reflect.*;


/**
 * n-dimensional array
 * @exclude
 */
public final class MultidimensionalArrayHandler extends ArrayHandler {
	
	
    public MultidimensionalArrayHandler(ObjectContainerBase stream, TypeHandler4 a_handler, boolean a_isPrimitive) {
        super(stream, a_handler, a_isPrimitive);
    }

    public final Object[] allElements(Object a_array) {
		return allElements(_reflectArray, a_array);
    }

	public static Object[] allElements(final ReflectArray reflectArray, Object array) {
		int[] dim = reflectArray.dimensions(array);
        Object[] flat = new Object[elementCount(dim)];
        reflectArray.flatten(array, dim, 0, flat, 0);
        return flat;
	}

    public final int elementCount(Transaction a_trans, Buffer a_bytes) {
        return elementCount(readDimensions(a_trans, a_bytes, ReflectClassByRef.IGNORED));
    }

    private static final int elementCount(int[] a_dim) {
        int elements = a_dim[0];
        for (int i = 1; i < a_dim.length; i++) {
            elements = elements * a_dim[i];
        }
        return elements;
    }

    public final byte identifier() {
        return Const4.YAPARRAYN;
    }

    public final int objectLength(Object a_object) {
        int[] dim = _reflectArray.dimensions(a_object);
        return Const4.OBJECT_LENGTH
            + (Const4.INT_LENGTH * (2 + dim.length))
            + (elementCount(dim) * i_handler.linkLength());
    }
    
    public int ownLength(Object obj){
        int[] dim = _reflectArray.dimensions(obj);
        return Const4.OBJECT_LENGTH
            + (Const4.INT_LENGTH * (2 + dim.length));
    }

    public final Object read1(MarshallerFamily mf, StatefulBuffer reader) throws CorruptionException, Db4oIOException {
        
        if (Deploy.debug) {
            reader.readBegin(identifier());
        }
        
		Object[] ret = new Object[1];
		int[] dim = read1Create(reader.getTransaction(), reader, ret);
		if(ret[0] != null){
	        Object[] objects = new Object[elementCount(dim)];
	        for (int i = 0; i < objects.length; i++) {
	            objects[i] = i_handler.read(mf, reader, true);
	        }
            _reflectArray.shape(objects, 0, ret[0], dim, 0);
		}
        
        if (Deploy.debug) {
            reader.readEnd();
        }
        
        return ret[0];
    }
    
    protected int readElementsDefrag(ReaderPair readers) {
    	int numDimensions=super.readElementsDefrag(readers);        
    	int [] dimensions=new int[numDimensions];
	    for (int i = 0; i < numDimensions; i++) {
	    	dimensions[i]=readers.readInt();
	    }
	    return elementCount(dimensions);
    }
    
    public final void read1Candidates(MarshallerFamily mf, Buffer reader, QCandidates candidates) {
        if(Deploy.debug){
            reader.readBegin(identifier());
        }
        
        Object[] ret = new Object[1];
        int[] dim = read1Create(candidates.i_trans, reader, ret);
        if(ret[0] != null){
            int count = elementCount(dim);
            for (int i = 0; i < count; i++) {
                QCandidate qc = i_handler.readSubCandidate(mf, reader, candidates, true);
                if(qc != null){
                    candidates.addByIdentity(qc);
                }
            }
        }
        
        if (Deploy.debug) {
            reader.readEnd();
        }
    }
    
	public final Object read1Query(Transaction a_trans, MarshallerFamily mf, Buffer a_bytes) throws CorruptionException, Db4oIOException {
        
        if(Deploy.debug){
            a_bytes.readBegin(identifier());
        }
        
		Object[] ret = new Object[1];
		int[] dim = read1Create(a_trans, a_bytes, ret);
        if(ret[0] != null){
			Object[] objects = new Object[elementCount(dim)];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = i_handler.readQuery(a_trans, mf, true, a_bytes, true);
			}
            _reflectArray.shape(objects, 0, ret[0], dim, 0);
        }
        
        if (Deploy.debug) {
            a_bytes.readEnd();
        }

		return ret[0];
	}

    private int[] read1Create(Transaction a_trans, Buffer a_bytes, Object[] obj) {
		ReflectClassByRef clazz = new ReflectClassByRef();
		int[] dim = readDimensions(a_trans, a_bytes, clazz);
        if (i_isPrimitive) {
        	obj[0] = a_trans.reflector().array().newInstance(i_handler.primitiveClassReflector(), dim);
        } else {
        	if (clazz.value != null) {
				obj[0] = a_trans.reflector().array().newInstance(clazz.value, dim);
        	}
        }
        return dim;
    }

    private final int[] readDimensions(Transaction a_trans, Buffer a_bytes, ReflectClassByRef clazz) {
        int[] dim = new int[readElementsAndClass(a_trans, a_bytes, clazz)];
        for (int i = 0; i < dim.length; i++) {
            dim[i] = a_bytes.readInt();
        }
        return dim;
    }

    public final void writeNew1(Object obj, StatefulBuffer writer) {
        
        if (Deploy.debug) {
            writer.writeBegin(identifier());
        }
        
        int[] dim = _reflectArray.dimensions(obj);
        writeClass(obj, writer);
        writer.writeInt(dim.length);
        for (int i = 0; i < dim.length; i++) {
            writer.writeInt(dim[i]);
        }
        Object[] objects = allElements(obj);
        
        MarshallerFamily mf = MarshallerFamily.current();
        
        for (int i = 0; i < objects.length; i++) {
            i_handler.writeNew(mf, element(objects, i), false, writer, true, true);
        }
        
        if (Deploy.debug) {
            writer.writeEnd();
        }
        
    }

    private Object element(Object a_array, int a_position) {
        try {
            return _reflectArray.get(a_array, a_position);
        } catch (Exception e) {
            return null;
        }
    }
}
