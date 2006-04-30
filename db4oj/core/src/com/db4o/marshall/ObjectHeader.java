/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public class ObjectHeader {
    
    public final YapClass _yapClass;
    
    public final MarshallerFamily _marshallerFamily;
    
    public ObjectHeader(YapStream stream, YapReader reader){
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPOBJECT);
        }
        int id = reader.readInt();
        byte marshallerVersion = 0;
        
        if(id == 0){
            _yapClass = null;
        }else if(id > 0){
            _yapClass = stream.getYapClass(id);
        }else{
            _yapClass = stream.getYapClass(- id);
            marshallerVersion = reader.readByte();
        }
        _marshallerFamily = MarshallerFamily.forVersion(marshallerVersion);
    }
    
    public ObjectHeader(YapWriter writer){
        this(writer.getStream(), writer);
    }
    
    public ObjectHeader(YapStream stream, YapClass yc, YapReader reader){
        _yapClass = yc;
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPOBJECT);
        }
        int id = reader.readInt();
        byte mIdx=0;
        if(id < 0) {
            if (Deploy.debug) {
                id=-id;
            }
            mIdx = reader.readByte();
        }
        _marshallerFamily = MarshallerFamily.forVersion(mIdx);
        if (Deploy.debug) {
            int ycID = yc.getID();
            if (id != ycID) {
                System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + id);
            }
        }
    }
    
    /**
     * @return Returns the marshaller.
     */
    public ObjectMarshaller objectMarshaller() {
        return _marshallerFamily._object;
    }
    

}
