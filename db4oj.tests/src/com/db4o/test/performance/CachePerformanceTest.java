package com.db4o.test.performance;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.caching.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.data.*;

/**
 * @decaf.ignore
 */
public class CachePerformanceTest {

	private static final int BENCHMARKS = 3;

	private static final int ITERATIONS = 1000;

	private static final int COMMIT_EVERY = 120;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// warm up
		benchmarkCachedIoAdapter();
		benchmarkIoAdapterWithCache();
		
		double totalRatio = 0;
		for (int i=0; i<BENCHMARKS; ++i) {
			final long t0 = benchmarkCachedIoAdapter();
			final long t1 = benchmarkIoAdapterWithCache();
			
			final double ratio = t1/((double)t0);
			report(ratio);
			totalRatio += ratio;
		}
		System.out.print("On average ");
		report(totalRatio / BENCHMARKS);
	}

	private static long benchmarkCachedIoAdapter() {
	    return new CachePerformanceTest(cachedIoAdapter()).run();
    }

	private static long benchmarkIoAdapterWithCache() {
	    return new CachePerformanceTest(ioAdapterWithCache()).run();
    }

	private static void report(final double ratio) {
	    System.out.println("IoAdapterWithCache is " + (ratio > 1 ? "slower by " : "faster by ") + ((int)(((ratio > 1 ? ratio : (1 - ratio)) * 100) % 100)) + "%");
    }
	
	private static IoAdapter cachedIoAdapter() {
	    return new CachedIoAdapter(new RandomAccessFileAdapter());
    }

	private static IoAdapter ioAdapterWithCache() {
		return new IoAdapterWithCache(new RandomAccessFileAdapter()) {
			@Override
			protected Cache4 newCache(int pageCount) {
				return CacheFactory.newLRUCache(pageCount);
//				return CacheFactory.new2QCache(pageCount);
			}
    	};
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
	
	private ObjectContainer _container;
	private String _filename;
	private final IoAdapter _io;
	
	public CachePerformanceTest(IoAdapter ioAdapter) {
		_io = ioAdapter;
    }

	private long run() {
		openFile();
		
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
			System.out.println("" + adapterName() + ": " + ((int)(elapsed/1000000.0)) + "ms");
			return elapsed;
			
		} finally {
			dispose();
		}
    }

	private void commit() {
	    _container.commit();
    }

	private String adapterName() {
	    final String simpleName = _io.getClass().getSimpleName();
	    if (simpleName.length() == 0) {
	    	return _io.getClass().getSuperclass().getSimpleName();
	    }
		return simpleName;
    }

	private void dispose() {
	    _container.close();
	    File4.delete(_filename);
    }

	private void openFile() {
	    _filename = Path4.getTempFileName();
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Item.class).objectField("_id").indexed(true);
		config.file().io(_io);
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
