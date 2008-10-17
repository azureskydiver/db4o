/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class ObjectServerTestCase implements TestLifeCycle {
    
    private ExtObjectServer server;
    
    private String fileName;

    public void setUp() throws Exception {
        fileName = Path4.getTempFileName();
        server = Db4o.openServer(Db4o.newConfiguration(), fileName, -1).ext();
        server.grantAccess(credentials(), credentials());
    }

    public void tearDown() throws Exception {
        server.close();
        new File(fileName).delete();
    }
    
    public void testClientCount(){
        assertClientCount(0);
        ObjectContainer client1 = Db4o.openClient(Db4o.newConfiguration(), "localhost", port(), credentials(), credentials());
        assertClientCount(1);
        ObjectContainer client2 = Db4o.openClient(Db4o.newConfiguration(), "localhost", port(), credentials(), credentials());
        assertClientCount(2);
        client1.close();
        client2.close();
        // closing is asynchronous, relying on completion is hard
        // That's why there is no test here. 
        // ClientProcessesTestCase tests closing.
    }

    private void assertClientCount(int count) {
        Assert.areEqual(count, server.clientCount());
    }
    
    private int port(){
        return server.port();
    }
    
    private String credentials(){
        return "DB4O";
    }

}
