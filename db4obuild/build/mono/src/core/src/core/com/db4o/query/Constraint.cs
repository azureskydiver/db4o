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
	/// constraint to limit the objects returned upon
	/// <see cref="com.db4o.query.Query.execute">query execution</see>
	/// .
	/// <br /><br />
	/// Constraints are constructed by calling
	/// <see cref="com.db4o.query.Query.constrain">Query.constrain()</see>
	/// .
	/// <br /><br />
	/// Constraints can be joined with the methods
	/// <see cref="com.db4o.query.Constraint.and">and()</see>
	/// and
	/// <see cref="com.db4o.query.Constraint.or">or()</see>
	/// .
	/// <br /><br />
	/// The methods to modify the constraint evaluation algorithm may
	/// be merged, to construct combined evaluation rules.
	/// Examples:
	/// <ul>
	/// <li> <code>Constraint#smaller().equal()</code> for "smaller or equal" </li>
	/// <li> <code>Constraint#not().like()</code> for "not like" </li>
	/// <li> <code>Constraint#not().greater().equal()</code> for "not greater or equal" </li>
	/// </ul>
	/// </summary>
	public interface Constraint
	{
		/// <summary>links two Constraints for AND evaluation.</summary>
		/// <remarks>links two Constraints for AND evaluation.</remarks>
		/// <param name="with">
		/// the other
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// </param>
		/// <returns>
		/// a new
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// , that can be used for further calls
		/// to
		/// <see cref="com.db4o.query.Constraint.and">and()</see>
		/// and
		/// <see cref="com.db4o.query.Constraint.or">or()</see>
		/// </returns>
		com.db4o.query.Constraint and(com.db4o.query.Constraint with);

		/// <summary>links two Constraints for OR evaluation.</summary>
		/// <remarks>links two Constraints for OR evaluation.</remarks>
		/// <param name="with">
		/// the other
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// </param>
		/// <returns>
		/// a new
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// , that can be used for further calls
		/// to
		/// <see cref="com.db4o.query.Constraint.and">and()</see>
		/// and
		/// <see cref="com.db4o.query.Constraint.or">or()</see>
		/// </returns>
		com.db4o.query.Constraint or(com.db4o.query.Constraint with);

		/// <summary>sets the evaluation mode to <code>==</code>.</summary>
		/// <remarks>sets the evaluation mode to <code>==</code>.</remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint equal();

		/// <summary>sets the evaluation mode to <code>&gt;</code>.</summary>
		/// <remarks>sets the evaluation mode to <code>&gt;</code>.</remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint greater();

		/// <summary>sets the evaluation mode to <code>&lt;</code>.</summary>
		/// <remarks>sets the evaluation mode to <code>&lt;</code>.</remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint smaller();

		/// <summary>sets the evaluation mode to identity comparison.</summary>
		/// <remarks>sets the evaluation mode to identity comparison.</remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint identity();

		/// <summary>sets the evaluation mode to "like" comparison.</summary>
		/// <remarks>
		/// sets the evaluation mode to "like" comparison.
		/// <br /><br />Constraints are compared to the first characters of a field.<br /><br />
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint like();

		/// <summary>sets the evaluation mode to containment comparison.</summary>
		/// <remarks>sets the evaluation mode to containment comparison.</remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint contains();

		/// <summary>turns on not() comparison.</summary>
		/// <remarks>turns on not() comparison.</remarks>
		/// <returns>
		/// this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		com.db4o.query.Constraint not();

		/// <summary>
		/// returns the Object the query graph was constrained with to
		/// create this
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// .
		/// </summary>
		/// <returns>Object the constraining object.</returns>
		object getObject();
	}
}
