/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.convert.conversions.*;

/**
 * Represents a db4o file format version, assembles all the marshallers
 * needed to read/write this specific version.
 * 
 * A marshaller knows how to read/write certain types of values from/to its
 * representation on disk for a given db4o file format version.
 * 
 * Responsibilities are somewhat overlapping with TypeHandler's.
 * 
 * @exclude
 */
public class MarshallerFamily {
    
    
    public static class FamilyVersion{
        
        public static final int PRE_MARSHALLER = 0;
        
        public static final int MARSHALLER = 1;
        
        public static final int BTREE_FIELD_INDEXES = 2; 
        
    }
   
    private static int CURRENT_VERSION = FamilyVersion.BTREE_FIELD_INDEXES;
    
    public final ClassMarshaller _class;
    
    public final FieldMarshaller _field;
    
    public final ObjectMarshaller _object;

    public final PrimitiveMarshaller _primitive;

    public final StringMarshaller _string;
    
    private final int _converterVersion;
    
    private final int _handlerVersion;

    private final static MarshallerFamily[] allVersions;
    static {
    	
    	allVersions = new MarshallerFamily[HandlerRegistry.HANDLER_VERSION + 1];
    	allVersions[0] =
	        // LEGACY => before 5.4
	        new MarshallerFamily(
	            0,
	            0,
	            new ClassMarshaller0(),
	            new FieldMarshaller0(),
	            new ObjectMarshaller0(), 
	            new PrimitiveMarshaller0(),
	            new StringMarshaller0());
    	
    	allVersions[1] =
	        new MarshallerFamily(
	            ClassIndexesToBTrees_5_5.VERSION,
	            1,
	            new ClassMarshaller1(),
	            new FieldMarshaller0(),
	            new ObjectMarshaller1(), 
	            new PrimitiveMarshaller1(),
	            new StringMarshaller1());
    	for (int i = 2; i < allVersions.length; i++) {
    	    allVersions[i] = latestFamily(i);
        }
    }

    public MarshallerFamily(
            int converterVersion,
            int handlerVersion,
            ClassMarshaller classMarshaller,
            FieldMarshaller fieldMarshaller,
            ObjectMarshaller objectMarshaller,
            PrimitiveMarshaller primitiveMarshaller, 
            StringMarshaller stringMarshaller) {
        _converterVersion = converterVersion;
        _handlerVersion = handlerVersion;
        _class = classMarshaller;
        _class._family = this;
        _field = fieldMarshaller;
        _object = objectMarshaller;
        _object._family = this;
        _primitive = primitiveMarshaller;
        _primitive._family = this;
        _string = stringMarshaller;
    }
    
    public static MarshallerFamily latestFamily(int version){
        return new MarshallerFamily(
            FieldIndexesToBTrees_5_7.VERSION,
            version,
            new ClassMarshaller2(),
            new FieldMarshaller1(),
            new ObjectMarshaller1(), 
            new PrimitiveMarshaller1(),
            new StringMarshaller1());
    }

    public static MarshallerFamily version(int n) {
        return allVersions[n];
    }

    public static MarshallerFamily current() {
        if(CURRENT_VERSION < FamilyVersion.BTREE_FIELD_INDEXES){
            throw new IllegalStateException("Using old marshaller versions to write database files is not supported, source code has been removed.");
        }
        return version(CURRENT_VERSION);
    }
    
    public static MarshallerFamily forConverterVersion(int n){
        MarshallerFamily result = allVersions[0];
        for (int i = 1; i < allVersions.length; i++) {
            if(allVersions[i]._converterVersion > n){
                return result;
            }
            result = allVersions[i]; 
        }
        return result;
    }
    
    public int handlerVersion(){
    	return _handlerVersion;
    }
    
}
