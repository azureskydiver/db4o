/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.io.*;

import db4ounit.*;

/**
 * #COR-775
 * Currently this test doesn't work with JDKs that use a 
 * timer file lock because the new logic grabs into the Bin 
 * below the MockBin and reads open times there directly.
 * The times are then inconsistent with the written times.
 */
public class DefragEncryptedFileTestCase implements TestLifeCycle {

    private static final String ORIGINAL = Path4.getTempFileName();
    
    private static final String DEFGARED = ORIGINAL + ".bk";

    Configuration db4oConfig;
    
    public void setUp() throws Exception {
    	cleanup();
	}

	public void tearDown() throws Exception {
		cleanup();
	}
	
	private void cleanup() {
		File4.delete(ORIGINAL);
		File4.delete(DEFGARED);
	}

    public static void main(String[] args) {
        new ConsoleTestRunner(DefragEncryptedFileTestCase.class).run();
    }

    public void testCOR775() throws Exception {
        prepare();
        verifyDB();
        
        DefragmentConfig config = new DefragmentConfig(ORIGINAL, DEFGARED);
        config.forceBackupDelete(true);
        //config.storedClassFilter(new AvailableClassFilter());
        config.db4oConfig(getConfiguration());
        Defragment.defrag(config);

        verifyDB();
    }

    private void prepare() {
        File file = new File(ORIGINAL);
        if (file.exists()) {
            file.delete();
        }
        
        ObjectContainer testDB = openDB();
        Item item = new Item("richard", 100);
        testDB.store(item);
        testDB.close();
    }
    
    private void verifyDB() {
        ObjectContainer testDB = openDB();
        ObjectSet result = testDB.queryByExample(Item.class);
        if (result.hasNext()) {
            Item retrievedItem = (Item) result.next();
            Assert.areEqual("richard", retrievedItem.name);
            Assert.areEqual(100, retrievedItem.value);
        } else {
            Assert.fail("Cannot retrieve the expected object.");
        }
        testDB.close();
    }

    private ObjectContainer openDB() {
        Configuration db4oConfig = getConfiguration();
        ObjectContainer testDB = Db4o.openFile(db4oConfig, ORIGINAL);
        return testDB;
    }

    private Configuration getConfiguration() {
        if (db4oConfig == null) {
            db4oConfig = Db4o.newConfiguration();

            db4oConfig.activationDepth(Integer.MAX_VALUE);
            db4oConfig.callConstructors(true);
            Storage storage = new MockStorage(
                    new FileStorage(), "db4o");
            db4oConfig.storage(storage);
        }
        return db4oConfig;
    }

    public static class Item {
        public String name;

        public int value;

        public Item(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class MockStorage extends StorageDecorator {

        private String password;

        public MockStorage(Storage storage, String password) {
        	super(storage);
            this.password = password;
        }

        @Override
        protected Bin decorate(Bin bin) {
        	return new MockBin(bin, password);
        }


        static class MockBin extends BinDecorator {
        
        	private String _password;
        	
	        public MockBin(Bin bin, String password) {
				super(bin);
				_password = password;
			}

			public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
	            _bin.read(pos, bytes, length);
	            for (int i = 0; i < length; i++) {
	                bytes[i] = (byte) (bytes[i] - _password.hashCode());
	            }
	            return length;
	        }
			
			public int syncRead(long pos, byte[] bytes, int length) throws Db4oIOException {
				return read(pos, bytes, length);
	        }
	
	        public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
	            for (int i = 0; i < length; i++) {
	                buffer[i] = (byte) (buffer[i] + _password.hashCode());
	            }
	            _bin.write(pos, buffer, length);
	        }
        }
    }

}
