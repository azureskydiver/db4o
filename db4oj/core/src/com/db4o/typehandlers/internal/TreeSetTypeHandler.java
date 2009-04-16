/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.typehandlers.internal;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TreeSetTypeHandler implements InstantiatingTypeHandler {
	
	public void writeInstantiation(WriteContext context, Object obj) {
		final Comparator comparator = ((TreeSet)obj).comparator();
		context.writeObject(comparator);
	}
	
	public Object instantiate(ReadContext context) {
		final Comparator comparator = (Comparator)context.readObject();
		return new TreeSet(comparator);
	}

	public void activate(ReferenceActivationContext context) {
		// already handled by CollectionTypeHandler
	}
	
	public void write(WriteContext context, Object obj) {
		// already handled by CollectionTypeHandler
	}

	public boolean canHold(ReflectClass type) {
		// TODO: must go to QueryableTypeHandler
		return true;
	}

	public void defragment(DefragmentContext context) {
		context.copyID();
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		// TODO: when the TreeSet is deleted
		// the comparator should be deleted too
		// TODO: What to do about shared comparators?
		// context.deleteObject();
	}
}
