/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import java.io.*;

//import javax.servlet.*;
//import javax.servlet.http.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.reflect.*;

/**
 * factory class with static methods to configure and start the engine.
 * <br><br>This class serves as a factory class, to open
 * <a href="ObjectContainer.html"><code>ObjectContainer</code></a>
 * instances on database files.<br><br>
 * The global db4o <a href="config/Configuration.html"><code>Configuration</code></a>
 * object for the running Java session is available through the
 * <a href="#configure()"><code>configure()</code></a> method.
 * <br><br>On running the <code>Db4o</code> class it prints the current
 * version to System.out.
 * @see <a href="ext/ExtDb4o.html">ExtDb4o</a> for extended functionality.
 */
public class Db4o {
	static final Config4Impl i_config = new Config4Impl();
	private static Sessions i_sessions = new Sessions();
	static final Object lock = initialize();
	static String licTo = "";
	private static boolean expirationMessagePrinted;

	private static final Object initialize(){
		Platform.getDefaultConfiguration(i_config);
		return new Object();
	}

    /**
	 * prints the version name of this version to <code>System.out</code>.
     */
	public static void main(String args[]){
		System.out.println(version());
	}
	
	static Class classForName(String name) throws ClassNotFoundException{
		return classForName(null, name);
	}
	
	static Class classForName(YapStream a_stream, String name) throws ClassNotFoundException{
	    try{
	        Config4Impl config = a_stream == null ? i_config : a_stream.i_config;
	        if(config.i_classLoader != null){
	            return config.i_classLoader.loadClass(name);
	        }else{
	            return Class.forName(name);
	        }
	    }catch(Throwable t){
	        return null;
	    }
	}
	

    /**
	 * returns the global db4o
	 * <a href="config/Configuration.html"><code>Configuration</code></a> context 
	 * for the running JVM session.
	 * <br><br>
	 * The <a href="config/Configuration.html"><code>Configuration</code></a>
	 * can be overriden in each
	 * <a href="ObjectContainer.html#configure()"><code>ObjectContainer</code></a>.<br><br>
	 * @return <a href="config/Configuration.html"><code>Configuration</code></a>
	 *  the global Configuration context
     * @see <a href="ObjectContainer.html#configure()">
     * <code>ObjectContainer#configure()</code></a>
     */
	public static Configuration configure(){
		return i_config;
	}
	
	/**
	 * enters the licensing information into licensed versions.
	 */
	public static void licensedTo(String emailAddress){
	    // functionality removed
	}
	
    static void logErr (Configuration config, int code, String msg, Throwable t) {
    	if(config == null){
    		config = i_config;
    	}
		PrintStream ps = ((Config4Impl)config).errStream();
		new Message(msg, code,ps);
		if(t != null){
			new Message(null,25,ps);
			t.printStackTrace(ps);
			new Message(null,26,ps, false);
		}
    }

    static void logMsg (Configuration config, int code, String msg) {
		if(Deploy.debug){
			if(code == 0){
				System.out.println(msg);
				return;
			}
		}
		Config4Impl c4i = (Config4Impl)config;
		if(c4i == null){
		    c4i = i_config;
		}
		
		if(c4i.i_messageLevel > YapConst.NONE){
			new Message(msg,code,c4i.outStream());
		}
    }
    
	static final void notAvailable(){
		throwRuntimeException(29);
	}
	
    /**
     * opens an <a href="ObjectContainer.html"><code>ObjectContainer</code></a>
	 * client and connects it to the specified named server and port.
	 * <br><br>
	 * The server needs to
	 * <a href="ObjectServer.html#grantAccess(java.lang.String, java.lang.String)">
	 * allow access</a> for the specified user and password.
	 * <br><br>
	 * A client <a href="ObjectContainer.html"><code>ObjectContainer</code></a> can be cast to 
	 * <a href="ext/ExtClient.html"><code>ExtClient</code></a> to use extended
	 * <a href="ext/ExtObjectContainer.html"><code>ExtObjectContainer</code></a> 
	 * and <a href="ext/ExtClient.html"><code>ExtClient</code></a> methods.
	 * <br><br>
     * @param hostName the host name
     * @param port the port the server is using
     * @param user the user name
     * @param password the user password
	 * @return an open <a href="ObjectContainer.html"><code>ObjectContainer</code></a>
     * @see <a href="ObjectServer.html#grantAccess(java.lang.String, java.lang.String)">
     * <code>ObjectServer#grantAccess()</code></a>
	 */
	public static ObjectContainer openClient(String hostName, int port, String user, String password)
		throws IOException {
		synchronized(Db4o.lock){
			return new YapClient(new YapSocket(hostName, port), user, password, true);
		}
	}
	
