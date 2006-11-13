/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public final class ObjectHeader {
    
    private final YapClass _yapClass;
    
    public final MarshallerFamily _marshallerFamily;
    
    public final ObjectHeaderAttributes _headerAttributes;
    
    public ObjectHeader(YapStream stream, YapReader reader){
    	this(stream,null,reader);
    }

    public ObjectHeader(YapClass yapClass, YapReader reader){
    	this(null,yapClass,reader);
    }

    public ObjectHeader(YapWriter writer){
        this(writer.getStream(), writer);
    }
    
    public ObjectHeader(YapStream stream, YapClass yc, YapReader reader){
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPOBJECT);
        }
        int classID = reader.readInt();
        _marshallerFamily = readMarshallerFamily(reader, classID);
        
        classID=normalizeID(classID);

        _yapClass=(yc!=null ? yc : stream.getYapClass(classID));

        if (Deploy.debug) {
        	// This check has been added to cope with defragment in debug mode: SlotDefragment#setIdentity()
        	// will trigger calling this constructor with a source db yap class and a target db stream,
        	// thus _yapClass==null. There may be a better solution, since this call is just meant to
        	// skip the object header.
        	if(_yapClass!=null) {
	        	int ycID = _yapClass.getID();
		        if (classID != ycID) {
		        	System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + classID);
		        }
        	}
        }
        _headerAttributes = readAttributes(_marshallerFamily,reader);
    }

    public static ObjectHeader defrag(ReaderPair readers) {
    	YapReader source = readers.source();
    	YapReader target = readers.target();
		ObjectHeader header=new ObjectHeader(readers.context().systemTrans().stream(),null,source);
    	int newID =readers.mapping().mappedID(header.yapClass().getID());
        if (Deploy.debug) {
            target.readBegin(YapConst.YAPOBJECT);
        }
		header._marshallerFamily._object.writeObjectClassID(target,newID);		
		header._marshallerFamily._object.skipMarshallerInfo(target);
		readAttributes(header._marshallerFamily, target);
    	return header;
    }		
    		
    public ObjectMarshaller objectMarshaller() {
        return _marshallerFamily._object;
    }

	private MarshallerFamily readMarshallerFamily(YapReader reader, int classID) {
		boolean marshallerAware=marshallerAware(classID);
        byte marshallerVersion=0;
        if(marshallerAware) {
            marshallerVersion = reader.readByte();
        }
        MarshallerFamily marshallerFamily=MarshallerFamily.version(marshallerVersion);
		return marshallerFamily;
	}
    
    private static ObjectHeaderAttributes readAttributes(MarshallerFamily marshallerFamily,YapReader reader) {
    	return marshallerFamily._object.readHeaderAttributes(reader);
    }

    private boolean marshallerAware(int id) {
    	return id<0;
    }
    
    private int normalizeID(int id) {
    	return (id<0 ? -id : id);
    }

    public YapClass yapClass() {
        return _yapClass;
    }
}
