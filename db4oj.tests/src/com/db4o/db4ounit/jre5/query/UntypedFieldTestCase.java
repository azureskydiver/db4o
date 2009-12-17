/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.query;

import java.math.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove(decaf.Platform.ALL)
public class UntypedFieldTestCase extends AbstractDb4oTestCase {

	private static final BigInteger VALUE = BigInteger.valueOf(42);

	public static class Item {
		public Item(BigInteger id) {
			untypedBigInteger = id;
		}
		
		public Object untypedBigInteger;
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new BigMathSupport());
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(VALUE));
		store(new Item(BigInteger.valueOf(0)));
	}
	
	public void testDescendTwice() {
		Query query = newQuery(Item.class);
		query.descend("untypedBigInteger").constrain(BigInteger.valueOf(0)).greater();
		ObjectSet<Item> result = query.execute();
		
		Assert.areEqual(1, result.size());
		Item actual = result.get(0);
		Assert.areEqual(VALUE, actual.untypedBigInteger);		
	}
}
