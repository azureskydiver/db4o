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
	/// will be passed to the
	/// <see cref="com.db4o.ext.Db4oCallback">com.db4o.ext.Db4oCallback</see>
	/// registered
	/// in a
	/// <see cref="com.db4o.ext.Db4oReplication">com.db4o.ext.Db4oReplication</see>
	/// with #setConflictHandler()
	/// in case an object that is replicated was changed in
	/// both
	/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
	/// s
	/// </summary>
	public interface Db4oReplicationConflict
	{
		/// <summary>
		/// returns the destination
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </summary>
		/// <returns>
		/// the destination
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// </returns>
		com.db4o.ObjectContainer destination();

		/// <summary>
		/// gets the object that caused the conflict from the destination
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </summary>
		/// <returns>
		/// the object as it exists in the destination
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </returns>
		object destinationObject();

		/// <summary>
		/// returns the source
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </summary>
		/// <returns>
		/// the source
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// </returns>
		com.db4o.ObjectContainer source();

		/// <summary>
		/// gets the object that caused the conflict from the source
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </summary>
		/// <returns>
		/// the object as it exists in the source
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </returns>
		object sourceObject();

		/// <summary>
		/// instructs the replication process to store the object from
		/// the source
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// to both ObjectContainers.
		/// <br /><br />If neither #useSource() nor #useDestination() is called in the
		/// <see cref="com.db4o.ext.Db4oCallback">com.db4o.ext.Db4oCallback</see>
		/// , replication will ignore the object that
		/// caused the conflict.
		/// </summary>
		void useSource();

		/// <summary>
		/// instructs the replication process to store the object from
		/// the destination
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// to both ObjectContainers.
		/// <br /><br />If neither #useSource() nor #useDestination() is called in the
		/// <see cref="com.db4o.ext.Db4oCallback">com.db4o.ext.Db4oCallback</see>
		/// , replication will ignore the object that
		/// caused the conflict.
		/// </summary>
		void useDestination();
	}
}
