
namespace com.db4o
{
	/// <summary>factory class with static methods to configure and start the engine.</summary>
	/// <remarks>
	/// factory class with static methods to configure and start the engine.
	/// <br /><br />This class serves as a factory class, to open
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// instances on database files.<br /><br />
	/// The global db4o
	/// <see cref="com.db4o.config.Configuration">Configuration</see>
	/// object for the running Java session is available through the
	/// <see cref="com.db4o.Db4o.configure">configure</see>
	/// method.
	/// <br /><br />On running the <code>Db4o</code> class it prints the current
	/// version to System.out.
	/// </remarks>
	/// <seealso cref="com.db4o.ext.ExtDb4o">ExtDb4o for extended functionality.</seealso>
	public class Db4o
	{
		internal static readonly com.db4o.Config4Impl i_config = new com.db4o.Config4Impl
			();

		private static com.db4o.Sessions i_sessions = new com.db4o.Sessions();

		internal static readonly object Lock = initialize();

		internal static string licTo = "";

		private static bool expirationMessagePrinted;

		private static object initialize()
		{
			com.db4o.Platform4.getDefaultConfiguration(i_config);
			return new object();
		}

		/// <summary>prints the version name of this version to <code>System.out</code>.</summary>
		/// <remarks>prints the version name of this version to <code>System.out</code>.</remarks>
		public static void Main(string args)
		{
			j4o.lang.JavaSystem._out.println(version());
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
		/// <see cref="com.db4o.ext.ExtObjectContainer.configure">ObjectContainer</see>
		/// .<br /><br />
		/// </summary>
		/// <returns>
		/// the global
		/// <see cref="com.db4o.config.Configuration">configuration</see>
		/// context
		/// </returns>
		public static com.db4o.config.Configuration configure()
		{
			return i_config;
		}

		/// <summary>enters the licensing information into licensed versions.</summary>
		/// <remarks>enters the licensing information into licensed versions.</remarks>
		public static void licensedTo(string emailAddress)
		{
		}

		/// <summary>
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// client and connects it to the specified named server and port.
		/// <br /><br />
		/// The server needs to
		/// <see cref="com.db4o.ObjectServer.grantAccess">allow access</see>
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
		/// <seealso cref="com.db4o.ObjectServer.grantAccess">com.db4o.ObjectServer.grantAccess
		/// 	</seealso>
		public static com.db4o.ObjectContainer openClient(string hostName, int port, string
			 user, string password)
		{
			lock (com.db4o.Db4o.Lock)
			{
				return new com.db4o.YapClient(new com.db4o.foundation.network.YapClientSocket(hostName
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
		/// <see cref="com.db4o.ObjectContainer.close">ObjectContainer.close</see>
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
		/// <seealso cref="com.db4o.config.Configuration.readOnly">com.db4o.config.Configuration.readOnly
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.encrypt">com.db4o.config.Configuration.encrypt
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.password">com.db4o.config.Configuration.password
		/// 	</seealso>
		public static com.db4o.ObjectContainer openFile(string databaseFileName)
		{
			lock (com.db4o.Db4o.Lock)
			{
				return i_sessions.open(databaseFileName);
			}
		}

		protected static com.db4o.ObjectContainer openMemoryFile1(com.db4o.ext.MemoryFile
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
					com.db4o.Messages.logErr(i_config, 4, "Memory File", t);
					return null;
				}
				if (oc != null)
				{
					com.db4o.Platform4.postOpen(oc);
					com.db4o.Messages.logMsg(i_config, 5, "Memory File");
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
		/// <see cref="com.db4o.ObjectServer.openClient">com.db4o.ObjectServer.openClient</see>
		/// , specify '0' as the
		/// port number.
		/// </summary>
		/// <param name="databaseFileName">the full path to the database file</param>
		/// <param name="port">
		/// the port to be used, or 0, if the server should not open a port,
		/// because it will only be used with
		/// <see cref="com.db4o.ObjectServer.openClient">com.db4o.ObjectServer.openClient</see>
		/// </param>
		/// <returns>
		/// an
		/// <see cref="com.db4o.ObjectServer">ObjectServer</see>
		/// listening
		/// on the specified port.
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.readOnly">com.db4o.config.Configuration.readOnly
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.encrypt">com.db4o.config.Configuration.encrypt
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.password">com.db4o.config.Configuration.password
		/// 	</seealso>
		public static com.db4o.ObjectServer openServer(string databaseFileName, int port)
		{
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.YapFile stream = (com.db4o.YapFile)openFile(databaseFileName);
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

		internal static com.db4o.reflect.Reflector reflector()
		{
			return i_config.reflector();
		}

		internal static void forEachSession(com.db4o.foundation.Visitor4 visitor)
		{
			i_sessions.forEach(visitor);
		}

		internal static void sessionStopped(com.db4o.Session a_session)
		{
			i_sessions.remove(a_session);
		}

		/// <summary>returns the version name of the used db4o version.</summary>
		/// <remarks>
		/// returns the version name of the used db4o version.
		/// <br /><br />
		/// </remarks>
		/// <returns>version information as a <code>String</code>.</returns>
		public static string version()
		{
			return "db4o " + com.db4o.Db4oVersion.NAME;
		}
	}
}
