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
	/// <summary>extended functionality for the ObjectServer interface.</summary>
	/// <remarks>
	/// extended functionality for the ObjectServer interface.
	/// <br /><br />Every ObjectServer also always is an ExtObjectServer
	/// so a cast is possible.<br /><br />
	/// <see cref="com.db4o.ObjectServer.ext">ObjectServer.ext()</see>
	/// is a convenient method to perform the cast.<br /><br />
	/// The functionality is split to two interfaces to allow newcomers to
	/// focus on the essential methods.
	/// </remarks>
	public interface ExtObjectServer : com.db4o.ObjectServer
	{
		/// <summary>backs up the database file used by the ObjectServer.</summary>
		/// <remarks>
		/// backs up the database file used by the ObjectServer.
		/// <br /><br />While the backup is running, the ObjectServer can continue to be
		/// used. Changes that are made while the backup is in progress, will be applied to
		/// the open ObjectServer and to the backup.<br /><br />
		/// While the backup is running, the ObjectContainer should not be closed.<br /><br />
		/// If a file already exists at the specified path, it will be overwritten.<br /><br />
		/// </remarks>
		/// <param name="path">a fully qualified path</param>
		void backup(string path);

		/// <summary>
		/// returns the
		/// <see cref="com.db4o.config.Configuration">com.db4o.config.Configuration</see>
		/// context for this ObjectServer.
		/// <br /><br />
		/// Upon opening an ObjectServer with any of the factory methods in the
		/// <see cref="com.db4o.Db4o">com.db4o.Db4o</see>
		/// class, the global
		/// <see cref="com.db4o.config.Configuration">com.db4o.config.Configuration</see>
		/// context
		/// is copied into the ObjectServer. The
		/// <see cref="com.db4o.config.Configuration">com.db4o.config.Configuration</see>
		/// can be modified individually for
		/// each ObjectServer without any effects on the global settings.<br /><br />
		/// </summary>
		/// <returns>the Configuration context for this ObjectServer</returns>
		/// <seealso cref="com.db4o.Db4o.configure">com.db4o.Db4o.configure</seealso>
		com.db4o.config.Configuration configure();

		/// <summary>returns the ObjectContainer used by the server.</summary>
		/// <remarks>
		/// returns the ObjectContainer used by the server.
		/// <br /><br />
		/// </remarks>
		/// <returns>the ObjectContainer used by the server</returns>
		com.db4o.ObjectContainer objectContainer();

		/// <summary>removes client access permissions for the specified user.</summary>
		/// <remarks>
		/// removes client access permissions for the specified user.
		/// <br /><br />
		/// </remarks>
		/// <param name="userName">the name of the user</param>
		void revokeAccess(string userName);
	}
}
