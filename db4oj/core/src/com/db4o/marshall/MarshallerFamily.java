/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

/**
 * @exclude
 */
public class MarshallerFamily {

    public static final boolean            LEGACY           = true;

    public final ObjectMarshaller           _object;

    public final PrimitiveMarshaller       _primitive;

    public final StringMarshaller          _string;
    

    private final static MarshallerFamily[] allVersions     = new MarshallerFamily[] {
        new MarshallerFamily(new ObjectMarshaller0(), new PrimitiveMarshaller0(),
            new StringMarshaller0()),
        new MarshallerFamily(new ObjectMarshaller1(), new PrimitiveMarshaller1(),
            new StringMarshaller1())                        };

    private static final int                CURRENT_VERSION = LEGACY ? 0 : allVersions.length - 1;

    private MarshallerFamily(ObjectMarshaller objectMarshaller,
        PrimitiveMarshaller primitiveMarshaller, StringMarshaller stringMarshaller) {
        _object = objectMarshaller;
        _object._family = this;
        _primitive = primitiveMarshaller;
        _primitive._family = this;
        _string = stringMarshaller;
    }

    public static MarshallerFamily forVersion(int n) {
        return allVersions[n];
    }

    public static MarshallerFamily current() {
        return forVersion(CURRENT_VERSION);
    }

}
