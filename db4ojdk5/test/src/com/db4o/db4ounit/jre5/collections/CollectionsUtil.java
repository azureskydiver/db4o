/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;
import com.db4o.ext.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude 
 * @sharpen.ignore
 */
public class CollectionsUtil {
    @SuppressWarnings("unchecked")
    public static ArrayList4<Integer> retrieveAndAssertNullArrayList4(
            ExtObjectContainer oc, Reflector reflector) throws Exception {
        ArrayList4<Integer> list = (ArrayList4<Integer>) AbstractDb4oTestCase
                .retrieveOnlyInstance(oc, ArrayList4.class);
        Assert.isFalse(oc.isActive(list));
        return list;
    }

    private static Object getField(Reflector reflector, Object parent,
            String fieldName) {
        ReflectClass parentClazz = reflector.forObject(parent);
        ReflectField field = parentClazz.getDeclaredField(fieldName);
        field.setAccessible();
        return field.get(parent);
    }

    private static void assertRetrieveStatus(Reflector reflector,
            ArrayMap4<String, Integer> map) {
        Assert.isNull(getField(reflector, map, "_keys"));
        Assert.isNull(getField(reflector, map, "_values"));
        Assert.areEqual(new Integer(0), getField(reflector, map, "_startIndex"));
        Assert.areEqual(new Integer(0), getField(reflector, map, "_endIndex"));
    }

    @SuppressWarnings("unchecked")
    public static ArrayMap4<String, Integer> retrieveMapFromDB(
            ExtObjectContainer oc, Reflector reflector) {
        ArrayMap4<String, Integer> map = (ArrayMap4<String, Integer>) AbstractDb4oTestCase
                .retrieveOnlyInstance(oc, ArrayMap4.class);
        assertRetrieveStatus(reflector, map);
        return map;
    }
}
