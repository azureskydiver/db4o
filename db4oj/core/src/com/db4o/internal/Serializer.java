/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.activation.*;


/**
 * @exclude
 */
public class Serializer {
	
    public static StatefulBuffer marshall(Transaction ta, Object obj) {
        SerializedGraph serialized = marshall(ta.container(), obj);
        StatefulBuffer buffer = new StatefulBuffer(ta, serialized.length());
        buffer.append(serialized._bytes);
        buffer.useSlot(serialized._id, 0, serialized.length());
        return buffer;
    }

    public static SerializedGraph marshall(ObjectContainerBase serviceProvider, Object obj) {
        MemoryFile memoryFile = new MemoryFile();
        memoryFile.setInitialSize(223);
        memoryFile.setIncrementSizeBy(300);
    	TransportObjectContainer carrier = new TransportObjectContainer(serviceProvider, memoryFile);
    	carrier.produceClassMetadata(carrier.reflector().forObject(obj));
		carrier.store(obj);
		int id = (int)carrier.getID(obj);
		carrier.close();
		return new SerializedGraph(id, memoryFile.getBytes());
    }
    
    public static Object unmarshall(ObjectContainerBase serviceProvider, StatefulBuffer yapBytes) {
        return unmarshall(serviceProvider, yapBytes._buffer, yapBytes.getID());
    }
    
    public static Object unmarshall(ObjectContainerBase serviceProvider, SerializedGraph serialized) {
    	return unmarshall(serviceProvider, serialized._bytes, serialized._id);
    }

    public static Object unmarshall(ObjectContainerBase serviceProvider, byte[] bytes, int id) {
		if(id <= 0){
			return null;
		}
        MemoryFile memoryFile = new MemoryFile(bytes);
		TransportObjectContainer carrier = new TransportObjectContainer(serviceProvider, memoryFile);
		Object obj = carrier.getByID(id);
		carrier.activate(carrier.transaction(), obj, new FullActivationDepth());
		carrier.close();
		return obj;
    }

}
