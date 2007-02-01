/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import java.io.IOException;

import com.db4o.config.Configuration;
import com.db4o.cs.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.YapSocketReal;
import com.db4o.inside.*;
import com.db4o.reflect.Reflector;

/**
 * factory class to start db4o database engines.
 * <br><br>This class provides static methods to<br> 
 * - open single-user databases {@link #openFile(String)} <br>
 * - open db4o servers {@link #openServer(String, int)} <br>
 * - connect to db4o servers {@link #openClient(String, int, String, String)} <br>
 * - provide access to the global configuration context {@link #configure()} <br>
 * - print the version number of this db4o version {@link #main(String[])} 
 * @see ExtDb4o ExtDb4o for extended functionality.
 * 
 * @sharpen.rename Db4oFactory
 */
public class Db4o {
	
	static final Config4Impl i_config = new Config4Impl();
	
	static {
		Platform4.getDefaultConfiguration(i_config);
	}

    /**
	 * prints the version name of this db4o version to <code>System.out</code>.
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
	 * Creates a fresh {@link Configuration Configuration} instance.
	 * 
	 * @return a fresh, independent configuration with all options set to their default values
	 */
	public static Configuration newConfiguration() {
		Config4Impl config = new Config4Impl();
		Platform4.getDefaultConfiguration(config);
		return config;
	}

	/**
	 * Creates a clone of the global db4o {@link Configuration Configuration}.
	 * 
	 * @return a fresh configuration with all option values set to the values
	 * currently configured for the global db4o configuration context
	 */
	public static Configuration cloneConfiguration() {
		return (Config4Impl) ((DeepClone) Db4o.configure()).deepClone(null);
	}

    /**
     * Operates just like {@link Db4o#openClient(Configuration, String, int, String, String)}, but uses
     * the global db4o {@link Configuration Configuration} context.
     * 
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
		return openClient(Db4o.cloneConfiguration(),hostName,port,user,password);
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
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
     * @param hostName the host name
     * @param port the port the server is using
     * @param user the user name
     * @param password the user password
	 * @return an open {@link ObjectContainer ObjectContainer}
     * @see ObjectServer#grantAccess
	 */
	public static ObjectContainer openClient(Configuration config,String hostName, int port, String user, String password)
			throws IOException {
		synchronized(Global4.lock){
			return new YapClient(config,new YapSocketReal(hostName, port), user, password, true);
		}
	}

    /**
     * Operates just like {@link Db4o#openFile(Configuration, String)}, but uses
     * the global db4o {@link Configuration Configuration} context.
     * 
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
     * @param databaseFileName an absolute or relative path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
	 */
	public static final ObjectContainer openFile(String databaseFileName) throws DatabaseFileLockedException {
		return openFile(cloneConfiguration(),databaseFileName);
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
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
     * @param databaseFileName an absolute or relative path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
	 */
	public static final ObjectContainer openFile(Configuration config,String databaseFileName) throws DatabaseFileLockedException {
		synchronized(Global4.lock){
			return Sessions.open(config,databaseFileName);
		}
	}

	protected static final ObjectContainer openMemoryFile1(Configuration config,MemoryFile memoryFile) {
		synchronized(Global4.lock){
			if(memoryFile == null){
				memoryFile = new MemoryFile();
			}
			ObjectContainer oc = null;
			if (Deploy.debug) {
				System.out.println("db4o Debug is ON");
		        oc = new YapMemoryFile(config,memoryFile);
	
				// intentionally no exception handling,
			    // in order to follow uncaught errors
			}
			else {
			    try {
			        oc = new YapMemoryFile(config,memoryFile);
				}
			    catch(Throwable t) {
			        Messages.logErr(i_config, 4, "Memory File", t);
					return null;
			    }
			}
		    Platform4.postOpen(oc);
			Messages.logMsg(i_config, 5, "Memory File");
			return oc;
		}
	}
	
	
	/**
     * Operates just like {@link Db4o#openServer(Configuration, String, int)}, but uses
     * the global db4o {@link Configuration Configuration} context.
     * 
     * opens an {@link ObjectServer ObjectServer}
	 * on the specified database file and port.
     * <br><br>
     * If the server does not need to listen on a port because it will only be used
     * in embedded mode with {@link ObjectServer#openClient}, specify '0' as the
     * port number.
     * @param databaseFileName an absolute or relative path to the database file
     * @param port the port to be used, or 0, if the server should not open a port,
     * because it will only be used with {@link ObjectServer#openClient()}
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
	 */
	public static final ObjectServer openServer(String databaseFileName, int port) throws DatabaseFileLockedException {
		return openServer(cloneConfiguration(),databaseFileName,port);
	}

	/**
     * opens an {@link ObjectServer ObjectServer}
	 * on the specified database file and port.
     * <br><br>
     * If the server does not need to listen on a port because it will only be used
     * in embedded mode with {@link ObjectServer#openClient}, specify '0' as the
     * port number.
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
     * @param databaseFileName an absolute or relative path to the database file
     * @param port the port to be used, or 0, if the server should not open a port,
     * because it will only be used with {@link ObjectServer#openClient()}
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
	 */
	public static final ObjectServer openServer(Configuration config,String databaseFileName, int port) throws DatabaseFileLockedException {
		synchronized(Global4.lock){
			YapFile stream = (YapFile)openFile(config,databaseFileName);
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
	
	
	/**
     * returns the version name of the used db4o version.
     * <br><br>
     * @return version information as a <code>String</code>.
     */
    public static final String version () {
    	 return "db4o " + Db4oVersion.NAME;
    }
}
