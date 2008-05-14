package profiling;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class Main {

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
		File4.delete(FILENAME);
		
		final LongArrayByRef array = new LongArrayByRef();
		withContainer("storing", new ContainerBlock() {
			public void run(ObjectContainer container) {
				array.value = store(container);
			}
		});
		withContainer("retrieving", new ContainerBlock() {
			public void run(ObjectContainer container) {
				retrieveAll(container, array.value);
			}
		});
	}

	private static void withContainer(String label, ContainerBlock block) {
		System.out.println("Entering '" + label + "' phase.");
		StopWatch watch = new StopWatch();
		final ObjectContainer container = Db4o.openFile(FILENAME);
		try {
			watch.start();
			block.run(container);
		} finally {
			watch.stop();
			container.close();
			System.out.println("Leaving '" + label + "' phase.");
			System.out.println("Time taken: " + watch.elapsed() + "ms.");
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
		long[] ids = new long[1024];
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
