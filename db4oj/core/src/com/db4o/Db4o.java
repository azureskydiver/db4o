/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package  com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

/**
 * 
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
	 * 
	 * @deprecated use explicit configuration via {@link Db4oEmbedded#newConfiguration()} instead
     */
	public static Configuration configure(){
		return i_config;
	}
	
	/**
	 * Creates a fresh {@link Configuration Configuration} instance.
	 * 
	 * @return a fresh, independent configuration with all options set to their default values
	 *
	 * @deprecated Use {@link Db4oEmbedded#newConfiguration()} instead.
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
	 * 
	 * @deprecated use explicit configuration via {@link Db4oEmbedded#newConfiguration()} instead
	 */
	public static Configuration cloneConfiguration() {
		return (Config4Impl) ((DeepClone) Db4o.configure()).deepClone(null);
	}

    /**
     * Operates just like {@link Db4oEmbedded#openFile(Configuration, String)}, but uses
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
     * @deprecated Use {@link Db4oEmbedded#openFile(EmbeddedConfiguration, String)} instead
	 */
	public static final ObjectContainer openFile(String databaseFileName)
			throws Db4oIOException, DatabaseFileLockedException,
			IncompatibleFileFormatException, OldFormatException, DatabaseReadOnlyException {
		return Db4o.openFile(cloneConfiguration(),databaseFileName);
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
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4oEmbedded#newConfiguration()}
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
     * @deprecated Use {@link Db4oEmbedded#openFile(EmbeddedConfiguration, String)} instead
	 */
	public static final ObjectContainer openFile(Configuration config,
			String databaseFileName) throws Db4oIOException,
			DatabaseFileLockedException, IncompatibleFileFormatException,
			OldFormatException, DatabaseReadOnlyException {

		return ObjectContainerFactory.openObjectContainer(config, databaseFileName);
	}

	protected static final ObjectContainer openMemoryFile1(
			Configuration config, MemoryFile memoryFile)
			throws Db4oIOException, DatabaseFileLockedException,
			OldFormatException {
		
		Config4Impl.assertIsNotTainted(config);
		
		if(memoryFile == null){
			memoryFile = new MemoryFile();
		}
		
		if (Deploy.debug) {
			System.out.println("db4o Debug is ON");
		}
	    
		ObjectContainer oc = new InMemoryObjectContainer(config,memoryFile);
		Messages.logMsg(config, 5, "Memory File");
		return oc;
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
