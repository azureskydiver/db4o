/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
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
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws InvalidPasswordException password supplied for the connection is
     * invalid.
	 */
	public static ObjectContainer openClient(String hostName, int port,
			String user, String password) throws Db4oIOException,
			OldFormatException, InvalidPasswordException {
		return openClient(Db4o.cloneConfiguration(), hostName, port, user,
				password);
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
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws InvalidPasswordException password supplied for the connection is
     * invalid.
	 */
	public static ObjectContainer openClient(Configuration config,
			String hostName, int port, String user, String password)
			throws Db4oIOException, OldFormatException,
			InvalidPasswordException {
		return openClient(config, hostName, port, user, password, new PlainSocketFactory());
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
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws InvalidPasswordException password supplied for the connection is
     * invalid.
	 */
	public static ObjectContainer openClient(Configuration config,
			String hostName, int port, String user, String password, NativeSocketFactory socketFactory)
			throws Db4oIOException, OldFormatException,
			InvalidPasswordException {
		if (user == null || password == null) {
			throw new InvalidPasswordException();
		}
		NetworkSocket networkSocket = new NetworkSocket(socketFactory, hostName, port);
		return new ClientObjectContainer(config, networkSocket, user, password, true);
	}

    /**
     * Operates just like {@link Db4o#openFile(Configuration, String)}, but uses
     * the global db4o {@link Configuration Configuration} context.
     * 
     * opens an {@link ObjectContainer ObjectContainer}
	 * on the specified database file for local use.
	 * <br><br>A database file can only be opened once, subsequent attempts to open
	 * another {@link ObjectContainer ObjectContainer} against the same file will result in
	 * a {@link DatabaseFileLockedException DatabaseFileLockedException}.<br><br>
 	 * Database files can only be accessed for readwrite access from one process 
 	 * (one Java VM) at one time. All versions except for db4o mobile edition use an
 	 * internal mechanism to lock the database file for other processes. 
     * <br><br>
     * @param databaseFileName an absolute or relative path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseFileLockedException the required database file is locked by 
     * another process.
     * @throws IncompatibleFileFormatException runtime 
     * {@link com.db4o.config.Configuration configuration} is not compatible
     * with the configuration of the database file. 
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws DatabaseReadOnlyException database was configured as read-only.
	 */
	public static final ObjectContainer openFile(String databaseFileName)
			throws Db4oIOException, DatabaseFileLockedException,
			IncompatibleFileFormatException, OldFormatException, DatabaseReadOnlyException {
		return openFile(cloneConfiguration(),databaseFileName);
	}

    /**
     * opens an {@link ObjectContainer ObjectContainer}
	 * on the specified database file for local use.
	 * <br><br>A database file can only be opened once, subsequent attempts to open
	 * another {@link ObjectContainer ObjectContainer} against the same file will result in
	 * a {@link DatabaseFileLockedException DatabaseFileLockedException}.<br><br>
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
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseFileLockedException the required database file is locked by 
     * another process.
     * @throws IncompatibleFileFormatException runtime 
     * {@link com.db4o.config.Configuration configuration} is not compatible
     * with the configuration of the database file. 
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws DatabaseReadOnlyException database was configured as read-only.
	 */
	public static final ObjectContainer openFile(Configuration config,
			String databaseFileName) throws Db4oIOException,
			DatabaseFileLockedException, IncompatibleFileFormatException,
			OldFormatException, DatabaseReadOnlyException {
		return ObjectContainerFactory.openObjectContainer(config,
				databaseFileName);
	}

	protected static final ObjectContainer openMemoryFile1(
			Configuration config, MemoryFile memoryFile)
			throws Db4oIOException, DatabaseFileLockedException,
			OldFormatException {
		if(memoryFile == null){
			memoryFile = new MemoryFile();
		}
		
		if (Deploy.debug) {
			System.out.println("db4o Debug is ON");
		}
	    
		ObjectContainer oc = new InMemoryObjectContainer(config,memoryFile);
		Messages.logMsg(i_config, 5, "Memory File");
		return oc;
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
     * because it will only be used with {@link ObjectServer#openClient()}.
     * Specify a value < 0 if an arbitrary free port should be chosen - see {@link ExtObjectServer#port()}.
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseFileLockedException the required database file is locked by 
     * another process.
     * @throws IncompatibleFileFormatException runtime 
     * {@link com.db4o.config.Configuration configuration} is not compatible
     * with the configuration of the database file. 
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws DatabaseReadOnlyException database was configured as read-only.
	 */
	public static final ObjectServer openServer(String databaseFileName,
			int port) throws Db4oIOException, IncompatibleFileFormatException,
			OldFormatException, DatabaseFileLockedException,
			DatabaseReadOnlyException {
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
     * because it will only be used with {@link ObjectServer#openClient()}.
     * Specify a value < 0 if an arbitrary free port should be chosen - see {@link ExtObjectServer#port()}.
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseFileLockedException the required database file is locked by 
     * another process.
     * @throws IncompatibleFileFormatException runtime 
     * {@link com.db4o.config.Configuration configuration} is not compatible
     * with the configuration of the database file. 
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws DatabaseReadOnlyException database was configured as read-only.
	 */
	public static final ObjectServer openServer(Configuration config,
			String databaseFileName, int port) throws Db4oIOException,
			IncompatibleFileFormatException, OldFormatException,
			DatabaseFileLockedException, DatabaseReadOnlyException {
		return openServer(config, databaseFileName, port, new PlainSocketFactory());
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
     * because it will only be used with {@link ObjectServer#openClient()}.
     * Specify a value < 0 if an arbitrary free port should be chosen - see {@link ExtObjectServer#port()}.
     * @param socketFactory the {@link NativeSocketFactory} to be used for socket creation
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseFileLockedException the required database file is locked by 
     * another process.
     * @throws IncompatibleFileFormatException runtime 
     * {@link com.db4o.config.Configuration configuration} is not compatible
     * with the configuration of the database file. 
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws DatabaseReadOnlyException database was configured as read-only.
	 */
	public static final ObjectServer openServer(Configuration config,
			String databaseFileName, int port, NativeSocketFactory socketFactory) throws Db4oIOException,
			IncompatibleFileFormatException, OldFormatException,
			DatabaseFileLockedException, DatabaseReadOnlyException {
		LocalObjectContainer stream = (LocalObjectContainer)openFile(config,databaseFileName);
        if(stream == null){
            return null;
        }
        synchronized(stream.lock()){
            return new ObjectServerImpl(stream, port, socketFactory);
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
