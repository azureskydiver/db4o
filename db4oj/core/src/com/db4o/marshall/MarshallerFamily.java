/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

/**
 * @exclude
 */
public class MarshallerFamily {

    private static final boolean            DEBUG           = false;

    public final ObjectMarshaller           _object;

    public final PrimitiveMarshaller0       _primitive;

    public final StringMarshaller0          _string;
    

    private final static MarshallerFamily[] allVersions     = new MarshallerFamily[] {
        new MarshallerFamily(new ObjectMarshaller0(), new PrimitiveMarshaller0(),
            new StringMarshaller0()),
        new MarshallerFamily(new ObjectMarshaller0(), new PrimitiveMarshaller0(),
            new StringMarshaller0())                        };

    private static final int                CURRENT_VERSION = DEBUG ? 0 : allVersions.length - 1;

    private MarshallerFamily(ObjectMarshaller objectMarshaller,
        PrimitiveMarshaller0 primitiveMarshaller, StringMarshaller0 stringMarshaller) {
        _object = objectMarshaller;
        _object._family = this;
        _primitive = primitiveMarshaller;
        _string = stringMarshaller;
    }

    public static MarshallerFamily forVersion(int n) {
        return allVersions[n];
    }

    public static MarshallerFamily current() {
        return forVersion(CURRENT_VERSION);
    }

}
