/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.inside.convert.conversions.*;

/**
 * @exclude
 */
public class MarshallerFamily {
    
    
    public static class Version{
        
        public static final int PRE_MARSHALLER = 0;
        
        public static final int MARSHALLER = 1;
        
        public static final int BTREE_FIELD_INDEXES = 2; 
        
    }
    
    private static int FAMILY_VERSION = Version.MARSHALLER;

    public static final boolean BTREE_FIELD_INDEX = (FAMILY_VERSION == Version.BTREE_FIELD_INDEXES);
    
    public static final boolean OLD_FIELD_INDEX = (FAMILY_VERSION < Version.BTREE_FIELD_INDEXES);
    
    public final ArrayMarshaller _array;
    
    public final ClassMarshaller _class;
    
    public final FieldMarshaller _field;
    
    public final ObjectMarshaller _object;

    public final PrimitiveMarshaller _primitive;

    public final StringMarshaller _string;
    
    public final UntypedMarshaller _untyped;

    private final int _converterVersion;


    private final static MarshallerFamily[] allVersions = new MarshallerFamily[] {
        
        // LEGACY => before 5.4
        
        new MarshallerFamily(
            0,
            new ArrayMarshaller0(),
            new ClassMarshaller0(),
            new FieldMarshaller0(),
            new ObjectMarshaller0(), 
            new PrimitiveMarshaller0(),
            new StringMarshaller0(),
            new UntypedMarshaller0()),
        
        new MarshallerFamily(
            ClassIndexesToBTrees_5_5.VERSION,
            new ArrayMarshaller1(),
            new ClassMarshaller1(),
            new FieldMarshaller0(),
            new ObjectMarshaller1(), 
            new PrimitiveMarshaller1(),
            new StringMarshaller1(),
            new UntypedMarshaller1()),
    
    new MarshallerFamily(
        FieldIndexesToBTrees_5_7.VERSION,
        new ArrayMarshaller1(),
        new ClassMarshaller2(),
        new FieldMarshaller1(),
        new ObjectMarshaller1(), 
        new PrimitiveMarshaller1(),
        new StringMarshaller1(),
        new UntypedMarshaller1())};

    private MarshallerFamily(
            int converterVersion,
            ArrayMarshaller arrayMarshaller,
            ClassMarshaller classMarshaller,
            FieldMarshaller fieldMarshaller,
            ObjectMarshaller objectMarshaller,
            PrimitiveMarshaller primitiveMarshaller, 
            StringMarshaller stringMarshaller,
            UntypedMarshaller untypedMarshaller) {
        _converterVersion = converterVersion;
        _array = arrayMarshaller;
        _array._family = this;
        _class = classMarshaller;
        _class._family = this;
        _field = fieldMarshaller;
        _object = objectMarshaller;
        _object._family = this;
        _primitive = primitiveMarshaller;
        _primitive._family = this;
        _string = stringMarshaller;
        _untyped = untypedMarshaller;
        _untyped._family = this;
    }

    public static MarshallerFamily version(int n) {
        return allVersions[n];
    }

    public static MarshallerFamily current() {
        return version(FAMILY_VERSION);
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

}
