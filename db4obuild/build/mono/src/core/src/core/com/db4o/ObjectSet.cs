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
	/// <summary>query resultset.</summary>
	/// <remarks>
	/// query resultset.
	/// <br /><br />The <code>ObjectSet</code> class serves as a cursor to
	/// iterate through a set of objects retrieved by a
	/// call to
	/// <see cref="com.db4o.ObjectContainer.get">ObjectContainer.get(template)</see>
	/// .
	/// <br /><br />An <code>ObjectSet</code> can easily be wrapped to a
	/// <code>java.util.List</code> (Java)  / <code>System.Collections.IList</code>  (.NET)
	/// using the source code supplied in ../com/db4o/wrap/
	/// <br /><br />Note that the used
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// needs to remain opened during the
	/// use of an <code>ObjectSet</code> to allow lazy instantiation.
	/// </remarks>
	/// <seealso cref="com.db4o.ext.ExtObjectSet">for extended functionality.</seealso>
	public interface ObjectSet
	{
		/// <summary>returns an ObjectSet with extended functionality.</summary>
		/// <remarks>
		/// returns an ObjectSet with extended functionality.
		/// <br /><br />Every ObjectSet that db4o provides can be casted to
		/// an ExtObjectSet. This method is supplied for your convenience
		/// to work without a cast.
		/// <br /><br />The ObjectSet functionality is split to two interfaces
		/// to allow newcomers to focus on the essential methods.
		/// </remarks>
		com.db4o.ext.ExtObjectSet ext();

		/// <summary>returns <code>true</code> if the <code>ObjectSet</code> has more elements.
		/// 	</summary>
		/// <remarks>returns <code>true</code> if the <code>ObjectSet</code> has more elements.
		/// 	</remarks>
		/// <returns>
		/// boolean - <code>true</code> if the <code>ObjectSet</code> has more
		/// elements.
		/// </returns>
		bool hasNext();

		/// <summary>returns the next object in the <code>ObjectSet</code>.</summary>
		/// <remarks>
		/// returns the next object in the <code>ObjectSet</code>.
		/// <br /><br />
		/// Before returning the Object, next() triggers automatic activation of the
		/// Object with the respective
		/// <see cref="com.db4o.config.Configuration.activationDepth">global</see>
		/// or
		/// <see cref="com.db4o.config.ObjectClass.maximumActivationDepth">class specific</see>
		/// setting.<br /><br />
		/// </remarks>
		/// <returns>the next object in the <code>ObjectSet</code>.</returns>
		object next();

		/// <summary>resets the <code>ObjectSet</code> cursor before the first element.</summary>
		/// <remarks>
		/// resets the <code>ObjectSet</code> cursor before the first element.
		/// <br /><br />A subsequent call to <code>next()</code> will return the first element.
		/// </remarks>
		void reset();

		/// <summary>returns the number of elements in the <code>ObjectSet</code>.</summary>
		/// <remarks>returns the number of elements in the <code>ObjectSet</code>.</remarks>
		/// <returns>the number of elements in the <code>ObjectSet</code>.</returns>
		int size();
	}
}
