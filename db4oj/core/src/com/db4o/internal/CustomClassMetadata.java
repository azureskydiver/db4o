/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.reflect.*;


/**
 * @exclude
 */
public class CustomClassMetadata extends ClassMetadata{

    CustomClassMetadata(ObjectContainerBase stream, ReflectClass reflector) {
        super(stream, reflector);
    }

}
