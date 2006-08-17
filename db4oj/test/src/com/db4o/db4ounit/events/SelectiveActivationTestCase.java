package com.db4o.db4ounit.events;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.fixtures.Db4oSolo;
import com.db4o.events.*;
import com.db4o.ObjectSet;
import com.db4o.Db4o;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

/**
 * User: treeder
 * Date: Aug 16, 2006
 * Time: 3:21:50 PM
 */
public class SelectiveActivationTestCase extends Db4oTestCase {
    private boolean debug = false;

    protected void configure() {
        enableCascadeOnDelete();
        Db4o.configure().activationDepth(1);
    }

    protected void store() {
        Item c = new Item("C", null);
        Item b = new Item("B", c);
        Item a = new Item("A", b);
        db().set(a);
    }

    public void testActivateFullTree() throws Exception {
        reopen();
        print("test activate full tree");
        Assert.areEqual(3, queryItems().size());

        Item a = queryItem("A");
        Assert.isNull(a.child.child); // only A and B should be activated.

        reopen(); // start fresh

        // Add listener for activated event
        eventRegistry().activated().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                try {
                    print("event occured");
                    ObjectEventArgs a = (ObjectEventArgs) args;
                    if (a.object() instanceof Item) {
                        Item item = ((Item) a.object());
                        print("Activated item: " + item.id);
                        boolean isChildActive = db().isActive(item.child);
                        print("child is active? " + isChildActive);
                        if (!isChildActive) {
                            print("Activating child manually...");
                            db().activate(item.child, 1);
                        }
                    } else {
                        print("got object: " + a.object());
                    }
                } catch (Throwable e1) {
                    // todo: Classcast exceptions weren't breaking out?  silently hidden
                    e1.printStackTrace();
                }
            }
        });

        a = queryItem("A");
        Assert.isNotNull(a.child.child); // this time, they should all be activated.
    }

    private void print(String s) {
        if(debug) System.out.println(s);
    }

    private ObjectSet queryItems() {
        return createItemQuery().execute();
    }

    private Query createItemQuery() {
        Query q = db().query();
        q.constrain(Item.class);
        // todo: having this here will not fire the activation events!
        /*q.sortBy(new QueryComparator() {
			public int compare(Object first, Object second) {
				return ((Item)first).id.compareTo(((Item)second).id);
			}
		});*/
        return q;
    }

    private EventRegistry eventRegistry() {
        return EventRegistryFactory.forObjectContainer(db());
    }

    private void enableCascadeOnDelete() {
        Db4o.configure().objectClass(Item.class).cascadeOnDelete(true);
    }

    private Item queryItem(final String id) {
        Query q = createItemQuery();
        q.descend("id").constrain(id);
        return (Item) q.execute().next();
    }

    public static void main(String[] args) {
        new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oSolo(),
                        SelectiveActivationTestCase.class)).run();
    }
}
