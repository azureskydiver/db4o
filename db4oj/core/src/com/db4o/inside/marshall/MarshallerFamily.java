/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

/**
 * @exclude
 */
public class MarshallerFamily {
    
    
    public static class Version{
        
        public static final int LEGACY = 0;
        
        public static final int MARSHALLER = 1;
        
        public static final int BTREE_FIELD_INDEX = 2; 
        
    }
    
    public static int VERSION = Version.MARSHALLER;

    public static final boolean LEGACY = (VERSION == Version.LEGACY);
    
    public static final boolean OLD_CLASS_INDEX = LEGACY;
    
    public static final boolean BTREE_FIELD_INDEX = (VERSION == Version.BTREE_FIELD_INDEX);
    
    public static final boolean OLD_FIELD_INDEX = (VERSION < Version.BTREE_FIELD_INDEX);
    
    public final ArrayMarshaller _array;
    
    public final ClassMarshaller _class;
    
    public final FieldMarshaller _field;
    
    public final ObjectMarshaller _object;

    public final PrimitiveMarshaller _primitive;

    public final StringMarshaller _string;
    
    public final UntypedMarshaller _untyped;


    private final static MarshallerFamily[] allVersions     = new MarshallerFamily[] {
        
        // LEGACY => before 5.4
        
        new MarshallerFamily(
            new ArrayMarshaller0(),
            new ClassMarshaller(),
            new FieldMarshaller0(),
            new ObjectMarshaller0(), 
            new PrimitiveMarshaller0(),
            new StringMarshaller0(),
            new UntypedMarshaller0()),
        
        // 5.4 => 5.5
            
        new MarshallerFamily(
            new ArrayMarshaller1(),
            new ClassMarshaller(),
            new FieldMarshaller0(),
            new ObjectMarshaller1(), 
            new PrimitiveMarshaller1(),
            new StringMarshaller1(),
            new UntypedMarshaller1()),
    
        // BTREE_FIELD_INDEX release
    
    new MarshallerFamily(
        new ArrayMarshaller1(),
        new ClassMarshaller(),
        new FieldMarshaller1(),
        new ObjectMarshaller1(), 
        new PrimitiveMarshaller1(),
        new StringMarshaller1(),
        new UntypedMarshaller1())};

    private MarshallerFamily(
            ArrayMarshaller arrayMarshaller,
            ClassMarshaller classMarshaller,
            FieldMarshaller fieldMarshaller,
            ObjectMarshaller objectMarshaller,
            PrimitiveMarshaller primitiveMarshaller, 
            StringMarshaller stringMarshaller,
            UntypedMarshaller untypedMarshaller) {
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

    public static MarshallerFamily forVersion(int n) {
        return allVersions[n];
    }

    public static MarshallerFamily current() {
        return forVersion(VERSION);
    }

}
