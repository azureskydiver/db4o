package com.db4odoc.semaphoreEvent;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.messaging.*;
/**
 * @sharpen.ignore
 */
public class SemaphoreMessageExample {

	private static final String DB4O_FILE_NAME = "reference.db4o";

	private static final int PORT = 4440;

	private static final String USER = "db4o";

	private static final String PASSWORD = "db4o";

	private static Map clientListeners = new HashMap();

	public static void main(String[] args) throws IOException {
		new SemaphoreMessageExample().run();
	}

	// end main

	public void run() throws IOException, DatabaseFileLockedException {
		new File(DB4O_FILE_NAME).delete();
		ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, PORT);
		try {
			server.grantAccess(USER, PASSWORD);
			Configuration configuration = server.ext().configure();
			final ObjectContainer oc = server.openClient();
			
			configuration.clientServer().setMessageRecipient(new MessageRecipient() {
				public void processMessage(MessageContext context,
						Object message) {
					oc.store(message);
					oc.commit();
					System.out.println("Server received: " + message);
					// we don't need this message any more - mark it deleted. 
					// The actual deletion will happen with the next commit
					oc.delete(message);
				}
			});

			ObjectContainer client1 = openClient();
		
			if (client1 != null) {
				try {
					// wait for the operations to finish
					waitForCompletion();

					Item item = new Item();
					client1.store(item);
					client1.commit();

					waitForCompletion();

					client1.ext().setSemaphore("test", 500);
					MessageSender sender = client1.ext().configure().clientServer().getMessageSender();
					sender.send(new SemaphoreMessage("test"));
					waitForCompletion();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					//closeClient(client1);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			waitForCompletion();
			waitForCompletion();
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
				// get all the added objects
				ObjectInfoCollection added = ((CommitEventArgs) args).added();
				Iterator4 infos = added.iterator();
				while (infos.moveNext()) {
					ObjectInfo info = (ObjectInfo) infos.current();
					Object obj = info.getObject();
					System.out.println(obj);
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