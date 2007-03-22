/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.ext.*;


/**
 * @exclude
 */
public class Serializer {
	
    public static StatefulBuffer marshall(Transaction ta, Object obj) {
        SerializedGraph serialized = marshall(ta.stream(), obj);
        StatefulBuffer buffer = new StatefulBuffer(ta, serialized.length());
        buffer.append(serialized._bytes);
        buffer.useSlot(serialized._id, 0, serialized.length());
        return buffer;
    }

    public static SerializedGraph marshall(ObjectContainerBase serviceProvider, Object obj) {
        MemoryFile memoryFile = new MemoryFile();
        memoryFile.setInitialSize(223);
        memoryFile.setIncrementSizeBy(300);
        serviceProvider.produceClassMetadata(serviceProvider.reflector().forObject(obj));
        try {
        	TransportObjectContainer carrier = new TransportObjectContainer(serviceProvider, memoryFile);
			carrier.set(obj);
			int id = (int)carrier.getID(obj);
			carrier.close();
			return new SerializedGraph(id, memoryFile.getBytes());
		} catch (IOException exc) {
			Exceptions4.shouldNeverHappen();
			return null; // unreachable, just to make the compiler happy
		} 
    }
    
    public static Object unmarshall(ObjectContainerBase serviceProvider, StatefulBuffer yapBytes) {
        return unmarshall(serviceProvider, yapBytes._buffer, yapBytes.getID());
    }
    
    public static Object unmarshall(ObjectContainerBase serviceProvider, SerializedGraph serialized) {
    	return unmarshall(serviceProvider, serialized._bytes, serialized._id);
    }

    public static Object unmarshall(ObjectContainerBase serviceProvider, byte[] bytes, int id) {
        MemoryFile memoryFile = new MemoryFile(bytes);
        try {
			TransportObjectContainer carrier = new TransportObjectContainer(serviceProvider, memoryFile);
			Object obj = carrier.getByID(id);
			carrier.activate(obj, Integer.MAX_VALUE);
			carrier.close();
			return obj;
		} catch (IOException exc) {
			Exceptions4.shouldNeverHappen();
			return null; // unreachable, just to make the compiler happy
		}
    }

}
