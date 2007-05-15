﻿/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.query.Query;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.Db4oSolo;

public class SelectiveActivationTestCase extends AbstractDb4oTestCase {
    private boolean debug = false;

    protected void configure(Configuration config) {
        enableCascadeOnDelete(config);
        config.activationDepth(1);
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

    private void enableCascadeOnDelete(Configuration config) {
        config.objectClass(Item.class).cascadeOnDelete(true);
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
