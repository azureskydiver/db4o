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
namespace com.db4o.replication
{
	/// <summary>
	/// will be called by a
	/// <see cref="com.db4o.replication.ReplicationProcess">com.db4o.replication.ReplicationProcess
	/// 	</see>
	/// upon
	/// replication conflicts. Conflicts occur whenever
	/// <see cref="com.db4o.replication.ReplicationProcess.replicate">com.db4o.replication.ReplicationProcess.replicate
	/// 	</see>
	/// is called with an object that
	/// was modified in both ObjectContainers since the last replication run between
	/// the two.
	/// </summary>
	public interface ReplicationConflictHandler
	{
		/// <summary>the callback method to be implemented to resolve a conflict.</summary>
		/// <remarks>
		/// the callback method to be implemented to resolve a conflict. <br />
		/// <br />
		/// </remarks>
		/// <param name="replicationProcess">
		/// the
		/// <see cref="com.db4o.replication.ReplicationProcess">com.db4o.replication.ReplicationProcess
		/// 	</see>
		/// for which this
		/// ReplicationConflictHandler is registered
		/// </param>
		/// <param name="a">the object modified in the peerA ObjectContainer</param>
		/// <param name="b">the object modified in the peerB ObjectContainer</param>
		/// <returns>
		/// the object (a or b) that should prevail in the conflict or null,
		/// if no action is to be taken. If this would violate the direction
		/// set with
		/// <see cref="com.db4o.replication.ReplicationProcess.setDirection">com.db4o.replication.ReplicationProcess.setDirection
		/// 	</see>
		/// no action will be taken.
		/// </returns>
		/// <seealso cref="com.db4o.replication.ReplicationProcess.peerA">com.db4o.replication.ReplicationProcess.peerA
		/// 	</seealso>
		/// <seealso cref="com.db4o.replication.ReplicationProcess.peerB">com.db4o.replication.ReplicationProcess.peerB
		/// 	</seealso>
		object resolveConflict(com.db4o.replication.ReplicationProcess replicationProcess
			, object a, object b);
	}
}
