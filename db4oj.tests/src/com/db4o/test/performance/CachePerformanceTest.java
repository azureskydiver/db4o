package com.db4o.test.performance;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.data.*;

/**
 * @decaf.ignore
 */
public class CachePerformanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CachePerformanceTest().run();
	}
	
	public static class Item {

		private int _id;

		public Item(int id) {
			_id = id;
        }
		
		public int id() {
			return _id;
		}
	}
	
	private ObjectContainer _container;
	private String _filename;

	private void run() {
		openFile();
		
		try {
			final StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			
			for (int i=0; i<10000; ++i) {
				writeAFewItems();
				queryAnotherFew();
			}
			
			stopWatch.stop();
			System.out.println("Elapsed: " + stopWatch.elapsed() + "ms");
			
		} finally {
			dispose();
		}
    }

	private void dispose() {
	    _container.close();
	    File4.delete(_filename);
    }

	private void openFile() {
	    _filename = Path4.getTempFileName();
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Item.class).objectField("_id").indexed(true);
		_container = Db4oEmbedded.openFile(config, _filename);
    }

	private void queryAnotherFew() {
		final Iterator4 ids = arbitraryIntegers();
		while (ids.moveNext()) {
			final Integer current = (Integer)ids.current();
			final Query query = newItemQuery(current);
			final ObjectSet<Object> result = query.execute();
			while (result.hasNext()) {
				final Item found = (Item)result.next();
				Assert.areEqual(current.intValue(), found.id());
			}
		}
    }

	private Query newItemQuery(final Integer current) {
	    final Query query = _container.query();
	    query.constrain(Item.class);
	    query.descend("_id").constrain(current);
	    return query;
    }

	private void writeAFewItems() {
		final Iterator4 ids = arbitraryIntegers();
		while (ids.moveNext()) {
			final Integer current = (Integer)ids.current();
			_container.store(new Item(current.intValue()));
		}
    }

	private Iterator4 arbitraryIntegers() {
	    return Generators.arbitraryValuesOf(Integer.class).iterator();
    }
}
