/* Copyright (C) 2006 - 2007 db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;


public class Db4oClientServer extends
		AbstractDb4oFixture implements Db4oClientServerFixture {
    
    private static final int	DEFAULT_PORT	= 0xdb40;

	protected static final String FILE = "Db4oClientServer.yap";
    
    protected static final String HOST = "localhost";

    protected static final String USERNAME = "db4o";

    protected static final String PASSWORD = USERNAME;

    private ObjectServer _server;

    private final File _yap;

	private boolean _embeddedClient;

	private ExtObjectContainer _objectContainer;
    
    protected static final int _port = findFreePort();
    
    
    public Db4oClientServer(ConfigurationSource configSource,String fileName, boolean embeddedClient) {
    	super(configSource);
        _yap = new File(fileName);
        _embeddedClient = embeddedClient;
    }
    
    public Db4oClientServer(ConfigurationSource configSource, boolean embeddedClient){
        this(configSource,FILE, embeddedClient);
    }
    
    public static int findFreePort() {
    	
		try {
			return findFreePort(DEFAULT_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			return findFreePort(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		throw new IllegalStateException("Could not allocate a usable port");
    }
    
    private static int findFreePort(int port) throws IOException{
		ServerSocket4 server = new ServerSocket4(new PlainSocketFactory(), port);
		port = server.getLocalPort();
		server.close();
    	Cool.sleepIgnoringInterruption(3);
    	return port; 
    }

    public void open() throws Exception {
		openServer();
		_objectContainer = _embeddedClient ? openEmbeddedClient().ext() : Db4o
				.openClient(config(), HOST, _port, USERNAME, PASSWORD).ext();
	}

	public ExtObjectContainer openNewClient() {
		return _embeddedClient ? openEmbeddedClient().ext() : Db4o.openClient(
				cloneDb4oConfiguration((Config4Impl) config()), HOST, _port,
				USERNAME, PASSWORD).ext();
	}

    private void openServer() throws Exception {
        _server = Db4o.openServer(config(),_yap.getAbsolutePath(), _port);
        _server.grantAccess(USERNAME, PASSWORD);
    }
    
    public void close() throws Exception {
		if (null != _objectContainer) {
			_objectContainer.close();
			_objectContainer = null;
		}
		closeServer();
	}

    private void closeServer() throws Exception {
    	if (null != _server) {
	        _server.close();
	        _server = null;
    	}
    }

	

	public ExtObjectContainer db() {
		return _objectContainer;
	}
    
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
				|| !AbstractDb4oTestCase.class.isAssignableFrom(clazz)) {
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
	
	private ObjectContainer openEmbeddedClient() {
		return _server.openClient(config());
	}
	
	private Config4Impl cloneDb4oConfiguration(Config4Impl config) {
		return (Config4Impl) config.deepClone(this);
	}

	public String getLabel() {
		return "C/S" + (_embeddedClient ? " Embedded" : "");
	}

	public int serverPort() {
		return _port;
	}
}
