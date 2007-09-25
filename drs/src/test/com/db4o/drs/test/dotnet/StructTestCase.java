/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test.dotnet;

import java.util.*;

import com.db4o.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;

import db4ounit.*;

class Container {
	public Value value;
	
	public Container(Value value) {
		this.value = value;
	}
}

/**
 * @sharpen.struct
 */
class Value
{
	public int value;
	
	public Value(int value) {
		this.value = value;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Value)) {
			return false;
		}
		Value other = (Value)obj;
		return other.value == value;
	}
}

public class StructTestCase extends DrsTestCase {
	
	Container template = new Container(new Value(42));
	
	public void test() {
        storeToProviderA();
        replicateAllToProviderB();
    }

    void storeToProviderA() {
        TestableReplicationProviderInside provider = a().provider();
        provider.storeNew(template);
        provider.commit();
        ensureContent(template, provider);
    }

    void replicateAllToProviderB() {
        replicateAll(a().provider(), b().provider());
        ensureContent(template, b().provider());
    }

    private void ensureContent(Container container,
            TestableReplicationProviderInside provider) {
        ObjectSet result = provider.getStoredObjects(container.getClass());
        Assert.areEqual(1, result.size());

        Container c = next(result);
        Assert.areEqual(template.value, c.value);
    }

	private Container next(ObjectSet result) {
		final Iterator iterator = result.iterator();
		if (iterator.hasNext()) {
			return (Container) iterator.next();
		}
		return null;
	}
}
