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
    
    protected ArrayInfo newArrayInfo() {
        return new MultidimensionalArrayInfo();
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
    
    protected Object readCreate(Transaction trans, ReadBuffer buffer, ArrayInfo info) {
        readInfo(trans, buffer, info);
		ReflectClass clazz = newInstanceReflectClass(trans.reflector(), info);
		if(clazz == null){
		    return null;
		}
		return arrayReflector(container(trans)).newInstance(clazz, ((MultidimensionalArrayInfo)info).dimensions());
    }
    
    protected void readDimensions(ArrayInfo info, ReadBuffer buffer) {
        readDimensions(info, buffer, buffer.readInt());
    }

    private void readDimensions(ArrayInfo info, ReadBuffer buffer, int dimensionCount) {
        int[] dim = new int[dimensionCount];
        for (int i = 0; i < dim.length; i++) {
            dim[i] = buffer.readInt();
        }
        ((MultidimensionalArrayInfo)info).dimensions(dim);
        info.elementCount(elementCount(dim));
    }
    
    protected void readDimensionsOldFormat(ReadBuffer buffer, ArrayInfo info, int classID) {
        readDimensions(info, buffer, classID);
    }

    public Object read(ReadContext context) {
        
        if (Deploy.debug) {
            Debug.readBegin(context, identifier());
        }
        
        MultidimensionalArrayInfo info = (MultidimensionalArrayInfo) newArrayInfo();
            
        Object array = readCreate(context.transaction(), context, info);

        if(array != null){
            Object[] objects = new Object[info.elementCount()];
            
            if (hasNullBitmap()) {
                BitMap4 nullBitMap = readNullBitmap(context, info.elementCount());                    
                for (int i = 0; i < info.elementCount(); i++) {
                    if (nullBitMap.isFalse(i)){
                        objects[i] = context.readObject(delegateTypeHandler());    
                    }
                }
            } else {
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = context.readObject(delegateTypeHandler());
                }
            }
            arrayReflector(container(context)).shape(objects, 0, array, info.dimensions() , 0);
        }
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        
        return array;
    }
    
    protected void writeDimensions(WriteContext context, ArrayInfo info) {
        int[] dim = ((MultidimensionalArrayInfo)info).dimensions();
        context.writeInt(dim.length);
        for (int i = 0; i < dim.length; i++) {
            context.writeInt(dim[i]);
        }
    }

    public void write(WriteContext context, Object obj) {
        
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPARRAYN);
        }
        ArrayInfo info = newArrayInfo();
        analyze(container(context), obj, info);
        writeInfo(context, info);
        
        Iterator4 objects = allElements(container(context), obj);
        
        if (hasNullBitmap()) {
            BitMap4 nullBitMap = new BitMap4(info.elementCount());
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
    
    protected void analyzeDimensions(ObjectContainerBase container, Object obj, ArrayInfo info){
        int[] dim = arrayReflector(container).dimensions(obj);
        ((MultidimensionalArrayInfo)info).dimensions(dim);
        info.elementCount(elementCount(dim));
    }

    public TypeHandler4 genericTemplate() {
        return new MultidimensionalArrayHandler();
    }

}
