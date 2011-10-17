package com.db4o.db4ounit.common.types;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.internal.*;
import com.db4o.io.*;
import com.db4o.typehandlers.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(unlessCompatible=decaf.Platform.JDK15)
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
        configuration.common().reflectWith(Platform4.reflectorForType(Holder.class));
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
