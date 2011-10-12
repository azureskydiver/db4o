package com.db4o.db4ounit.common.types;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.io.MemoryStorage;
import com.db4o.io.Storage;
import com.db4o.typehandlers.SingleClassTypeHandlerPredicate;

import db4ounit.*;

/**
 * @sharpen.remove
 */
public class StringBuilderHandlerTestCase  implements TestLifeCycle {
	
    private static final String DATABASE_FILE = "!In:MemoryDB!";
    private EmbeddedObjectContainer container;
    private Storage storage;

    @Override
    public void setUp() throws Exception {
        storage = new MemoryStorage();
        EmbeddedConfiguration configuration = newConfig();
        this.container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
    }

    @Override
    public void tearDown() throws Exception {
    	container.close();
    }

    public void testCanStore(){
        storeInstance();
    }
    public void testCanRead(){
        canRead();
    }
    public void testCanReadAfterDefragment() throws Exception {
        canReadAfterDefragment();
    }

    public void canStore(){
        storeInstance();
    }
    
    public void canRead(){
        storeInstance();
        assertCanReadData();
    }
    
    public void canReadAfterDefragment() throws Exception {
        storeInstance();
        container.close();

        DefragmentConfig cfg = new DefragmentConfig(DATABASE_FILE);
        cfg.db4oConfig(newConfig());

        Defragment.defrag(cfg);

        this.container = Db4oEmbedded.openFile(newConfig(), DATABASE_FILE);
        assertCanReadData();

    }

    private EmbeddedConfiguration newConfig() {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().storage(storage);
        configuration.common().registerTypeHandler(new SingleClassTypeHandlerPredicate(StringBuilder.class),
                new StringBuilderHandler());
        return configuration;
    }

    private void assertCanReadData() {
        final Holder holder = container.query(Holder.class).get(0);
        Assert.areEqual(holder.getBuilder().toString(), "TestData");
    }

    private void storeInstance() {
        container.store(new Holder());
    }

    public static class Holder{
        StringBuilder builder ;

        public Holder(String data) {
            this.builder = new StringBuilder(data);
        }

        public Holder() {
            this("TestData");
        }

        public StringBuilder getBuilder() {
            return builder;
        }

        public void setBuilder(StringBuilder builder) {
            this.builder = builder;
        }
    }

}
