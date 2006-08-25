/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public class ObjectHeader {
    
    public final YapClass _yapClass;
    
    public final MarshallerFamily _marshallerFamily;
    
    public final ObjectHeaderAttributes _headerAttributes;
    
    public ObjectHeader(YapStream stream, YapReader reader){
    	this(stream,null,reader);
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
            int ycID = yc.getID();
            if (classID != ycID) {
                System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + classID);
            }
        }
        _headerAttributes = readAttributes(_marshallerFamily,reader);
    }

    public static ObjectHeader defrag(YapClass yapClass,YapReader source,YapReader target,IDMapping mapping) {
    	ObjectHeader header=new ObjectHeader(null,yapClass,source);
    	int newID = mapping.mappedID(yapClass.getID());
		header._marshallerFamily._object.writeObjectClassID(target,newID);		
		header._marshallerFamily._object.skipMarshallerInfo(target);
		// TODO defrag for attributes
		readAttributes(header._marshallerFamily, target);
		System.out.println("MAP CLASS REF "+yapClass.getID()+" -> "+newID+", "+source._offset+"/"+target._offset);
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
        MarshallerFamily marshallerFamily=MarshallerFamily.forVersion(marshallerVersion);
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
}
