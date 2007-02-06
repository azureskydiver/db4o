namespace com.db4o
{
	/// <summary>factory class to start db4o database engines.</summary>
	/// <remarks>
	/// factory class to start db4o database engines.
	/// <br /><br />This class provides static methods to<br />
	/// - open single-user databases
	/// <see cref="com.db4o.Db4o.OpenFile">com.db4o.Db4o.OpenFile</see>
	/// <br />
	/// - open db4o servers
	/// <see cref="com.db4o.Db4o.OpenServer">com.db4o.Db4o.OpenServer</see>
	/// <br />
	/// - connect to db4o servers
	/// <see cref="com.db4o.Db4o.OpenClient">com.db4o.Db4o.OpenClient</see>
	/// <br />
	/// - provide access to the global configuration context
	/// <see cref="com.db4o.Db4o.Configure">com.db4o.Db4o.Configure</see>
	/// <br />
	/// - print the version number of this db4o version
	/// <see cref="com.db4o.Db4o.Main">com.db4o.Db4o.Main</see>
	/// 
	/// </remarks>
	/// <seealso cref="com.db4o.ext.ExtDb4o">ExtDb4o for extended functionality.</seealso>
	public class Db4oFactory
	{
		internal static readonly com.db4o.@internal.Config4Impl i_config = new com.db4o.@internal.Config4Impl
			();

		static Db4oFactory()
		{
			com.db4o.@internal.Platform4.GetDefaultConfiguration(i_config);
		}

		/// <summary>prints the version name of this db4o version to <code>System.out</code>.
		/// 	</summary>
		/// <remarks>prints the version name of this db4o version to <code>System.out</code>.
		/// 	</remarks>
		public static void Main(string[] args)
		{
			j4o.lang.JavaSystem.Out.WriteLine(Version());
		}

		/// <summary>
		/// returns the global db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// context
		/// for the running JVM session.
		/// <br /><br />
		/// The
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// can be overriden in each
		/// <see cref="com.db4o.ext.ExtObjectContainer.Configure">ObjectContainer</see>
		/// .<br /><br />
		/// </summary>
		/// <returns>
		/// the global
		/// <see cref="com.db4o.config.Configuration">configuration</see>
		/// context
		/// </returns>
		public static com.db4o.config.Configuration Configure()
		{
			return i_config;
		}

		/// <summary>
		/// Creates a fresh
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// instance.
		/// </summary>
		/// <returns>a fresh, independent configuration with all options set to their default values
		/// 	</returns>
		public static com.db4o.config.Configuration NewConfiguration()
		{
			com.db4o.@internal.Config4Impl config = new com.db4o.@internal.Config4Impl();
			com.db4o.@internal.Platform4.GetDefaultConfiguration(config);
			return config;
		}

		/// <summary>
		/// Creates a clone of the global db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// .
		/// </summary>
		/// <returns>
		/// a fresh configuration with all option values set to the values
		/// currently configured for the global db4o configuration context
		/// </returns>
		public static com.db4o.config.Configuration CloneConfiguration()
		{
			return (com.db4o.@internal.Config4Impl)((com.db4o.foundation.DeepClone)com.db4o.Db4o
				.Configure()).DeepClone(null);
		}

		/// <summary>
		/// Operates just like
		/// <see cref="com.db4o.Db4o.OpenClient">com.db4o.Db4o.OpenClient</see>
		/// , but uses
		/// the global db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// context.
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// client and connects it to the specified named server and port.
		/// <br /><br />
		/// The server needs to
		/// <see cref="com.db4o.ObjectServer.GrantAccess">allow access</see>
		/// for the specified user and password.
		/// <br /><br />
		/// A client
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// can be cast to
		/// <see cref="com.db4o.ext.ExtClient">ExtClient</see>
		/// to use extended
		/// <see cref="com.db4o.ext.ExtObjectContainer">ExtObjectContainer</see>
		/// 
		/// and
		/// <see cref="com.db4o.ext.ExtClient">ExtClient</see>
		/// methods.
		/// <br /><br />
		/// </summary>
		/// <param name="hostName">the host name</param>
		/// <param name="port">the port the server is using</param>
		/// <param name="user">the user name</param>
		/// <param name="password">the user password</param>
		/// <returns>
		/// an open
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// </returns>
		/// <seealso cref="com.db4o.ObjectServer.GrantAccess">com.db4o.ObjectServer.GrantAccess
		/// 	</seealso>
		public static com.db4o.ObjectContainer OpenClient(string hostName, int port, string
			 user, string password)
		{
			return OpenClient(com.db4o.Db4o.CloneConfiguration(), hostName, port, user, password
				);
		}

		/// <summary>
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// client and connects it to the specified named server and port.
		/// <br /><br />
		/// The server needs to
		/// <see cref="com.db4o.ObjectServer.GrantAccess">allow access</see>
		/// for the specified user and password.
		/// <br /><br />
		/// A client
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// can be cast to
		/// <see cref="com.db4o.ext.ExtClient">ExtClient</see>
		/// to use extended
		/// <see cref="com.db4o.ext.ExtObjectContainer">ExtObjectContainer</see>
		/// 
		/// and
		/// <see cref="com.db4o.ext.ExtClient">ExtClient</see>
		/// methods.
		/// <br /><br />
		/// </summary>
		/// <param name="config">
		/// a custom
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// instance to be obtained via
		/// <see cref="com.db4o.Db4o.NewConfiguration">com.db4o.Db4o.NewConfiguration</see>
		/// </param>
		/// <param name="hostName">the host name</param>
		/// <param name="port">the port the server is using</param>
		/// <param name="user">the user name</param>
		/// <param name="password">the user password</param>
		/// <returns>
		/// an open
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// </returns>
		/// <seealso cref="com.db4o.ObjectServer.GrantAccess">com.db4o.ObjectServer.GrantAccess
		/// 	</seealso>
		public static com.db4o.ObjectContainer OpenClient(com.db4o.config.Configuration config
			, string hostName, int port, string user, string password)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				return new com.db4o.@internal.cs.ClientObjectContainer(config, new com.db4o.foundation.network.NetworkSocket
					(hostName, port), user, password, true);
			}
		}

		/// <summary>
		/// Operates just like
		/// <see cref="com.db4o.Db4o.OpenFile">com.db4o.Db4o.OpenFile</see>
		/// , but uses
		/// the global db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// context.
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// on the specified database file for local use.
		/// <br /><br />Subsidiary calls with the same database file name will return the same
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// object.<br /><br />
		/// Every call to <code>openFile()</code> requires a corresponding
		/// <see cref="com.db4o.ObjectContainer.Close">ObjectContainer.close</see>
		/// .<br /><br />
		/// Database files can only be accessed for readwrite access from one process
		/// (one Java VM) at one time. All versions except for db4o mobile edition use an
		/// internal mechanism to lock the database file for other processes.
		/// <br /><br />
		/// </summary>
		/// <param name="databaseFileName">an absolute or relative path to the database file</param>
		/// <returns>
		/// an open
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.ReadOnly">com.db4o.config.Configuration.ReadOnly
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Encrypt">com.db4o.config.Configuration.Encrypt
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Password">com.db4o.config.Configuration.Password
		/// 	</seealso>
		public static com.db4o.ObjectContainer OpenFile(string databaseFileName)
		{
			return OpenFile(CloneConfiguration(), databaseFileName);
		}

		/// <summary>
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// on the specified database file for local use.
		/// <br /><br />Subsidiary calls with the same database file name will return the same
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// object.<br /><br />
		/// Every call to <code>openFile()</code> requires a corresponding
		/// <see cref="com.db4o.ObjectContainer.Close">ObjectContainer.close</see>
		/// .<br /><br />
		/// Database files can only be accessed for readwrite access from one process
		/// (one Java VM) at one time. All versions except for db4o mobile edition use an
		/// internal mechanism to lock the database file for other processes.
		/// <br /><br />
		/// </summary>
		/// <param name="config">
		/// a custom
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// instance to be obtained via
		/// <see cref="com.db4o.Db4o.NewConfiguration">com.db4o.Db4o.NewConfiguration</see>
		/// </param>
		/// <param name="databaseFileName">an absolute or relative path to the database file</param>
		/// <returns>
		/// an open
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.ReadOnly">com.db4o.config.Configuration.ReadOnly
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Encrypt">com.db4o.config.Configuration.Encrypt
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Password">com.db4o.config.Configuration.Password
		/// 	</seealso>
		public static com.db4o.ObjectContainer OpenFile(com.db4o.config.Configuration config
			, string databaseFileName)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				return com.db4o.@internal.Sessions.Open(config, databaseFileName);
			}
		}

		protected static com.db4o.ObjectContainer OpenMemoryFile1(com.db4o.config.Configuration
			 config, com.db4o.ext.MemoryFile memoryFile)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				if (memoryFile == null)
				{
					memoryFile = new com.db4o.ext.MemoryFile();
				}
				com.db4o.ObjectContainer oc = null;
				try
				{
					oc = new com.db4o.@internal.InMemoryObjectContainer(config, memoryFile);
				}
				catch (System.Exception t)
				{
					com.db4o.@internal.Messages.LogErr(i_config, 4, "Memory File", t);
					return null;
				}
				com.db4o.@internal.Platform4.PostOpen(oc);
				com.db4o.@internal.Messages.LogMsg(i_config, 5, "Memory File");
				return oc;
			}
		}

		/// <summary>
		/// Operates just like
		/// <see cref="com.db4o.Db4o.OpenServer">com.db4o.Db4o.OpenServer</see>
		/// , but uses
		/// the global db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// context.
		/// opens an
		/// <see cref="com.db4o.ObjectServer">ObjectServer</see>
		/// on the specified database file and port.
		/// <br /><br />
		/// If the server does not need to listen on a port because it will only be used
		/// in embedded mode with
		/// <see cref="com.db4o.ObjectServer.OpenClient">com.db4o.ObjectServer.OpenClient</see>
		/// , specify '0' as the
		/// port number.
		/// </summary>
		/// <param name="databaseFileName">an absolute or relative path to the database file</param>
		/// <param name="port">
		/// the port to be used, or 0, if the server should not open a port,
		/// because it will only be used with
		/// <see cref="com.db4o.ObjectServer.OpenClient">com.db4o.ObjectServer.OpenClient</see>
		/// </param>
		/// <returns>
		/// an
		/// <see cref="com.db4o.ObjectServer">ObjectServer</see>
		/// listening
		/// on the specified port.
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.ReadOnly">com.db4o.config.Configuration.ReadOnly
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Encrypt">com.db4o.config.Configuration.Encrypt
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Password">com.db4o.config.Configuration.Password
		/// 	</seealso>
		public static com.db4o.ObjectServer OpenServer(string databaseFileName, int port)
		{
			return OpenServer(CloneConfiguration(), databaseFileName, port);
		}

		/// <summary>
		/// opens an
		/// <see cref="com.db4o.ObjectServer">ObjectServer</see>
		/// on the specified database file and port.
		/// <br /><br />
		/// If the server does not need to listen on a port because it will only be used
		/// in embedded mode with
		/// <see cref="com.db4o.ObjectServer.OpenClient">com.db4o.ObjectServer.OpenClient</see>
		/// , specify '0' as the
		/// port number.
		/// </summary>
		/// <param name="config">
		/// a custom
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// instance to be obtained via
		/// <see cref="com.db4o.Db4o.NewConfiguration">com.db4o.Db4o.NewConfiguration</see>
		/// </param>
		/// <param name="databaseFileName">an absolute or relative path to the database file</param>
		/// <param name="port">
		/// the port to be used, or 0, if the server should not open a port,
		/// because it will only be used with
		/// <see cref="com.db4o.ObjectServer.OpenClient">com.db4o.ObjectServer.OpenClient</see>
		/// </param>
		/// <returns>
		/// an
		/// <see cref="com.db4o.ObjectServer">ObjectServer</see>
		/// listening
		/// on the specified port.
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.ReadOnly">com.db4o.config.Configuration.ReadOnly
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Encrypt">com.db4o.config.Configuration.Encrypt
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.Password">com.db4o.config.Configuration.Password
		/// 	</seealso>
		public static com.db4o.ObjectServer OpenServer(com.db4o.config.Configuration config
			, string databaseFileName, int port)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				com.db4o.@internal.LocalObjectContainer stream = (com.db4o.@internal.LocalObjectContainer
					)OpenFile(config, databaseFileName);
				if (stream == null)
				{
					return null;
				}
				lock (stream.Lock())
				{
					return new com.db4o.@internal.cs.ObjectServerImpl(stream, port);
				}
			}
		}

		internal static com.db4o.reflect.Reflector Reflector()
		{
			return i_config.Reflector();
		}

		/// <summary>returns the version name of the used db4o version.</summary>
		/// <remarks>
		/// returns the version name of the used db4o version.
		/// <br /><br />
		/// </remarks>
		/// <returns>version information as a <code>String</code>.</returns>
		public static string Version()
		{
			return "db4o " + com.db4o.Db4oVersion.NAME;
		}
	}
}
