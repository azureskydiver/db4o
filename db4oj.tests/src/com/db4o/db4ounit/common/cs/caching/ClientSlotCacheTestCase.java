package com.db4o.db4ounit.common.cs.caching;

import static com.db4o.foundation.Environments.my;

import com.db4o.*;
import com.db4o.cs.caching.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClientSlotCacheTestCase extends AbstractDb4oTestCase implements OptOutAllButNetworkingCS {

	public void testSlotCacheIsTransactionBased() {
		
		container().withEnvironment(new Runnable() { public void run() {
			
			final Transaction t1 = newTransaction();
			final Transaction t2 = newTransaction();
			
			final ClientSlotCache subject = my(ClientSlotCache.class);
			
			final ByteArrayBuffer slot = new ByteArrayBuffer(0);
			subject.add(t1, 42, slot);
			Assert.areSame(slot, subject.get(t1, 42));
			
			Assert.isNull(subject.get(t2, 42));
			
			t1.commit();
			Assert.isNull(subject.get(t1, 42));
			
		}});
	}
	
	public void testCacheIsCleanUponTransactionCommit() {
		
		assertCacheIsCleanAfterTransactionOperation(new Procedure4<Transaction>() {
			public void apply(Transaction value) {
				value.commit();
            }
		});
	}
	
	public void testCacheIsCleanUponTransactionRollback() {
		
		assertCacheIsCleanAfterTransactionOperation(new Procedure4<Transaction>() {
			public void apply(Transaction value) {
				value.rollback();
            }
		});
	}

	private void assertCacheIsCleanAfterTransactionOperation(final Procedure4<Transaction> operation) {
	    container().withEnvironment(new Runnable() { public void run() {
			
			final ClientSlotCache subject = my(ClientSlotCache.class);
			
			final ByteArrayBuffer slot = new ByteArrayBuffer(0);
			subject.add(trans(), 42, slot);
			
			operation.apply(trans());
			
			Assert.isNull(subject.get(trans(), 42));
			
		}});
    }
	
	public void testSlotCacheEntryIsPurgedUponActivation() {
		
		final Item item = new Item();
		db().store(item);
		final int id = (int)db().getID(item);
		db().purge(item);
		
		db().configure().clientServer().prefetchDepth(1);
		
		container().withEnvironment(new Runnable() { public void run() {
			
			final ClientSlotCache subject = my(ClientSlotCache.class);
			
			final ObjectSet<Item> items = newQuery(Item.class).execute();
			Assert.isNotNull(subject.get(trans(), id));
			Assert.isNotNull(items.next());
			Assert.isNull(subject.get(trans(), id), "activation should have purged slot from cache");
			
		}});
		
	}
	
	public static class Item {
	}
	
}
