package com.db4o.db4ounit.common.cs;


import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import db4ounit.Assert;
import db4ounit.ConsoleTestRunner;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class UniqueConstraintOnServerTestCase implements TestLifeCycle, TestCase {

    private static final String DATABASE_FILE = "database.db4o";
    private static final String DATABASE_ADMIN = "dba";
    private int port;
    private ObjectServer objectServer;

    public static void main(String[] args) {
        new ConsoleTestRunner(UniqueConstraintOnServerTestCase.class).run();
    }

    @Override
    public void setUp() throws Exception {
        cleanUp();
        this.port = findFreePort();
        this.objectServer = openServer(port);
    }

    @Override
    public void tearDown() throws Exception {
        objectServer.close();
    }
    
    public void testWorksForUniqueItems() {
        final ObjectContainer container = Db4oClientServer.openClient("localhost",
                port, DATABASE_ADMIN, DATABASE_ADMIN);
        container.store(new UniqueId(1));
        container.store(new UniqueId(2));
        container.store(new UniqueId(3));
        container.commit();

    }
    public void testNotUniqueItems() {
        final ObjectContainer container = Db4oClientServer.openClient("localhost",
                port, DATABASE_ADMIN, DATABASE_ADMIN);
        container.store(new UniqueId(1));
        container.store(new UniqueId(1));
        boolean exceptionWasThrown = false;
        try {
            container.commit();
        } catch (UniqueFieldValueConstraintViolationException e) {
            exceptionWasThrown = true;
        }
        Assert.isTrue(exceptionWasThrown);
    }

    private ObjectServer openServer(int port) {
        ServerConfiguration config = Db4oClientServer.newServerConfiguration();
        config.common().objectClass(UniqueId.class).objectField("id").indexed(true);
        config.common().add(new UniqueFieldValueConstraint(UniqueId.class, "id"));

        final ObjectServer server = Db4oClientServer.openServer(config, DATABASE_FILE, port);
        server.grantAccess(DATABASE_ADMIN, DATABASE_ADMIN);
        return server;
    }

    private void cleanUp() {
        new File(DATABASE_FILE).delete();
    }


    private int findFreePort() {
        try {
            final ServerSocket serverSocket = new ServerSocket(0);
            int port= serverSocket.getLocalPort();
            serverSocket.close();
            return port;
        } catch (IOException e) {
            throw new RuntimeException("Coundn't find free port", e);
        }
    }

    public static class UniqueId {
    	
        public int id;

        public UniqueId(int id) {
            this.id = id;
        }
    }
}
