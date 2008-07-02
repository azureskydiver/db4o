package ibs.spikes;

import ie.wombat.jbdiff.*;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.query.*;

public class JbDiffSpike {
	
	private static final String BACKUP_FILE = "backup.db4o";
	private static final String DIFF_FILE = "diff.bin";
	private static final String BASELINE_FILE = "basefile.db4o";
	private static final String ORIGINAL_FILE = "original.db4o";

	public static class Item {
		
		public String name;
		
		public Item(String name_) {
			name = name_;
		}
	}
	
	interface ObjectContainerAction {
		void apply(ObjectContainer container);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		originalFile().delete();
		backupFile().delete();
		
		generateBaseFile();
		
		withContainerReplication("insert 1000 objects", new ObjectContainerAction() {
			public void apply(ObjectContainer container) {
				for (int i=0; i<1000; ++i) {
					container.store(new Item("Item " + i));
				}
			}
		});
		
		withContainerReplication("change single object", new ObjectContainerAction() {
			public void apply(ObjectContainer container) {
				final Item item = itemByName(container, "Item 42");
				item.name = "wahoo";
				container.store(item);
			}
		});
		
		withContainerReplication("change 999 objects", new ObjectContainerAction() {
			public void apply(ObjectContainer container) {
				for (int i=0; i<1000; ++i) {

					final Item found = itemByName(container, "Item " + i);
					if (null == found) {
						continue;
					}
					found.name = found.name + "*";
					container.store(found);
				}
			}
		});
		
		withContainer(BACKUP_FILE, new ObjectContainerAction() {
			public void apply(ObjectContainer container) {
				for (Item item : container.query(Item.class)) {
					System.out.println(item.name);
				}
			}
		});
	}

	private static void withContainerReplication(
			String label, final ObjectContainerAction action) throws IOException {
		withContainer(ORIGINAL_FILE, action);
		
		generateDiffFile();
		
		System.out.println(label + ": " + diffFile().length());
		System.out.println("File size: " + originalFile().length());
		
		applyDiff();
	}

	private static void applyDiff() throws IOException {
		JBPatch.bspatch(baselineFile(), backupFile(), diffFile());
	}

	private static File backupFile() {
		return new File(BACKUP_FILE);
	}

	private static void generateDiffFile() throws IOException {
		JBDiff.bsdiff(baselineFile(), originalFile(), diffFile());
		File4.copy(ORIGINAL_FILE, BASELINE_FILE);
	}

	private static File originalFile() {
		return new File(ORIGINAL_FILE);
	}

	private static File baselineFile() {
		return new File(BASELINE_FILE);
	}

	private static File diffFile() {
		return new File(DIFF_FILE);
	}

	private static void generateBaseFile() throws IOException {
		openContainer(ORIGINAL_FILE).close();
		
		File4.copy(ORIGINAL_FILE, BASELINE_FILE);
	}

	private static void withContainer(final String fname, final ObjectContainerAction action) {
		final ObjectContainer container = openContainer(fname);
		try {
			action.apply(container);
		} finally {
			container.close();
		}
	}

	private static ObjectContainer openContainer(String fname) {
		final Configuration config = Db4o.newConfiguration();
//		config.lockDatabaseFile(false);
		config.objectClass(Item.class).objectField("name").indexed(true);
		
		return Db4o.openFile(config, fname);
	}

	private static Item itemByName(ObjectContainer container, final String name) {
		final Query query = container.query();
		query.constrain(Item.class);
		query.descend("name").constrain(name);
		final ObjectSet found = query.execute();
		if (!found.hasNext()) {
			return null;
		}
		return (Item)found.next();
	}

}
