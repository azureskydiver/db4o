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
	/// <summary>
	/// candidate for
	/// <see cref="com.db4o.query.Evaluation">com.db4o.query.Evaluation</see>
	/// callbacks.
	/// <br /><br />
	/// During
	/// <see cref="com.db4o.query.Query.execute">query execution</see>
	/// all registered
	/// <see cref="com.db4o.query.Evaluation">com.db4o.query.Evaluation</see>
	/// callback
	/// handlers are called with
	/// <see cref="com.db4o.query.Candidate">com.db4o.query.Candidate</see>
	/// proxies that represent the persistent objects that
	/// meet all other
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// criteria.
	/// <br /><br />
	/// A
	/// <see cref="com.db4o.query.Candidate">com.db4o.query.Candidate</see>
	/// provides access to the persistent object it
	/// represents and allows to specify, whether it is to be included in the
	/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
	/// resultset.
	/// </summary>
	public interface Candidate
	{
		/// <summary>
		/// returns the persistent object that is represented by this query
		/// <see cref="com.db4o.query.Candidate">com.db4o.query.Candidate</see>
		/// .
		/// </summary>
		/// <returns>Object the persistent object.</returns>
		object getObject();

		/// <summary>
		/// specify whether the Candidate is to be included in the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// resultset.
		/// <br /><br />
		/// This method may be called multiple times. The last call prevails.
		/// </summary>
		/// <param name="flag">inclusion.</param>
		void include(bool flag);

		/// <summary>
		/// returns the
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// the Candidate object is stored in.
		/// </summary>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// </returns>
		com.db4o.ObjectContainer objectContainer();
	}
}
