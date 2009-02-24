package com.db4o.db4ounit.optional.handlers;

import java.math.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
public class BigDecimalTypeHandlerTestCase extends AbstractInMemoryDb4oTestCase {

	private static final BigDecimal ZERO = new BigDecimal("0");
	private static final BigDecimal ONE = new BigDecimal("1");
	private static final BigDecimal LONG_MAX = new BigDecimal(String.valueOf(Long.MAX_VALUE));
	private static final BigDecimal LONG_MIN = new BigDecimal(String.valueOf(Long.MIN_VALUE));
	private static final BigDecimal LARGE = LONG_MAX.multiply(new BigDecimal("2"));
	
	private static final BigDecimal[] VALUES = {
		ZERO,
		ONE,
		LONG_MAX,
		LONG_MIN,
		LARGE
	};
	
	public static class Item {
		public int _id;
		public BigDecimal _typed;
		public Object _untyped;
		
		public Item(int id, BigDecimal value) {
			_id = id;
			_typed = value;
			_untyped = value;
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.registerTypeHandler(new SingleClassTypeHandlerPredicate(BigDecimal.class), new BigDecimalTypeHandler());
	}

	@Override
	protected void store() throws Exception {
		int idx = 0;
		for (BigDecimal bi : VALUES) {
			store(new Item(idx, bi));
			idx++;
		}
	}

	public void testRetrieval() {
		assertRetrievedAsStored();
	}

	public void testUpdate() throws Exception {
		ObjectSet<Item> result = db().query(Item.class);
		while (result.hasNext()) {
			Item item = result.next();
			item._typed = item._typed.multiply(item._typed);
			item._untyped = item._typed.add(ONE);
			store(item);
		}
		reopen();
		assertRetrieved(new Procedure4<Item>() {
			public void apply(Item item) {
				BigDecimal expectedBase = VALUES[item._id];
				Assert.areEqual(expectedBase.multiply(expectedBase), item._typed);
				Assert.areEqual(item._typed.add(ONE), item._untyped);
			}
		});
	}

	public void testDescendTyped() {
		Query query = newQuery(Item.class);
		query.descend("_typed").constrain(LARGE);
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(1, result.size());
		final Item found = result.next();
		Assert.areEqual(LARGE, found._typed);
		Assert.areEqual(LARGE, found._untyped);
	}
	
	public void testDelete() throws Exception {
		deleteAll(Item.class);
		assertOccurrences(Item.class, 0);
	}
	
	public void testDefrag() throws Exception {
		defragment();
		assertRetrievedAsStored();
	}
	
	private void assertRetrieved(Procedure4<Item> check) {
		Query query = newQuery(Item.class);
		query.descend("_id").orderAscending();
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(VALUES.length, result.size());
		while (result.hasNext()) {
			check.apply(result.next());
		}
	}

	private void assertRetrievedAsStored() {
		assertRetrieved(new Procedure4<Item>() {
			public void apply(Item item) {
				BigDecimal expected = VALUES[item._id];
				Assert.areEqual(expected, item._typed);
				Assert.areEqual(expected, item._untyped);
			}
		});
	}

}
