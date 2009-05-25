package com.db4o.db4ounit.common.cs;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.messages.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class PrefetchConfigurationTestCase extends ClientServerTestCaseBase implements OptOutAllButNetworkingCS{
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		ensureQueryGraphClassMetadataHasBeenExchanged();
	}
	
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
		query.descend("child").constrain(null);
		
		assertPrefetchingBehaviorFor(query, Msg.QUERY_EXECUTE);
	}
	
	public void testRefreshIsUnaffectedByPrefetchingBehavior() {
		
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = openNewClient();
		
		oc1.configure().clientServer().prefetchDepth(1);
		oc2.configure().clientServer().prefetchDepth(1);
		
		try {
			final Item itemFromClient1 = new RootItem(new Item());
			oc1.store(itemFromClient1);
			oc1.commit();
			
			itemFromClient1.child = null;
			oc1.store(itemFromClient1);
			
			Item itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			Assert.isNotNull(itemFromClient2.child);

			oc1.rollback();
			itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			oc2.refresh(itemFromClient2, Integer.MAX_VALUE);
			Assert.isNotNull(itemFromClient2.child);

			oc1.commit();
			itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			Assert.isNotNull(itemFromClient2.child);

			oc1.store(itemFromClient1);
			oc1.commit();
			oc2.refresh(itemFromClient2, Integer.MAX_VALUE);
			itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			Assert.isNull(itemFromClient2.child);
		} finally {
			oc2.close();
		}
		
	}
	
	public void testPrefetchingDepth2Behavior() {
		
		storeDepth2Graph(); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(2);
		
		final Query query = queryForItemsWithChild();
		
		// TODO: items to level 3
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(),
			new Depth2Stimulus(),
			new Depth2Stimulus(Msg.READ_MULTIPLE_OBJECTS)
		});
    }
	
	public void testDepth2WithPrefetching1() {
		
		storeDepth2Graph(); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(1);
		
		final Query query = queryForItemsWithChild();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(Msg.READ_READER_BY_ID),
			new Depth2Stimulus(Msg.READ_READER_BY_ID),
			new Depth2Stimulus(Msg.READ_MULTIPLE_OBJECTS, Msg.READ_READER_BY_ID),
		});
    }

	private Query queryForItemsWithChild() {
	    final Query query = client().query();
		query.constrain(Item.class);
		query.descend("child").constrain(null).not();
	    return query;
    }

	private void storeDepth2Graph() {
	    storeAllAndPurge(
				new Item(new Item()),
				new Item(new Item()),
				new Item(new Item()),
				new Item());
    }

	private void assertPrefetchingBehaviorFor(final Query query, final MsgD expectedFirstMessage) {
		
		storeFlatItemGraph();
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(1);
		
		assertQueryIterationProtocol(query, expectedFirstMessage, new Stimulus[] {
				new Stimulus(),
				new Stimulus(),
				new Stimulus(Msg.READ_MULTIPLE_OBJECTS),
				new Stimulus(),
				new Stimulus(Msg.READ_MULTIPLE_OBJECTS),
		});
    }

	private void assertQueryIterationProtocol(final Query query, final MsgD expectedResultMessage, Stimulus[] stimuli) {
	    final List<Msg> messages = MessageCollector.forServerDispatcher(serverDispatcher());
		
		final ObjectSet<Item> result = query.execute();
		assertMessages(messages, expectedResultMessage);
		messages.clear();
		
		for (Stimulus stimulus : stimuli) {
			stimulus.actUpon(result);
			assertMessages(messages, stimulus.expectedMessagesAfter);
			messages.clear();
        }
		
		Assert.isFalse(result.hasNext());
		assertMessages(messages);
    }
	
	private class Depth2Stimulus extends Stimulus {
		
		public Depth2Stimulus(MsgD... expectedMessagesAfter) {
			super(expectedMessagesAfter);
        }
		
	    @Override
	    public void actUpon(ObjectSet<Item> result) {
	    	actUpon(result.next());
	    }

		protected void actUpon(final Item item) {
	        Assert.isNotNull(item.child);
	        db().activate(item.child, 1); // ensure no further messages are exchange
        }
    }

	public static class Stimulus {
		public final MsgD[] expectedMessagesAfter;

		public Stimulus(MsgD... expectedMessagesAfter) {
			this.expectedMessagesAfter = expectedMessagesAfter;
        }

		public void actUpon(ObjectSet<Item> result) {
	        Assert.isNotNull(result.next());
        }
    }

	private void assertMessages(List<Msg> actualMessages, MsgD... expectedMessages) {
		Iterator4Assert.areEqual(expectedMessages, Iterators.iterator(actualMessages));
    }

	private void ensureQueryGraphClassMetadataHasBeenExchanged() {
		// ensures classmetadata exists for query objects
	    final Query query = client().query();
	    query.constrain(Item.class);
	    query.descend("child").constrain(null).not();
	    query.descend("child").constrain(null);
		Assert.areEqual(0, query.execute().size());
    }

	private void storeFlatItemGraph() {
		storeAllAndPurge(
				new Item(),
				new Item(),
				new Item(),
				new Item(),
				new Item());
	}
	
	private void storeAllAndPurge(Item... items) {
	    for (Item item : items) {
			storeAndPurge(item);
		}
	    client().commit();
    }

	private void storeAndPurge(Item item) {
	    client().store(item);
	    purge(item);
    }

	private void purge(Item item) {
	    client().purge(item);
	    if (null != item.child) {
	    	purge(item.child);
	    }
    }
	
	public static class Item {
		public Item(Item child) {
			this.child = child;
        }
		
		public Item() {
		}

		public Item child;
	}
	
	public static class RootItem extends Item {

		public RootItem() {
	        super();
        }

		public RootItem(Item child) {
	        super(child);
        }
		
	}

}
