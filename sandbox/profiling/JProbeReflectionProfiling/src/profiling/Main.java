package profiling;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.io.*;

import db4ounit.*;

public class Main {

	private static final int OBJECT_COUNT = 1024*100;
	private static final String FILENAME = "profiling.db4o";

	interface ContainerBlock {
		void run(ObjectContainer container);
	}
	
	static class LongArrayByRef {
		public long[] value;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for (int i=0; i<3; ++i) {
			profile("default reflector", newConfiguration());
			profile("custom reflector", customReflectorConfig());
		}
	}

	private static Configuration newConfiguration() {
		Configuration config = Db4o.newConfiguration();
		config.io(new MemoryIoAdapter());
		return config;
	}

	private static Configuration customReflectorConfig() {
		Configuration config = newConfiguration();
		config.reflectWith(new CustomItemReflector());
		return config;
	}

	private static void profile(String label, Configuration config) {
		File4.delete(FILENAME);
		
		final LongArrayByRef array = new LongArrayByRef();
		withContainer("[" + label + "] storing", config, new ContainerBlock() {
			public void run(ObjectContainer container) {
				array.value = store(container);
			}
		});
		withContainer("[" + label + "] retrieving", config, new ContainerBlock() {
			public void run(ObjectContainer container) {
				retrieveAll(container, array.value);
			}
		});
	}

	private static void withContainer(String label, Configuration config, ContainerBlock block) {
		System.out.println("Entering '" + label + "' phase.");
		StopWatch watch = new StopWatch();
		final ObjectContainer container = Db4o.openFile(config, FILENAME);
		try {
			watch.start();
			block.run(container);
		} finally {
			watch.stop();
			container.close();
			System.out.println("Leaving  '" + label + "' phase.");
			System.out.println("Time taken: " + watch.elapsed() + "ms.");
			System.out.println();
		}
	}

	private static void retrieveAll(ObjectContainer container, long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			retrieve(container, ids[i]);
		}
	}

	private static void retrieve(ObjectContainer container, long id) {
		Item item = container.ext().getByID(id);
		container.activate(item, Integer.MAX_VALUE);
		Assert.areEqual(42, item._intValue);
	}

	private static long[] store(ObjectContainer container) {
		long[] ids = new long[OBJECT_COUNT];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = storeNewItem(container);
		}
		return ids;
	}

	private static long storeNewItem(ObjectContainer container) {
		Object item = newItem();
		container.store(item);
		return container.ext().getID(item);
	}

	private static Object newItem() {
		return new Item(42, "42");
	}

}
