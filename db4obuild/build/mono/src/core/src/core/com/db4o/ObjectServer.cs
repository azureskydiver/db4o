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
	/// <summary>the db4o server class.</summary>
	/// <remarks>the db4o server class.</remarks>
	/// <seealso cref="com.db4o.Db4o.openServer">Db4o.openServer</seealso>
	/// <seealso cref="com.db4o.ext.ExtObjectServer">ExtObjectServer for extended functionality
	/// 	</seealso>
	public interface ObjectServer
	{
		/// <summary>closes the <code>ObjectServer</code> and writes all cached data.</summary>
		/// <remarks>
		/// closes the <code>ObjectServer</code> and writes all cached data.
		/// <br /><br />
		/// </remarks>
		/// <returns>
		/// true - denotes that the last instance connected to the
		/// used database file was closed.
		/// </returns>
		bool close();

		/// <summary>returns an ObjectServer with extended functionality.</summary>
		/// <remarks>
		/// returns an ObjectServer with extended functionality.
		/// <br /><br />Use this method as a convient accessor to extended methods.
		/// Every ObjectServer can be casted to an ExtObjectServer.
		/// <br /><br />The functionality is split to two interfaces to allow newcomers to
		/// focus on the essential methods.
		/// </remarks>
		com.db4o.ext.ExtObjectServer ext();

		/// <summary>grants client access to the specified user with the specified password.</summary>
		/// <remarks>
		/// grants client access to the specified user with the specified password.
		/// <br /><br />If the user already exists, the password is changed to
		/// the specified password.<br /><br />
		/// </remarks>
		/// <param name="userName">the name of the user</param>
		/// <param name="password">the password to be used</param>
		void grantAccess(string userName, string password);

		/// <summary>opens a client against this server.</summary>
		/// <remarks>
		/// opens a client against this server.
		/// <br /><br />A client opened with this method operates within the same VM
		/// as the server. Since an embedded client can use direct communication, without
		/// an in-between socket connection, performance will be better than a client
		/// opened with
		/// <see cref="com.db4o.Db4o.openClient">com.db4o.Db4o.openClient</see>
		/// <br /><br />Every client has it's own transaction and uses it's own cache
		/// for it's own version of all peristent objects.
		/// </remarks>
		com.db4o.ObjectContainer openClient();
	}
}
