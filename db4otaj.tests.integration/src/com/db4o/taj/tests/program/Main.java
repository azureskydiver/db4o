package com.db4o.taj.tests.program;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.ta.*;
import com.db4o.taj.tests.model.*;

import db4ounit.*;

public class Main {
	
	private static final class TAJActivationListener implements EventListener4 {
		
		private Map _activations = new HashMap();
		
		public int activationCount(Class clazz) {
			Integer activationCount = (Integer) _activations.get(clazz);
			if(activationCount == null) {
				return 0;
			}
			return activationCount.intValue();
		}
		
		public void onEvent(Event4 event, EventArgs args) {
			Object obj = ((ObjectEventArgs) args).object();
			Class curClazz = obj.getClass();
			while(curClazz != Object.class) {
				_activations.put(curClazz, new Integer(activationCount(curClazz) + 1));
				curClazz = curClazz.getSuperclass();
			}
		}
	}

	private static final String DB_PATH = "pilots.db4o";

	public static void main(String[] args) throws Throwable {
		final Team ferrari = new Team("Ferrari");
		Pilot raikkonen = new Pilot("Raikkonen", 100);
		ferrari.addPilot(raikkonen);
		ferrari.addMechanic(new Person("John Doe"));
		ferrari.addSponsor("Versant", 1000);
		
		deleteDatabase();

		try {
			withDatabase(new Procedure4() {
				public void apply(Object obj) {
					((ObjectContainer)obj).store(ferrari);
				}
			});
	
			withDatabase(new Procedure4() {
				public void apply(Object obj) {
					try {
						ObjectContainer db = (ObjectContainer) obj;
						TAJActivationListener listener = new TAJActivationListener();
						EventRegistryFactory.forObjectContainer(db).activated().addListener(listener);
						ObjectSet result = db.query(Team.class);
						Team ferrari = (Team) result.next();
						Assert.isNull(Reflection4.getFieldValue(ferrari, "_pilots"));
						Assert.isNull(Reflection4.getFieldValue(ferrari, "_mechanics"));
						Assert.isNull(Reflection4.getFieldValue(ferrari, "_sponsors"));
						assertNoActivation(listener, new Class[] { Person.class, ArrayList.class, HashMap.class, LinkedList.class });
						List pilots = ferrari.pilots();
						assertNoActivation(listener, new Class[] { Person.class, ArrayList.class, HashMap.class, LinkedList.class });
						Pilot raikkonen = (Pilot) pilots.get(0);
						assertActivationCount(listener, ArrayList.class, 1);
						assertNoActivation(listener, new Class[] { Person.class, HashMap.class, LinkedList.class });
						Assert.areEqual("Raikkonen", raikkonen.name());
						assertActivationCount(listener, ArrayList.class, 1);
						assertActivationCount(listener, Pilot.class, 1);
						assertNoActivation(listener, new Class[] { HashMap.class, LinkedList.class });
						int amountSponsored = ferrari.amountSponsored("Versant");
						assertActivationCount(listener, ArrayList.class, 1);
						assertActivationCount(listener, Pilot.class, 1);
						assertActivationCount(listener, HashMap.class, 1);
						assertNoActivation(listener, new Class[] { LinkedList.class });
						Assert.areEqual(1000, amountSponsored);
						System.out.println(raikkonen);
						List mechanics = ferrari.mechanics();
						assertActivationCount(listener, Person.class, 1);
						assertActivationCount(listener, LinkedList.class, 0);
						Person mechanic = (Person) mechanics.get(0);
						assertActivationCount(listener, LinkedList.class, 1);
						assertActivationCount(listener, Person.class, 1);
						mechanic.name();
						assertActivationCount(listener, Person.class, 2);
						System.out.println(mechanic);
					}
					catch(Exception exc) {
						Assert.fail("", exc);
					}
				}
			});
		}
		finally {
			deleteDatabase();
		}

	}

	private static void deleteDatabase() {
		new File(DB_PATH).delete();
	}

	private static void assertActivationCount(TAJActivationListener listener, Class clazz, int expectedCount) {
		Assert.areEqual(expectedCount, listener.activationCount(clazz), clazz.getName());
	}

	private static void assertNoActivation(TAJActivationListener listener, Class[] clazzes) {
		for (int clazzIdx = 0; clazzIdx < clazzes.length; clazzIdx++) {
			Assert.areEqual(0, listener.activationCount(clazzes[clazzIdx]), clazzes[clazzIdx].getName());
		}
	}

	private static EmbeddedConfiguration config() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().activationDepth(0);
		config.common().add(new TransparentPersistenceSupport());
		return config;
	}

	private static void withDatabase(Procedure4 block) {
		ObjectContainer db = Db4oEmbedded.openFile(config(), DB_PATH);
		try {
			block.apply(db);
		}
		finally {
			db.close();
		}
	}
}
