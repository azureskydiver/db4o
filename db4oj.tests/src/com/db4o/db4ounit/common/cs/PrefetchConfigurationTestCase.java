package com.db4o.db4ounit.common.cs;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.messages.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class PrefetchConfigurationTestCase extends ClientServerTestCaseBase implements OptOutAllButNetworkingCS{
	
	public void testDefaultPrefetchDepth() {
		Assert.areEqual(0, client().config().prefetchDepth());
	}
	
	public void testPrefetchingBehaviorForClassOnlyQuery() {
		
		final Query query = client().query();
		query.constrain(Item.class);
		
		assertPrefetchingBehaviorFor(query, Msg.GET_INTERNAL_IDS);
	}
	
	public void testPrefetchingBehaviorForConstrainedQuery() {
		
		final Query query = client().query();
		query.constrain(Item.class);
		query.descend("field").constrain(null);
		
		assertPrefetchingBehaviorFor(query, Msg.QUERY_EXECUTE);
	}

	private void assertPrefetchingBehaviorFor(final Query query, final MsgD expectedFirstMessage) {
	    queryWarmUp();
		
		storeFiveItems();
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(1);
		
		final List<Msg> messages = MessageCollector.forServerDispatcher(serverDispatcher());
		
		final ObjectSet result = query.execute();
		assertMessages(messages, expectedFirstMessage);
		messages.clear();
		
		result.next(); // should have been prefetched already
		result.next();
		
		assertMessages(messages);
		
		result.next(); // causes prefetch
		assertMessages(messages, Msg.READ_MULTIPLE_OBJECTS);
		messages.clear();
		
		result.next(); // prefetched before
		assertMessages(messages);
		
		result.next(); // last prefetch
		assertMessages(messages, Msg.READ_MULTIPLE_OBJECTS);
		messages.clear();
		
		Assert.isFalse(result.hasNext());
		assertMessages(messages);
    }

	private void assertMessages(List<Msg> actualMessages, MsgD... expectedMessages) {
		Iterator4Assert.areEqual(expectedMessages, Iterators.iterator(actualMessages));
    }

	private void queryWarmUp() {
		// ensures classmetadata exists for query objects
	    Assert.areEqual(0, client().query(Item.class).size());
    }

	private void storeFiveItems() {
	    for (int i=0; i<5; ++i) {
			final Item item = new Item();
			client().store(item);
			client().purge(item);
		}
	    client().commit();
    }
	
	public static class Item {
	}

}
