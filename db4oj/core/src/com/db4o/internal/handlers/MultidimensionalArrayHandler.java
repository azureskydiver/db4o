/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * n-dimensional array
 * @exclude
 */
public class MultidimensionalArrayHandler extends ArrayHandler {
	
    public MultidimensionalArrayHandler(TypeHandler4 a_handler, boolean a_isPrimitive) {
        super(a_handler, a_isPrimitive);
    }
    
    public MultidimensionalArrayHandler(){
        // required for reflection cloning
    }
    
    public final Iterator4 allElements(ObjectContainerBase container, Object array) {
		return allElements(arrayReflector(container), array);
    }

	public static Iterator4 allElements(final ReflectArray reflectArray, Object array) {
		// TODO: replace array copying code with iteration
		int[] dim = reflectArray.dimensions(array);
        Object[] flat = new Object[elementCount(dim)];
        reflectArray.flatten(array, dim, 0, flat, 0);
        return new ArrayIterator4(flat);
	}

    public final int elementCount(Transaction trans, ReadBuffer buffer) {
        return elementCount(readDimensions(trans, buffer, new ArrayInfo()));
    }

    protected static final int elementCount(int[] a_dim) {
        int elements = a_dim[0];
        for (int i = 1; i < a_dim.length; i++) {
            elements = elements * a_dim[i];
        }
        return elements;
    }

    public final byte identifier() {
        return Const4.YAPARRAYN;
    }

    public int ownLength(ObjectContainerBase container, Object obj){
        int[] dim = arrayReflector(container).dimensions(obj);
        return Const4.OBJECT_LENGTH
            + (Const4.INT_LENGTH * (2 + dim.length));
    }

    protected int readElementCountDefrag(DefragmentContext context) {
    	int numDimensions=super.readElementCountDefrag(context);        
    	int [] dimensions=new int[numDimensions];
	    for (int i = 0; i < numDimensions; i++) {
	    	dimensions[i]=context.readInt();
	    }
	    return elementCount(dimensions);
    }
    
    protected Object readCreate(Transaction trans, ReadBuffer buffer, IntArrayByRef dimensions) {
		ArrayInfo info = new ArrayInfo();
		dimensions.value = readDimensions(trans, buffer, info);
		ReflectClass clazz = newInstanceReflectClass(trans.reflector(), info);
		if(clazz == null){
		    return null;
		}
		return arrayReflector(container(trans)).newInstance(clazz, dimensions.value);
    }
    
    private final int[] readDimensions(Transaction trans, ReadBuffer buffer, ArrayInfo info) {
        int[] dim = new int[readElementsAndClass(trans, buffer, info)];
        for (int i = 0; i < dim.length; i++) {
            dim[i] = buffer.readInt();
        }
        return dim;
    }

    public Object read(ReadContext context) {
        
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPARRAYN);
        }
            
        IntArrayByRef dimensions = new IntArrayByRef();
        Object array = readCreate(context.transaction(), context, dimensions);

        if(array != null){
            int elementCount = elementCount(dimensions.value);
            Object[] objects = new Object[elementCount];
            
            if (hasNullBitmap()) {
                BitMap4 nullBitMap = readNullBitmap(context, elementCount);                    
                for (int i = 0; i < elementCount; i++) {
                    if (nullBitMap.isFalse(i)){
                        objects[i] = context.readObject(delegateTypeHandler());    
                    }
                }
            } else {
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = context.readObject(delegateTypeHandler());
                }
            }
            arrayReflector(container(context)).shape(objects, 0, array, dimensions.value, 0);
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
        
        int classID = classID(container(context), obj);
        context.writeInt(classID);
        
        int[] dim = arrayReflector(container(context)).dimensions(obj);
        context.writeInt(dim.length);
        for (int i = 0; i < dim.length; i++) {
            context.writeInt(dim[i]);
        }
        
        Iterator4 objects = allElements(container(context), obj);
        
        if (hasNullBitmap()) {
            int elementCount = elementCount(dim);
            BitMap4 nullBitMap = new BitMap4(elementCount);
            ReservedBuffer nullBitMapBuffer = context.reserve(nullBitMap.marshalledLength());
            int currentElement = 0;
            while (objects.moveNext()) {
                Object current = objects.current();
                if(current == null){
                    nullBitMap.setTrue(currentElement);
                }else{
                    context.writeObject(delegateTypeHandler(), current);
                }
                currentElement++;
            }
            nullBitMapBuffer.writeBytes(nullBitMap.bytes());
        } else {
            while (objects.moveNext()) {
                context.writeObject(delegateTypeHandler(), objects.current());
            }
        }

        
        
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
    
    public TypeHandler4 genericTemplate() {
        return new MultidimensionalArrayHandler();
    }
    

}
