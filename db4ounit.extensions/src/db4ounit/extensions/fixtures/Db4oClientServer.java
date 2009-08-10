/* Copyright (C) 2006 - 2007 Versant Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.threading.*;

import db4ounit.extensions.*;
import db4ounit.extensions.util.*;


public class Db4oClientServer extends
		AbstractDb4oFixture implements Db4oClientServerFixture {
    
	private static final int THREADPOOL_TIMEOUT = 3000;

	protected static final String FILE = "Db4oClientServer.db4o";
    
    public static final String HOST = "127.0.0.1";

    public static final String USERNAME = "db4o";

    public static final String PASSWORD = USERNAME;

    private ObjectServer _server;

    private final File _file;

	private boolean _embeddedClient;

	private ExtObjectContainer _objectContainer;
	
	private String _label;
    
    private int _port;

	private Configuration _serverConfig;
	
	private final ClientServerFactory _csFactory;
	
	public Db4oClientServer(ClientServerFactory csFactory, boolean embeddedClient, String label) {
		_csFactory = csFactory != null ? csFactory : defaultClientServerFactory();
		_file = new File(filePath());
        _embeddedClient = embeddedClient;
        _label = label;
	}

	private ClientServerFactory defaultClientServerFactory() {
	    return ((Config4Impl)config()).clientServerFactory();
    }     
    
    public Db4oClientServer(boolean embeddedClient, String label){
    	this(null, embeddedClient, label);
    }
    
    public void open(Db4oTestCase testInstance) throws Exception {
		openServerFor(testInstance);
		openClientFor(testInstance);
		
		listenToUncaughtExceptions();
	}

	private void listenToUncaughtExceptions() {
		listenToUncaughtExceptions(serverThreadPool());
		
		final ThreadPool4 clientThreadPool = clientThreadPool();
		if (null != clientThreadPool) {
			listenToUncaughtExceptions(clientThreadPool);
		}
		
    }

	private ThreadPool4 clientThreadPool() {
		return threadPoolFor(_objectContainer);
	}

	private ThreadPool4 serverThreadPool() {
		return threadPoolFor(_server.ext().objectContainer());
	}

	private void openClientFor(Db4oTestCase testInstance) throws Exception {
	    final Configuration config = clientConfigFor(testInstance);
		_objectContainer = openClientWith(config);
    }

	private Configuration clientConfigFor(Db4oTestCase testInstance) throws Exception {

        if (testInstance instanceof CustomClientServerConfiguration) {
        	final Configuration customServerConfig = newConfiguration();
			((CustomClientServerConfiguration)testInstance).configureClient(customServerConfig);
			return customServerConfig;
        }
        
	    final Configuration config = cloneConfiguration();
		applyFixtureConfiguration(testInstance, config);
	    return config;
    }

	private ExtObjectContainer openSocketClient(final Configuration config) {
	    return _csFactory.openClient(config, HOST, _port, USERNAME, PASSWORD, new PlainSocketFactory()).ext();
    }

	public ExtObjectContainer openNewClient() {
		return openClientWith(cloneConfiguration());
	}

	private ExtObjectContainer openClientWith(final Configuration config) {
	    return _embeddedClient ? openEmbeddedClient().ext() : openSocketClient(config);
    }

	private void openServerFor(Db4oTestCase testInstance) throws Exception {
        _serverConfig = serverConfigFor(testInstance);
		_server = _csFactory.openServer(_serverConfig,_file.getAbsolutePath(), -1, new PlainSocketFactory());
        _port = _server.ext().port();
        _server.grantAccess(USERNAME, PASSWORD);
    }

	private Configuration serverConfigFor(Db4oTestCase testInstance) throws Exception {
		
        if (testInstance instanceof CustomClientServerConfiguration) {
        	final Configuration customServerConfig = newConfiguration();
			((CustomClientServerConfiguration)testInstance).configureServer(customServerConfig);
			return customServerConfig;
        }
        
        return cloneConfiguration();
    }
    
    public void close() throws Exception {
		if (null != _objectContainer) {
			ThreadPool4 clientThreadPool = clientThreadPool();
			
			_objectContainer.close();
			_objectContainer = null;
			
			if (null != clientThreadPool) {
				clientThreadPool.join(THREADPOOL_TIMEOUT);
			}
		}
		closeServer();
	}

    private void closeServer() throws Exception {
    	if (null != _server) {
    		ThreadPool4 serverThreadPool = serverThreadPool();
	        _server.close();
	        _server = null;
			
	        if (null != serverThreadPool) {
	        	serverThreadPool.join(THREADPOOL_TIMEOUT);
	        }
    	}
    }	

	public ExtObjectContainer db() {
		return _objectContainer;
	}
    
    protected void doClean() {
        _file.delete();
    }
    
    public ObjectServer server() {
    	return _server;
    }
    
    public boolean embeddedClients() {
    	return _embeddedClient;
    }
    
    /**
	 * Does not accept a clazz which is assignable from OptOutCS, or not
	 * assignable from Db4oTestCase.
	 * 
	 * @return returns false if the clazz is assignable from OptOutCS, or not
	 *         assignable from Db4oTestCase. Otherwise, returns true.
	 */
	public boolean accept(Class clazz) {
		if (!Db4oTestCase.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (OptOutCS.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (!_embeddedClient && (OptOutNetworkingCS.class.isAssignableFrom(clazz))) {
			return false;
		}
		if (_embeddedClient && (OptOutAllButNetworkingCS.class.isAssignableFrom(clazz))) {
			return false;
		}
		return true;
	}
    
	public LocalObjectContainer fileSession() {
		return (LocalObjectContainer)_server.ext().objectContainer();
	}
	
	public void defragment() throws Exception {
		defragment(filePath());
	}
	
	private ObjectContainer openEmbeddedClient() {
		return _server.openClient();
	}
	
	public String label() {
		return buildLabel(_label);
	}

	public int serverPort() {
		return _port;
	}

	private static String filePath() {
		return CrossPlatformServices.databasePath(FILE);
	}

	public void configureAtRuntime(RuntimeConfigureAction action) {
		action.apply(config());
		action.apply(_serverConfig);
	}
}
