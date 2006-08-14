/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

/**
 * @exclude
 */
public class MarshallerFamily {

    public static final boolean LEGACY = false;
    
    public final ArrayMarshaller _array;
    
    public final ClassMarshaller _class;
    
    public final FieldMarshaller _field;
    
    public final ObjectMarshaller _object;

    public final PrimitiveMarshaller _primitive;

    public final StringMarshaller _string;
    
    public final UntypedMarshaller _untyped;
    

    private final static MarshallerFamily[] allVersions     = new MarshallerFamily[] {
        new MarshallerFamily(
            new ArrayMarshaller0(),
            new ClassMarshaller(),
            new FieldMarshaller(),
            new ObjectMarshaller0(), 
            new PrimitiveMarshaller0(),
            new StringMarshaller0(),
            new UntypedMarshaller0()),
        new MarshallerFamily(
            new ArrayMarshaller1(),
            new ClassMarshaller(),
            new FieldMarshaller(),
            new ObjectMarshaller1(), 
            new PrimitiveMarshaller1(),
            new StringMarshaller1(),
            new UntypedMarshaller1())};

    private static final int                CURRENT_VERSION = LEGACY ? 0 : allVersions.length - 1;

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
        return forVersion(CURRENT_VERSION);
    }

}
