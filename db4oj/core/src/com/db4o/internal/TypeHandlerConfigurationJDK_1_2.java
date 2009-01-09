/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.util.*;

import com.db4o.internal.collections.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TypeHandlerConfigurationJDK_1_2 extends TypeHandlerConfiguration{
	
	public TypeHandlerConfigurationJDK_1_2(Config4Impl config) {
		super(config);
        listTypeHandler(new CollectionTypeHandler());
        mapTypeHandler(new MapTypeHandler());
	}

	public void apply(){
        registerCollection(AbstractCollection.class);
		ignoreFieldsOn(AbstractList.class);
		ignoreFieldsOn(AbstractSequentialList.class);
		ignoreFieldsOn(LinkedList.class);
		ignoreFieldsOn(ArrayList.class);
		ignoreFieldsOn(Vector.class);
		ignoreFieldsOn(Stack.class);
		ignoreFieldsOn(AbstractSet.class);
		ignoreFieldsOn(HashSet.class);
		
		registerMap(AbstractMap.class);
		registerMap(Hashtable.class);
		
		ignoreFieldsOn(HashMap.class);
		ignoreFieldsOn(WeakHashMap.class);
		
		registerTypeHandlerFor(BigSet.class, new BigSetTypeHandler());
	}

}
