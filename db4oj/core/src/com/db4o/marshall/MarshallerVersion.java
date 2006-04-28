/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

/**
 * @exclude
 */
public class MarshallerVersion {
    
    private ObjectMarshaller _objectMarshaller;
    
    private final static MarshallerVersion[] allVersions = new MarshallerVersion[]{
        new MarshallerVersion(new ObjectMarshaller0()),
        new MarshallerVersion(new ObjectMarshaller0()),
    };
    
    private static final int currentVersion = allVersions.length - 1;
    
    
    private MarshallerVersion(ObjectMarshaller objectMarshaller){
        _objectMarshaller = objectMarshaller;
    }
    
    public static MarshallerVersion forVersionNumber(int n){
        return allVersions[n];
    }
    
    public static ObjectMarshaller objectMarshaller(int versionNumber){
        return forVersionNumber(versionNumber)._objectMarshaller;
    }
    
    public static ObjectMarshaller objectMarshaller(){
        return objectMarshaller(currentVersion); 
    }
    
    
    
    
    
    
}
