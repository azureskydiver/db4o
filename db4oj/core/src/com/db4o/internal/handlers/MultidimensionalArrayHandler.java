/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * n-dimensional array
 * @exclude
 */
public class MultidimensionalArrayHandler extends ArrayHandler {
	
    public MultidimensionalArrayHandler(ObjectContainerBase stream, TypeHandler4 a_handler, boolean a_isPrimitive) {
        super(stream, a_handler, a_isPrimitive);
    }
    
    protected MultidimensionalArrayHandler(TypeHandler4 template) {
        super(template);
    }

    public final Object[] allElements(Object array) {
		return allElements(arrayReflector(), array);
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

    public int ownLength(Object obj){
        int[] dim = arrayReflector().dimensions(obj);
        return Const4.OBJECT_LENGTH
            + (Const4.INT_LENGTH * (2 + dim.length));
    }

    public final Object read1(MarshallerFamily mf, StatefulBuffer reader) throws CorruptionException, Db4oIOException {
        
        if (Deploy.debug) {
            reader.readBegin(identifier());
        }
        
        IntArrayByRef dimensions = new IntArrayByRef();
        Object arr = readCreate(reader.getTransaction(), reader, dimensions);
		if(arr != null){
	        Object[] objects = new Object[elementCount(dimensions.value)];
	        for (int i = 0; i < objects.length; i++) {
	            objects[i] = _handler.read(mf, reader, true);
	        }
	        arrayReflector().shape(objects, 0, arr, dimensions.value, 0);
		}
        
        if (Deploy.debug) {
            reader.readEnd();
        }
        
        return arr;
    }
    
    protected int readElementsDefrag(BufferPair readers) {
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
        
        IntArrayByRef dimensions = new IntArrayByRef();
        Object arr = readCreate(candidates.i_trans, reader, dimensions);
        
        if(arr != null){
            int count = elementCount(dimensions.value);
            for (int i = 0; i < count; i++) {
                QCandidate qc = _handler.readSubCandidate(mf, reader, candidates, true);
                if(qc != null){
                    candidates.addByIdentity(qc);
                }
            }
        }
        
        if (Deploy.debug) {
            reader.readEnd();
        }
    }
    
	public final Object read1Query(Transaction trans, MarshallerFamily mf, Buffer buffer) throws CorruptionException, Db4oIOException {
        
        if(Deploy.debug){
            buffer.readBegin(identifier());
        }
        
        IntArrayByRef dimensions = new IntArrayByRef();
		Object arr = readCreate(trans, buffer, dimensions);
        if(arr != null){
			Object[] objects = new Object[elementCount(dimensions.value)];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = _handler.readQuery(trans, mf, true, buffer, true);
			}
			arrayReflector().shape(objects, 0, arr, dimensions.value, 0);
        }
        
        if (Deploy.debug) {
            buffer.readEnd();
        }

		return arr;
	}

    private Object readCreate(Transaction trans, ReadBuffer buffer, IntArrayByRef dimensions) {
		ReflectClassByRef clazz = new ReflectClassByRef();
		dimensions.value = readDimensions(trans, buffer, clazz);
        if (_isPrimitive) {
        	return arrayReflector().newInstance(primitiveClassReflector(), dimensions.value);
        } 
    	if (clazz.value != null) {
			return arrayReflector().newInstance(clazz.value, dimensions.value);
    	}
    	return null;
    }

    private final int[] readDimensions(Transaction trans, ReadBuffer buffer, ReflectClassByRef clazz) {
        int[] dim = new int[readElementsAndClass(trans, buffer, clazz)];
        for (int i = 0; i < dim.length; i++) {
            dim[i] = buffer.readInt();
        }
        return dim;
    }

    private Object element(Object a_array, int a_position) {
        try {
            return arrayReflector().get(a_array, a_position);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Object read(ReadContext context) {
        
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPARRAYN);
        }
            
        IntArrayByRef dimensions = new IntArrayByRef();
        Object array = readCreate(context.transaction(), context, dimensions);

        if(array != null){
            Object[] objects = new Object[elementCount(dimensions.value)];
            for (int i = 0; i < objects.length; i++) {
                objects[i] = context.readObject(_handler);
            }
            arrayReflector().shape(objects, 0, array, dimensions.value, 0);
        }
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        
        return array;
    }

    public void write(WriteContext context, Object obj) {
        
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPARRAYN);
        }
        
        int classID = classID(obj);
        context.writeInt(classID);
        
        int[] dim = arrayReflector().dimensions(obj);
        context.writeInt(dim.length);
        for (int i = 0; i < dim.length; i++) {
            context.writeInt(dim[i]);
        }
        
        Object[] objects = allElements(obj);
        for (int i = 0; i < objects.length; i++) {
            context.writeObject(_handler, element(objects, i));
        }
        
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
    

}
