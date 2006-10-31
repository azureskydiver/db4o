namespace Db4objects.Db4o
{
	/// <exclude></exclude>
	public sealed class Messages
	{
		public const int CLOSED_OR_OPEN_FAILED = 20;

		public const int FATAL_MSG_ID = 44;

		public const int NOT_IMPLEMENTED = 49;

		public const int ONLY_FOR_INDEXED_FIELDS = 66;

		public const int CLIENT_SERVER_UNSUPPORTED = 67;

		private static string[] i_messages;

		public static string Get(int a_code)
		{
			return Get(a_code, null);
		}

		public static string Get(int a_code, string param)
		{
			if (a_code < 0)
			{
				return param;
			}
			Load();
			if (i_messages == null || a_code > i_messages.Length - 1)
			{
				return "msg[" + a_code + "]";
			}
			string msg = i_messages[a_code];
			if (param != null)
			{
				int pos = msg.IndexOf("%", 0);
				if (pos > -1)
				{
					msg = Sharpen.Runtime.Substring(msg, 0, pos) + "'" + param + "'" + Sharpen.Runtime.Substring
						(msg, pos + 1);
				}
			}
			return msg;
		}

		private static void Load()
		{
			if (i_messages == null)
			{
				i_messages = new string[] { string.Empty, "blocksize should be between 1 and 127"
					, "% close request", "% closed", "Exception opening %", "% opened O.K.", "Class %: Instantiation failed. \n Check custom ObjectConstructor code."
					, "Class %: Instantiation failed.\n Add a constructor for use with db4o, ideally with zero arguments."
					, "renaming %", "rename not possible. % already exists", "rename failed", "File close failed."
					, "File % not available for readwrite access.", "File read access failed.", "File not found: % Creating new file"
					, "Creation of file failed: %", "File write failed.", "File format incompatible."
					, "Uncaught Exception. Engine closed.", "writing log for %", "% is closed. close() was called or open() failed."
					, "Filename not specified.", "The database file is locked by another process.", 
					"Class not available: %. Check CLASSPATH settings.", "finalized while performing a task.\n DO NOT USE CTRL + C OR System.exit() TO STOP THE ENGINE."
					, "Please mail the following to exception@db4o.com:\n <db4o " + Db4objects.Db4o.Db4oVersion
					.NAME + " stacktrace>", "</db4o " + Db4objects.Db4o.Db4oVersion.NAME + " stacktrace>"
					, "Creation of lock file failed: %", "Previous session was not shut down correctly"
					, "This method call is only possible on stored objects", "Could not open port: %"
					, "Server listening on port: %", "Client % connected.", "Client % timed out and closed."
					, "Connection closed by client %.", "Connection closed by server. %.", "% connected to server."
					, "The directory % can neither be found nor created.", "This blob was never stored."
					, "Blob file % not available.", "Failure finding blob filename.", "File does not exist %."
					, "Failed to connect to server.", "No blob data stored.", "Uncaught Exception. db4o engine closed."
					, "Add constructor that won't throw exceptions, configure constructor calls, or provide a translator to class % and make sure the class is deployed to the server with the same package/namespace + assembly name."
					, "This method can only be called before opening the database file.", "AccessibleObject#setAccessible() is not available. Private fields can not be stored."
					, "ObjectTranslator could not be installed: %.", "Not implemented", "% closed by ShutdownHook."
					, "This constraint is not persistent. It has no database identity.", "Add at least one ObjectContainer to the Cluster"
					, "Unsupported Operation", "Database password does not match user-provided password."
					, "Thread interrupted.", "Password can not be null.", "Classes does not match.", 
					"rename() needs to be executed on the server.", "Primitive types like % can not be stored directly. Store and retrieve them in wrapper objects."
					, "Backups can not be run from clients and memory files.", "Backup in progress."
					, "Only use persisted first class objects as keys for IdentityHashMap.", "This functionality is only available from version 5.0 onwards."
					, "By convention a Predicate needs the following method: public boolean match(ExtentClass extent){}"
					, "Old database file format detected. To allow automatic updates call Db4o.configure().allowVersionUpdates(true)."
					, "This functionality is only available for indexed fields.", "This functionality is not supported for db4o clients in Client/Server mode."
					 };
			}
		}

		public static void LogErr(Db4objects.Db4o.Config.IConfiguration config, int code, 
			string msg, System.Exception t)
		{
			if (config == null)
			{
				config = Db4objects.Db4o.Db4o.Configure();
			}
			System.IO.TextWriter ps = ((Db4objects.Db4o.Config4Impl)config).ErrStream();
			new Db4objects.Db4o.Message(msg, code, ps);
			if (t != null)
			{
				new Db4objects.Db4o.Message(null, 25, ps);
				Sharpen.Runtime.PrintStackTrace(t, ps);
				new Db4objects.Db4o.Message(null, 26, ps, false);
			}
		}

		public static void LogMsg(Db4objects.Db4o.Config.IConfiguration config, int code, 
			string msg)
		{
			Db4objects.Db4o.Config4Impl c4i = (Db4objects.Db4o.Config4Impl)config;
			if (c4i == null)
			{
				c4i = (Db4objects.Db4o.Config4Impl)Db4objects.Db4o.Db4o.Configure();
			}
			if (c4i.MessageLevel() > Db4objects.Db4o.YapConst.NONE)
			{
				new Db4objects.Db4o.Message(msg, code, c4i.OutStream());
			}
		}
	}
}
