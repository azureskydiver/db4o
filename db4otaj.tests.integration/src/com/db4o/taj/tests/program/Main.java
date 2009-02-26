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
		private int _pilotActivationCount = 0;
		private int _listActivationCount = 0;
		
		public void onEvent(Event4 event, EventArgs args) {
			Object obj = ((ObjectEventArgs) args).object();
			if(obj instanceof ArrayList) {
				_listActivationCount++;
			}
			if(obj instanceof Pilot) {
				_pilotActivationCount++;
			}
		}
	}

	private static final String DB_PATH = "pilots.db4o";

	public static void main(String[] args) throws Throwable {
		final Team ferrari = new Team("Ferrari");
		Pilot raikkonen = new Pilot("Raikkonen", 100);
		ferrari.addPilot(raikkonen);

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
						assertActivationCount(listener, 0, 0);
						List pilots = ferrari.pilots();
						assertActivationCount(listener, 0, 0);
						Pilot raikkonen = (Pilot) pilots.get(0);
						assertActivationCount(listener, 1, 0);
						Assert.areEqual("Raikkonen", raikkonen.name());
						assertActivationCount(listener, 1, 1);
						System.out.println(raikkonen);
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

	private static void assertActivationCount(TAJActivationListener listener, int expectedListCount, int expectedPilotCount) {
		Assert.areEqual(expectedListCount, listener._listActivationCount);
		Assert.areEqual(expectedPilotCount, listener._pilotActivationCount);
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
