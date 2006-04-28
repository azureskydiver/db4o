/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public class ObjectHeader {
    
    public final YapClass _yapClass;
    
    public final ObjectMarshaller _marshaller;
    
    public ObjectHeader(YapStream stream, YapReader reader){
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPOBJECT);
        }
        int id = reader.readInt();
        
        if(id == 0){
            _yapClass = null;
            _marshaller = MarshallerVersion.objectMarshaller(0);
        }else if(id > 0){
            _yapClass = stream.getYapClass(id);
            _marshaller = MarshallerVersion.objectMarshaller(0);
        }else{
            _yapClass = stream.getYapClass(- id);
            byte b = reader.readByte();
            _marshaller = MarshallerVersion.objectMarshaller(b);
        }
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
        _marshaller = MarshallerVersion.objectMarshaller(mIdx);
        if (Deploy.debug) {
            int ycID = yc.getID();
            if (id != ycID) {
                System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + id);
            }
        }

        
    }
    
    public static void skip(YapStream stream, YapClass yc, YapReader reader){
        new ObjectHeader(stream, yc, reader);
    }
    

}
