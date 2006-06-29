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
	public class Db4o
	{
		internal static readonly com.db4o.Config4Impl i_config = new com.db4o.Config4Impl
			();

		private static com.db4o.Sessions i_sessions = new com.db4o.Sessions();

		internal static readonly object Lock = Initialize();

		internal static string licTo = "";

		private static bool expirationMessagePrinted;

		private static object Initialize()
		{
			com.db4o.Platform4.GetDefaultConfiguration(i_config);
			return new object();
		}

		/// <summary>prints the version name of this version to <code>System.out</code>.</summary>
		/// <remarks>prints the version name of this version to <code>System.out</code>.</remarks>
		public static void Main(string args)
		{
			j4o.lang.JavaSystem._out.Println(Version());
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

		/// <summary>enters the licensing information into licensed versions.</summary>
		/// <remarks>enters the licensing information into licensed versions.</remarks>
		public static void LicensedTo(string emailAddress)
		{
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
			lock (com.db4o.Db4o.Lock)
			{
				return new com.db4o.YapClient(new com.db4o.foundation.network.YapSocketReal(hostName
					, port), user, password, true);
			}
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
		/// <param name="databaseFileName">the full path to the database file</param>
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
			lock (com.db4o.Db4o.Lock)
			{
				return i_sessions.Open(databaseFileName);
			}
		}

		protected static com.db4o.ObjectContainer OpenMemoryFile1(com.db4o.ext.MemoryFile
			 memoryFile)
		{
			lock (com.db4o.Db4o.Lock)
			{
				if (memoryFile == null)
				{
					memoryFile = new com.db4o.ext.MemoryFile();
				}
				com.db4o.ObjectContainer oc = null;
				try
				{
					oc = new com.db4o.YapMemoryFile(memoryFile);
				}
				catch (System.Exception t)
				{
					com.db4o.Messages.LogErr(i_config, 4, "Memory File", t);
					return null;
				}
				if (oc != null)
				{
					com.db4o.Platform4.PostOpen(oc);
					com.db4o.Messages.LogMsg(i_config, 5, "Memory File");
				}
				return oc;
			}
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
		/// <param name="databaseFileName">the full path to the database file</param>
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
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.YapFile stream = (com.db4o.YapFile)OpenFile(databaseFileName);
				if (stream == null)
				{
					return null;
				}
				lock (stream.Lock())
				{
					return new com.db4o.YapServer(stream, port);
				}
			}
		}

		internal static com.db4o.reflect.Reflector Reflector()
		{
			return i_config.Reflector();
		}

		internal static void ForEachSession(com.db4o.foundation.Visitor4 visitor)
		{
			i_sessions.ForEach(visitor);
		}

		internal static void SessionStopped(com.db4o.Session a_session)
		{
			i_sessions.Remove(a_session);
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
