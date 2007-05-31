/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.ObjectContainer;
import com.db4o.config.*;
import com.db4o.internal.ReflectException;

import db4ounit.*;
import db4ounit.extensions.AbstractDb4oTestCase;


public class StoreExceptionBubblesUpTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new StoreExceptionBubblesUpTestCase().runClientServer();
	}
	
	public static final class ItemTranslator implements ObjectTranslator {

		public void onActivate(ObjectContainer container,
				Object applicationObject, Object storedObject) {		
		}

		public Object onStore(ObjectContainer container,
				Object applicationObject) {
			throw new ItemException();
		}

		public Class storedClass() {
			return Item.class;
		}
		
	}
	
	protected void configure(Configuration config) {
		config.objectClass(Item.class).translate(new ItemTranslator());
	}
	
	public void test() {
		CodeBlock exception = new CodeBlock() {
					public void run() throws Throwable {
						store(new Item());
					}
				};
		Assert.expect(ReflectException.class, exception);
	}

}
