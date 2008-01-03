package com.db4odoc.commitcallbacks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.events.CommitEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.ObjectInfoCollection;
import com.db4o.foundation.Iterator4;

public class PushedUpdatesExample {

	private static final String DB4O_FILE_NAME = "reference.db4o";

	private static final int PORT = 4440;

	private static final String USER = "db4o";

	private static final String PASSWORD = "db4o";

	private static Map clientListeners = new HashMap();

	public static void main(String[] args) throws IOException {
		new PushedUpdatesExample().run();
	}

	// end main

	public void run() throws IOException, DatabaseFileLockedException {
		new File(DB4O_FILE_NAME).delete();
		ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, PORT);
		try {
			server.grantAccess(USER, PASSWORD);

			ObjectContainer client1 = openClient();
			ObjectContainer client2 = openClient();

			if (client1 != null && client2 != null) {
				try {
					// wait for the operations to finish
					waitForCompletion();

					// save pilot with client1
					Car client1Car = new Car("Ferrari", 2006, new Pilot(
							"Schumacher"));
					client1.store(client1Car);
					client1.commit();

					waitForCompletion();

					// retrieve the same pilot with client2
					Car client2Car = (Car) client2.query(Car.class).next();
					System.out.println(client2Car);

					// modify the pilot with client1
					client1Car.setModel(2007);
					client1Car.setPilot(new Pilot("Hakkinnen"));
					client1.store(client1Car);
					client1.commit();

					waitForCompletion();

					// client2Car has been automatically updated in
					// the committed event handler because of the
					// modification and the commit by client1
					System.out.println(client2Car);

					waitForCompletion();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					closeClient(client1);
					closeClient(client2);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			server.close();
		}
	}

	// end run

	private void closeClient(ObjectContainer client) {
		// remove listeners before shutting down
		if (clientListeners.get(client) != null) {
			EventRegistry eventRegistry = EventRegistryFactory
					.forObjectContainer(client);
			eventRegistry.committed().removeListener(
					(EventListener4) clientListeners.get(client));
			clientListeners.remove(client);
		}
		client.close();
	}

	// end closeClient

	private ObjectContainer openClient() {
		try {
			ObjectContainer client = Db4o.openClient("localhost", PORT, USER,
					PASSWORD);
			EventListener4 committedEventListener = createCommittedEventListener(client);
			EventRegistry eventRegistry = EventRegistryFactory
					.forObjectContainer(client);
			eventRegistry.committed().addListener(committedEventListener);
			// save the client-listener pair in a map, so that we can
			// remove the listener later
			clientListeners.put(client, committedEventListener);
			return client;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// end openClient

	private EventListener4 createCommittedEventListener(
			final ObjectContainer objectContainer) {
		return new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				// get all the updated objects
				ObjectInfoCollection updated = ((CommitEventArgs) args)
						.updated();
				Iterator4 infos = updated.iterator();
				while (infos.moveNext()) {
					ObjectInfo info = (ObjectInfo) infos.current();
					Object obj = info.getObject();
					// refresh object on the client
					objectContainer.ext().refresh(obj, 2);
				}
			}
		};
	}

	// end createCommittedEventListener

	private void waitForCompletion() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// end waitForCompletion

}