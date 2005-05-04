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
 * {@link ObjectContainer ObjectContainer}
 * instances on database files.<br><br>
 * The global db4o {@link com.db4o.config.Configuration Configuration}
 * object for the running Java session is available through the
 * {@link #configure configure} method.
 * <br><br>On running the <code>Db4o</code> class it prints the current
 * version to System.out.
 * @see ExtDb4o ExtDb4o for extended functionality.
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

    /**
	 * returns the global db4o
	 * {@link Configuration Configuration} context 
	 * for the running JVM session.
	 * <br><br>
	 * The {@link Configuration Configuration}
	 * can be overriden in each
	 * {@link com.db4o.ext.ExtObjectContainer#configure ObjectContainer}.<br><br>
	 * @return the global {@link Configuration configuration} context
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
	
    /**
     * opens an {@link ObjectContainer ObjectContainer}
	 * client and connects it to the specified named server and port.
	 * <br><br>
	 * The server needs to
	 * {@link ObjectServer#grantAccess allow access} for the specified user and password.
	 * <br><br>
	 * A client {@link ObjectContainer ObjectContainer} can be cast to 
	 * {@link ExtClient ExtClient} to use extended
	 * {@link ExtObjectContainer ExtObjectContainer} 
	 * and {@link ExtClient ExtClient} methods.
	 * <br><br>
     * @param hostName the host name
     * @param port the port the server is using
     * @param user the user name
     * @param password the user password
	 * @return an open {@link ObjectContainer ObjectContainer}
     * @see ObjectServer#grantAccess
	 */
	public static ObjectContainer openClient(String hostName, int port, String user, String password)
		throws IOException {
		synchronized(Db4o.lock){
			return new YapClient(new YapSocket(hostName, port), user, password, true);
		}
	}
	
    /**
     * opens an {@link ObjectContainer ObjectContainer}
	 * on the specified database file for local use.
	 * <br><br>Subsidiary calls with the same database file name will return the same
	 * {@link ObjectContainer ObjectContainer} object.<br><br>
	 * Every call to <code>openFile()</code> requires a corresponding
 	 * {@link ObjectContainer#close ObjectContainer.close}.<br><br>
 	 * Database files can only be accessed for readwrite access from one process 
 	 * (one Java VM) at one time. All versions except for db4o mobile edition use an
 	 * internal mechanism to lock the database file for other processes. 
     * <br><br>
     * @param databaseFileName the full path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
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
     * opens an {@link ObjectServer ObjectServer}
	 * on the specified database file and port.
     * <br><br>
     * If the server does not need to listen on a port because it will only be used
     * in embedded mode with {@link ObjectServer#openClient}, specify '0' as the
     * port number.
     * @param databaseFileName the full path to the database file
     * @param port the port to be used, or 0, if the server should not open a port,
     * because it will only be used with {@link ObjectServer#openClient()}
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
	 */
	public static final ObjectServer openServer(String databaseFileName, int port) throws DatabaseFileLockedException {
		synchronized(Db4o.lock){
			YapFile stream = (YapFile)openFile(databaseFileName);
            if(stream == null){
                return null;
            }
            synchronized(stream.lock()){
                return new YapServer(stream, port);
            }
		}
	}
	
	static Reflector reflector(){
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
     * @return version information as a <code>String</code>.
     */
    public static final String version () {
    	 return "db4o " + Db4oVersion.name;
    }
}
