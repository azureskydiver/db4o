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
	/// <summary>the internal representation of a stored class.</summary>
	/// <remarks>the internal representation of a stored class.</remarks>
	public interface StoredClass
	{
		/// <summary>returns the name of this stored class.</summary>
		/// <remarks>returns the name of this stored class.</remarks>
		string getName();

		/// <summary>returns an array of IDs of all stored object instances of this stored class.
		/// 	</summary>
		/// <remarks>returns an array of IDs of all stored object instances of this stored class.
		/// 	</remarks>
		long[] getIDs();

		/// <summary>returns the StoredClass for the parent of the class, this StoredClass represents.
		/// 	</summary>
		/// <remarks>returns the StoredClass for the parent of the class, this StoredClass represents.
		/// 	</remarks>
		com.db4o.ext.StoredClass getParentStoredClass();

		/// <summary>returns all stored fields of this stored class.</summary>
		/// <remarks>returns all stored fields of this stored class.</remarks>
		com.db4o.ext.StoredField[] getStoredFields();

		/// <summary>renames this stored class.</summary>
		/// <remarks>
		/// renames this stored class.
		/// <br /><br />After renaming one or multiple classes the ObjectContainer has
		/// to be closed and reopened to allow internal caches to be refreshed.
		/// <br /><br />.NET: As the name you should provide [Classname, Assemblyname]<br /><br />
		/// </remarks>
		/// <param name="name">the new name</param>
		void rename(string name);

		/// <summary>returns an existing stored field of this stored class.</summary>
		/// <remarks>returns an existing stored field of this stored class.</remarks>
		/// <param name="name">the name of the field</param>
		/// <param name="type">
		/// the type of the field.
		/// There are four possibilities how to supply the type:<br />
		/// - a Class object.  (.NET: a Type object)<br />
		/// - a fully qualified classname.<br />
		/// - any object to be used as a template.<br /><br />
		/// - null, if the first found field should be returned.
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ext.StoredField">com.db4o.ext.StoredField</see>
		/// </returns>
		com.db4o.ext.StoredField storedField(string name, object type);
	}
}
