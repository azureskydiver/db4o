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
	/// <summary>handle to a node in the query graph.</summary>
	/// <remarks>
	/// handle to a node in the query graph.
	/// <br /><br />
	/// A node in the query graph can represent multiple
	/// classes, one class or an attribute of a class.<br /><br />The graph
	/// is automatically extended with attributes of added constraints
	/// (see
	/// <see cref="com.db4o.query.Query.constrain">com.db4o.query.Query.constrain</see>
	/// ) and upon calls to
	/// <see cref="com.db4o.query.Query.descend">descend()</see>
	/// that request nodes that do not yet exist.
	/// <br /><br />
	/// References to joined nodes in the query graph can be obtained
	/// by "walking" along the nodes of the graph with the method
	/// <see cref="com.db4o.query.Query.descend">descend()</see>
	/// .
	/// <br /><br />
	/// <see cref="com.db4o.query.Query.execute">com.db4o.query.Query.execute</see>
	/// evaluates the entire graph against all persistent objects.
	/// <br /><br />
	/// <see cref="com.db4o.query.Query.execute">com.db4o.query.Query.execute</see>
	/// can be called from any
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// node
	/// of the graph. It will return an
	/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
	/// filled with
	/// objects of the class/classes that the node, it was called from,
	/// represents.
	/// </remarks>
	public interface Query
	{
		/// <summary>adds a constraint to this node.</summary>
		/// <remarks>
		/// adds a constraint to this node.
		/// <br /><br />
		/// If the constraint contains attributes that are not yet
		/// present in the query graph, the query graph is extended
		/// accordingly.
		/// <br /><br />
		/// Special behaviour for:
		/// <ul>
		/// <li> class
		/// <see cref="j4o.lang.Class">j4o.lang.Class</see>
		/// : confine the result to objects of one
		/// class or to objects implementing an interface.</li>
		/// <li> interface
		/// <see cref="com.db4o.query.Evaluation">com.db4o.query.Evaluation</see>
		/// : run
		/// evaluation callbacks against all candidates.</li>
		/// </ul>
		/// </remarks>
		/// <param name="constraint">the constraint to be added to this Query.</param>
		/// <returns>
		/// 
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// a new
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// for this
		/// query node or <code>null</code> for objects implementing the
		/// <see cref="com.db4o.query.Evaluation">com.db4o.query.Evaluation</see>
		/// interface.
		/// </returns>
		com.db4o.query.Constraint constrain(object constraint);

		/// <summary>
		/// returns a
		/// <see cref="com.db4o.query.Constraints">com.db4o.query.Constraints</see>
		/// object that holds an array of all constraints on this node.
		/// </summary>
		/// <returns>
		/// 
		/// <see cref="com.db4o.query.Constraints">com.db4o.query.Constraints</see>
		/// on this query node.
		/// </returns>
		com.db4o.query.Constraints constraints();

		/// <summary>returns a reference to a descendant node in the query graph.</summary>
		/// <remarks>
		/// returns a reference to a descendant node in the query graph.
		/// <br /><br />If the node does not exist, it will be created.
		/// <br /><br />
		/// All classes represented in the query node are tested, whether
		/// they contain a field with the specified field name. The
		/// descendant Query node will be created from all possible candidate
		/// classes.
		/// </remarks>
		/// <param name="fieldName">path to the descendant.</param>
		/// <returns>
		/// descendant
		/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
		/// node
		/// </returns>
		com.db4o.query.Query descend(string fieldName);

		/// <summary>
		/// executes the
		/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
		/// .
		/// </summary>
		/// <returns>
		/// 
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// - the result of the
		/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
		/// .
		/// </returns>
		com.db4o.ObjectSet execute();

		/// <summary>
		/// adds an ascending ordering criteria to this node of
		/// the query graph.
		/// </summary>
		/// <remarks>
		/// adds an ascending ordering criteria to this node of
		/// the query graph. Multiple ordering criteria will be applied
		/// in the order they were called.
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
		/// object to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Query orderAscending();

		/// <summary>
		/// adds a descending order criteria to this node of
		/// the query graph.
		/// </summary>
		/// <remarks>
		/// adds a descending order criteria to this node of
		/// the query graph. Multiple ordering criteria will be applied
		/// in the order they were called.
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
		/// object to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Query orderDescending();
	}
}
