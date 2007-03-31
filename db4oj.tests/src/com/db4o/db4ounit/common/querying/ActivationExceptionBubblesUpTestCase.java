/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.ObjectContainer;
import com.db4o.config.*;
import com.db4o.query.Predicate;

import db4ounit.*;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.OptOutCS;


public class ActivationExceptionBubblesUpTestCase extends AbstractDb4oTestCase implements OptOutCS {
	
	public static final class ItemException extends RuntimeException {
	}
	
	public static final class Item {
	}
	
	public static final class ItemTranslator implements ObjectTranslator {

		public void onActivate(ObjectContainer container,
				Object applicationObject, Object storedObject) {
			
			throw new ItemException();
		}

		public Object onStore(ObjectContainer container,
				Object applicationObject) {
			return applicationObject;
		}

		public Class storedClass() {
			return Item.class;
		}
		
	}
	
	protected void configure(Configuration config) {
		config.objectClass(Item.class).translate(new ItemTranslator());
	}
	
	protected void store() throws Exception {
		store(new Item());
	}
	
	public void test() {
		Assert.expect(ItemException.class, new CodeBlock() {
			public void run() throws Exception {
				db().query(new Predicate() {
					public boolean match(Item candidate) {
						return candidate.getClass() == Item.class;
					}
				}).hasNext();
			}
		});
	}

}