    /**
     * opens an <a href="ObjectContainer.html"><code>ObjectContainer</code></a>
	 * on the specified database file for local use.
	 * <br><br>Subsidiary calls with the same database file name will return the same
	 * <a href="ObjectContainer.html"><code>ObjectContainer</code></a> object.<br><br>
	 * Every call to <code>openFile()</code> requires a corresponding
 	 * <a href="ObjectContainer.html#close()"><code>ObjectContainer.close()</code></a>.<br><br>
 	 * Database files can only be accessed for readwrite access from one process 
 	 * (one Java VM) at one time. All versions except for db4o mobile edition use an
 	 * internal mechanism to lock the database file for other processes. 
     * <br><br>
     * @param databaseFileName the full path to the database file
	 * @return an open <a href="ObjectContainer.html"><code>ObjectContainer</code></a>
     * @see <a href="config/Configuration.html#readOnly(boolean)">
     * <code>Configuration#readOnly()</code></a>
     * @see <a href="config/Configuration.html#encrypt(boolean)">
     * <code>Configuration#encrypt()</code></a>
     * @see <a href="config/Configuration.html#password(java.lang.String)">
     * <code>Configuration#password()</code></a>
	 */
	public static final ObjectContainer openFile(String databaseFileName) throws DatabaseFileLockedException {
		synchronized(Db4o.lock){
			return i_sessions.open(databaseFileName);
		}
	}
	
	protected static final ObjectContainer openMemoryFile1(MemoryFile memoryFile) {
		synchronized(Db4o.lock){
			if(memoryFile == null){
				memoryFile = new MemoryFile();
			}
			ObjectContainer oc = null;
			if (Deploy.debug) {
				System.out.println("db4o Debug is ON");
		        oc = new YapMemoryFile(memoryFile);
	
				// intentionally no exception handling,
			    // in order to follow uncaught errors
			}
			else {
			    try {
			        oc = new YapMemoryFile(memoryFile);
				}
			    catch(Throwable t) {
			        logErr(i_config, 4, "Memory File", t);
					return null;
			    }
			}
			if(oc != null){
			    Platform.postOpen(oc);
				logMsg(i_config, 5, "Memory File");
			}
			return oc;
		}
	}
	
	
	 /**
     * opens an <a href="ObjectServer.html"><code>ObjectServer</code></a>
	 * on the specified database file and port.
     * <br><br>
     * If the server does not need to listen on a port because it will only be used
     * in embedded mode with {@link ObjectServer#openClient()}, specify '0' as the
     * port number.
     * @param databaseFileName the full path to the database file
     * @param port the port to be used, or 0, if the server should not open a port,
     * because it will only be used with {@link ObjectServer#openClient()}
	 * @return an <a href="ObjectServer.html"><code>ObjectServer</code></a> listening
	 * on the specified port.
     * @see <a href="config/Configuration.html#readOnly(boolean)">
     * <code>Configuration#readOnly()</code></a>
     * @see <a href="config/Configuration.html#encrypt(boolean)">
     * <code>Configuration#encrypt()</code></a>
     * @see <a href="config/Configuration.html#password(java.lang.String)">
     * <code>Configuration#password()</code></a>
	 */
	public static final ObjectServer openServer(String databaseFileName, int port) throws DatabaseFileLockedException {
		synchronized(Db4o.lock){
			ObjectContainer oc = openFile(databaseFileName);
			if(oc != null){
				return new YapServer((YapFile)oc, port);
			}
			return null;
		}
	}
	
	static IReflect reflector(){
		return i_config.reflector();
	}
	
	static void forEachSession(Visitor4 visitor){
		i_sessions.forEach(visitor);
	}

	static void sessionStopped(Session a_session){
		i_sessions.remove(a_session);
	}
	
	static final void throwRuntimeException (int code) {
	    throwRuntimeException(code, null, null);
	}
	
    static final void throwRuntimeException (int code, Throwable cause) {
		throwRuntimeException(code, null, cause);
    }
    
    static final void throwRuntimeException (int code, String msg) {
        throwRuntimeException(code, msg, null);
    }

    static final void throwRuntimeException (int code, String msg, Throwable cause) {
		logErr(i_config, code,msg, cause);
        throw new RuntimeException(Messages.get(code, msg));
    }
    

    /**
     * returns the version name of the used db4o version.
     * <br><br>
     * @return version information as a <code>String</code).
     */
    public static final String version () {
    	 return "db4o " + Db4oVersion.name;
    }
}
