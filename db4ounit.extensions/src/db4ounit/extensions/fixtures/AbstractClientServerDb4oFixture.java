/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;


public abstract class AbstractClientServerDb4oFixture extends AbstractDb4oFixture{
    
    protected static final String FILE = "Db4oClientServer.yap";
    
    protected static final String HOST = "localhost";

    protected static final int PORT = 0xdb40;
    
    protected static final String USERNAME = "db4o";

    protected static final String PASSWORD = USERNAME;

    private ObjectServer _server;

    private final File _yap;
    
    private final int _port;
    
    public AbstractClientServerDb4oFixture(ConfigurationSource configSource,String fileName, int port) {
    	super(configSource);
        _yap = new File(fileName);
        _port = port;
    }
    
    public AbstractClientServerDb4oFixture(ConfigurationSource configSource){
        this(configSource,FILE, PORT);
    }

    public void close() throws Exception {
        _server.close();
    }

    public void open() throws Exception {
        _server = Db4o.openServer(config(),_yap.getAbsolutePath(), _port);
        _server.grantAccess(USERNAME, PASSWORD);
    }

    public abstract ExtObjectContainer db();
    
    protected void doClean() {
        _yap.delete();
    }

}
