/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class Messages
	{
		private static string[] i_messages;

		public static string get(int a_code)
		{
			return get(a_code, null);
		}

		public static string get(int a_code, string param)
		{
			if (a_code < 0)
			{
				return param;
			}
			load();
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
					msg = msg.Substring(0, pos) + "'" + param + "'" + msg.Substring(pos + 1);
				}
			}
			return msg;
		}

		private static void load()
		{
			if (i_messages == null)
			{
				i_messages = new string[] { "", "blocksize should be between 1 and 127", "% close request"
					, "% closed", "Exception opening %", "% opened O.K.", "Class %: Instantiation failed. \n Check custom ObjectConstructor code."
					, "Class %: Instantiation failed.\n Add a constructor for use with db4o, ideally with zero arguments."
					, "renaming %", "rename not possible. % already exists", "rename failed", "File close failed."
					, "File % not available for readwrite access.", "File read access failed.", "File not found: % Creating new file"
					, "Creation of file failed: %", "File write failed.", "File format incompatible."
					, "Uncaught Exception. Engine closed.", "writing log for %", "% is closed. close() was called or open() failed."
					, "Filename not specified.", "The database file is locked by another process.", 
					"Class not available: %. Check CLASSPATH settings.", "finalized while performing a task.\n DO NOT USE CTRL + C OR System.exit() TO STOP THE ENGINE."
					, "Please mail the following to info@db4o.com:\n <db4o stacktrace>", "</db4o stacktrace>"
					, "Creation of lock file failed: %", "Previous session was not shut down correctly"
					, "This method call is only possible on stored objects", "Could not open port: %"
					, "Server listening on port: %", "Client % connected.", "Client % timed out and closed."
					, "Connection closed by client %.", "Connection closed by server. %.", "% connected to server."
					, "The directory % can neither be found nor created.", "This blob was never stored."
					, "Blob file % not available.", "Failure finding blob filename.", "File does not exist %."
					, "Failed to connect to server.", "No blob data stored.", "Uncaught Exception. db4o engine closed."
					, "Add a constructor that won't throw exceptions, configure constructor calls, or provide a translator to class %."
					, "This method can only be called before opening the database file.", "AccessibleObject#setAccessible() is not available. Private fields can not be stored."
					, "ObjectTranslator could not be installed: %.", "", "% closed by ShutdownHook."
					, "", "", "", "", "Thread interrupted.", "Password can not be null.", "Classes does not match."
					, "rename() needs to be executed on the server.", "Primitive types like % can not be stored directly. Store and retrieve them in wrapper objects."
					, "Backups can not be run from clients and memory files.", "Backup in progress."
					, "Only use persisted first class objects as keys for IdentityHashMap." };
			}
		}
	}
}
