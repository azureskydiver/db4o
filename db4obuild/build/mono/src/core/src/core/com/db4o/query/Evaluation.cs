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
namespace com.db4o.query
{
	/// <summary>for implementation of callback evaluations.</summary>
	/// <remarks>
	/// for implementation of callback evaluations.
	/// <br /><br />
	/// To constrain a
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// node with your own callback
	/// <code>Evaluation</code>, construct an object that implements the
	/// <code>Evaluation</code> interface and register it by passing it
	/// to
	/// <see cref="com.db4o.query.Query.constrain">com.db4o.query.Query.constrain</see>
	/// .
	/// <br /><br />
	/// Evaluations are called as the last step during query execution,
	/// after all other constraints have been applied. Evaluations in higher
	/// level
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// nodes in the query graph are called first.
	/// <br /><br />Java client/server only:<br />
	/// db4o first attempts to use Java Serialization to allow to pass final
	/// variables to the server. Please make sure that all variables that are
	/// used within the evaluate() method are Serializable. This may include
	/// the class an anonymous Evaluation object is created in. If db4o is
	/// not successful at using Serialization, the Evaluation is transported
	/// to the server in a db4o MemoryFile. In this case final variables can
	/// not be restored.
	/// </remarks>
	public interface Evaluation : j4o.io.Serializable
	{
		/// <summary>
		/// callback method during
		/// <see cref="com.db4o.query.Query.execute">query execution</see>
		/// .
		/// </summary>
		/// <param name="candidate">reference to the candidate persistent object.</param>
		void evaluate(com.db4o.query.Candidate candidate);
	}
}
