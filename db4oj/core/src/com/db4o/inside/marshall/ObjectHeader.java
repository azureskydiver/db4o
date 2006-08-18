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
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPOBJECT);
        }
        int classID = reader.readInt();
        byte marshallerVersion = 0;
        
        if(classID == 0){
            _yapClass = null;
        }else if(classID > 0){
            _yapClass = stream.getYapClass(classID);
        }else{
            _yapClass = stream.getYapClass(- classID);
            marshallerVersion = reader.readByte();
        }
        _marshallerFamily = MarshallerFamily.forVersion(marshallerVersion);
        
        _headerAttributes = _marshallerFamily._object.readHeaderAttributes(reader);
        
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
        byte marshallerVersion=0;
        if(id < 0) {
            marshallerVersion = reader.readByte();
        }
        _marshallerFamily = MarshallerFamily.forVersion(marshallerVersion);
        if (Deploy.debug) {
            if(id < 0){
                id = -id;
            }
            int ycID = yc.getID();
            if (id != ycID) {
                System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + id);
            }
        }
        _headerAttributes = _marshallerFamily._object.readHeaderAttributes(reader);
    }
    
    public ObjectMarshaller objectMarshaller() {
        return _marshallerFamily._object;
    }

}
