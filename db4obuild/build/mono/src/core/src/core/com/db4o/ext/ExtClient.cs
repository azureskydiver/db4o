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
namespace com.db4o.ext
{
	/// <summary>
	/// extended client functionality for the
	/// <see cref="com.db4o.ext.ExtObjectContainer">ExtObjectContainer</see>
	/// interface.
	/// <br /><br />Both
	/// <see cref="com.db4o.Db4o.openClient">Db4o.openClient()</see>
	/// methods always
	/// return an <code>ExtClient</code> object so a cast is possible.<br /><br />
	/// The ObjectContainer functionality is split into multiple interfaces to allow newcomers to
	/// focus on the essential methods.
	/// </summary>
	public interface ExtClient : com.db4o.ext.ExtObjectContainer
	{
		/// <summary>requests opening a different server database file for this client session.
		/// 	</summary>
		/// <remarks>
		/// requests opening a different server database file for this client session.
		/// <br /><br />
		/// This method can be used to switch between database files from the client
		/// side while not having to open a new socket connection or closing the
		/// current one.
		/// <br /><br />
		/// If the database file does not exist on the server, it will be created.
		/// <br /><br />
		/// A typical usecase:<br />
		/// The main database file is used for login, user and rights management only.
		/// Only one single db4o server session needs to be run. Multiple satellite
		/// database files are used for different applications or multiple user circles.
		/// Storing the data to multiple database files has the following advantages:<br />
		/// - easier rights management<br />
		/// - easier backup<br />
		/// - possible later load balancing to multiple servers<br />
		/// - better performance of smaller individual database files<br />
		/// - special debugging database files can be used
		/// <br /><br />
		/// User authorization to the alternative database file will not be checked.
		/// <br /><br />
		/// All persistent references to objects that are currently in memory
		/// are discarded during the switching process.<br /><br />
		/// </remarks>
		/// <param name="fileName">the fully qualified path of the requested database file.</param>
		void switchToFile(string fileName);

		/// <summary>
		/// requests switching back to the main database file after a previous call
		/// to <code>switchToFile(String fileName)</code>.
		/// </summary>
		/// <remarks>
		/// requests switching back to the main database file after a previous call
		/// to <code>switchToFile(String fileName)</code>.
		/// <br /><br />
		/// All persistent references to objects that are currently in memory
		/// are discarded during the switching process.<br /><br />
		/// </remarks>
		void switchToMainFile();
	}
}
