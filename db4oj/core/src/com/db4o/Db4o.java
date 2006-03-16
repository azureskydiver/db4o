/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.reflect.*;

/**
 * factory class to start db4o database engines.
 * <br><br>This class provides static methods to<br> 
 * - open single-user databases {@link #openFile(String)} <br>
 * - open db4o servers {@link #openServer(String, int)} <br>
 * - connect to db4o servers {@link #openClient(String, int, String, String)} <br>
 * - provide access to the global configuration context {@link #configure()} <br>
 * - print the version number of this db4o version {@link #main(String[])} 
 * @see ExtDb4o ExtDb4o for extended functionality.
 */
public class Db4o {
	static final Config4Impl i_config = new Config4Impl();
	private static Sessions i_sessions = new Sessions();
	static final Object lock = initialize();
	static String licTo = "";
	private static boolean expirationMessagePrinted;

	private static final Object initialize(){
		Platform4.getDefaultConfiguration(i_config);
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
		return openClient(new YapSocketReal(hostName, port), user, password);
	}

	private static ObjectContainer openClient(YapSocket socket, String user, String password)
			throws IOException {
		synchronized(Db4o.lock){
			return new YapClient(socket, user, password, true);
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
			        Messages.logErr(i_config, 4, "Memory File", t);
					return null;
			    }
			}
			if(oc != null){
			    Platform4.postOpen(oc);
				Messages.logMsg(i_config, 5, "Memory File");
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
	
	/**
     * returns the version name of the used db4o version.
     * <br><br>
     * @return version information as a <code>String</code>.
     */
    public static final String version () {
    	 return "db4o " + Db4oVersion.NAME;
    }
}
