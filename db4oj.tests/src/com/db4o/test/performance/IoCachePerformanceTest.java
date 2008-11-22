package com.db4o.test.performance;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.data.*;

/**
 * @decaf.ignore
 */
public class IoCachePerformanceTest {
	
	private static final int BENCHMARKS = 5;

	private static final int PRE_EXISTING_ITEMS = 10000;
	
	private static final int ITERATIONS = 20;

	private static final int COMMIT_EVERY = 5;
	
	private static final int ITEMS_PER_ITERATION = 10;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		IoCachePerformanceTest test0 = benchmarkCachedIoAdapter();
		IoCachePerformanceTest test1 = benchmarkIoAdapterWithCache();

		// warm up
		test0.run();
		test1.run();
		
		double totalRatio = 0;
		for (int i=0; i<BENCHMARKS; ++i) {
			
			
			final long t0 = test0.run();
			final long t1 = test1.run();
			
			final double ratio = t1/((double)t0);
			report(test1, ratio);
			totalRatio += ratio;
		}
		System.out.print("On average ");
		report(test1, totalRatio / BENCHMARKS);
	}

	private static IoCachePerformanceTest benchmarkCachedIoAdapter() {
	    return new IoCachePerformanceTest("CachedIoAdapter", cachedIoAdapter());
    }

	private static IoCachePerformanceTest benchmarkIoAdapterWithCache() {
	    return new IoCachePerformanceTest("CachingStorage", cachingStorage());
    }

	private static Storage cachingStorage() {
		return new CachingStorageFactory(new FileStorage());
    }

	private static void report(IoCachePerformanceTest test1, final double ratio) {
	    System.out.println(test1._name + " is " + (ratio > 1 ? "slower by " : "faster by ") + ((int)(((ratio > 1 ? ratio : (1 - ratio)) * 100) % 100)) + "%");
    }
	
	private static IoAdapter cachedIoAdapter() {
	    return new CachedIoAdapter(new RandomAccessFileAdapter());
    }

	public static class Item {

		private int _id;
		
		private boolean[] _payLoad;

		public Item(int id) {
			_id = id;
			_payLoad = new boolean[100];
        }
		
		public int id() {
			return _id;
		}
	}
	
	private final String _name;
	private ObjectContainer _container;
	private String _filename;
	private final Storage _io;
	
	public IoCachePerformanceTest(String name, Storage storageFactory) {
		_name = name;
		_io = storageFactory;
    }

	public IoCachePerformanceTest(String name, IoAdapter adapter) {
		this(name, new IoAdapterStorage(adapter));
	}

	private long run() {
		openFile();
		
		if(PRE_EXISTING_ITEMS > 0){
			for (int i = 0; i < PRE_EXISTING_ITEMS; i++) {
				_container.store(new Item(i));
			}
			commit();
		}
		
		try {
			final long t0 = System.nanoTime();
			for (int i=0; i<ITERATIONS; ++i) {
				writeAFewItems();
				if (i % COMMIT_EVERY == 0) {
					commit();
				}
				queryAnotherFew();
			}
			
			final long t1 = System.nanoTime();
			final long elapsed = t1-t0;
			System.out.println(_name + ": " + ((int)(elapsed/1000000.0)) + "ms");
			return elapsed;
			
		} finally {
			dispose();
		}
    }

	private void commit() {
	    _container.commit();
    }

	private void dispose() {
	    _container.close();
	    File4.delete(_filename);
    }

	private void openFile() {
	    _filename = Path4.getTempFileName();
		_container = Db4oEmbedded.openFile(configuration(), _filename);
    }

	private EmbeddedConfiguration configuration() {
	    final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Item.class).objectField("_id").indexed(true);
		config.file().storageFactory(_io);
	    return config;
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
	    return Generators.take(ITEMS_PER_ITERATION, Streams.randomIntegers()).iterator();
    }
}
