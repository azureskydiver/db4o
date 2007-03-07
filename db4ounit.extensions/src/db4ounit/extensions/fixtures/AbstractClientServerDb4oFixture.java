/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.internal.LocalObjectContainer;

import db4ounit.extensions.Db4oTestCase;


public abstract class AbstractClientServerDb4oFixture extends AbstractDb4oFixture{
    
    protected static final String FILE = "Db4oClientServer.yap";
    
    protected static final String HOST = "localhost";

    protected static final int PORT = 0xdb40;
    
    protected static final String USERNAME = "db4o";

    protected static final String PASSWORD = USERNAME;

    private ObjectServer _server;

    private final File _yap;
    
    protected final int _port;
    
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
    
    public ObjectServer server() {
    	return _server;
    }
    
    /**
	 * Does not accept a clazz which is assignable from OptOutCS, or not
	 * assignable from Db4oTestCase.
	 * 
	 * @return returns false if the clazz is assignable from OptOutCS, or not
	 *         assignable from Db4oTestCase. Otherwise, returns true.
	 */
	public boolean accept(Class clazz) {
		if ((OptOutCS.class.isAssignableFrom(clazz))
				|| !Db4oTestCase.class.isAssignableFrom(clazz)) {
			return false;
		}
		return true;
	}
    
	public LocalObjectContainer fileSession() {
		return (LocalObjectContainer)_server.ext().objectContainer();
	}
	
	public void defragment() throws Exception {
		defragment(FILE);
	}
	
	protected ObjectContainer openEmbeddedClient() {
		return _server.openClient(config());
	}

}
