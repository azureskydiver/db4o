package com.db4odoc.tpexample;

import java.io.File;
import java.util.Iterator;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ta.TransparentPersistenceSupport;

public class TPCollectionExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";
	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		testCollectionPersistence();
	}
	
	private static void storeCollection() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configureTP());
		if (container != null) {
			try {
				Team team = new Team();
				for (int i = 0; i < 10; i++) {
					team.addPilot(new Pilot("Pilot #" + i));
				}
				container.store(team);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeDatabase();
			}
		}
	}

	// end storeCollection

	private static Configuration configureTP() {
		Configuration configuration = Db4o.newConfiguration();
		// add TP support
		configuration.add(new TransparentPersistenceSupport());
		return configuration;	
	}
	// end configureTP

	private static void testCollectionPersistence() {
		storeCollection();
		ObjectContainer container = database(configureTP());
		if (container != null) {
			try {
				Team team = (Team) container.queryByExample(new Team()).next();
				// this method will activate all the members in the collection
				Iterator<Pilot> it = team.getPilots().iterator();
				while (it.hasNext()){
					Pilot p = it.next();
					p.setName("Modified: " + p.getName());
				}
				team.addPilot(new Pilot("New pilot"));
				// explicitly commit to persist changes
				container.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				// If TP changes were not committed explicitly,
				// they would be persisted with the #close call
				closeDatabase();
			}
		}
		// reopen the database and check the changes
		container = database(configureTP());
		if (container != null) {
			try {
				Team team = (Team) container.queryByExample(new Team()).next();
				team.listAllPilots();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeDatabase();
			}
		}
	}

	// end testCollectionPersistence

	private static ObjectContainer database(Configuration configuration) {
		if (_container == null) {
			try {
				_container = Db4o.openFile(configuration, DB4O_FILE_NAME);
			} catch (DatabaseFileLockedException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return _container;
	}

	// end database

	private static void closeDatabase() {
		if (_container != null) {
			_container.close();
			_container = null;
		}
	}

	// end closeDatabase



}
