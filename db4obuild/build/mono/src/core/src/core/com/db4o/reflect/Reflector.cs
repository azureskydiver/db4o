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
namespace com.db4o.reflect
{
	/// <summary>root of the reflection implementation API.</summary>
	/// <remarks>
	/// root of the reflection implementation API.
	/// <br /><br />The open reflection interface is supplied to allow to implement
	/// reflection functionality on JDKs that do not come with the
	/// java.lang.reflect.* package.<br /><br />
	/// See the code in com.db4o.samples.reflect for a reference implementation
	/// that uses java.lang.reflect.*.
	/// <br /><br />
	/// Use
	/// <see cref="com.db4o.config.Configuration.reflectWith">Db4o.configure().reflectWith(IReflect reflector)
	/// 	</see>
	/// to register the use of your implementation before opening database
	/// files.
	/// </remarks>
	public interface Reflector
	{
		/// <summary>returns an IArray object, the equivalent to java.lang.reflect.Array.</summary>
		/// <remarks>returns an IArray object, the equivalent to java.lang.reflect.Array.</remarks>
		com.db4o.reflect.ReflectArray array();

		/// <summary>specifiy whether parameterized Constructors are supported.</summary>
		/// <remarks>
		/// specifiy whether parameterized Constructors are supported.
		/// <br /><br />The support of Constructors is optional. If Constructors
		/// are not supported, every persistent class needs a public default
		/// constructor with zero parameters.
		/// </remarks>
		bool constructorCallsSupported();

		/// <summary>
		/// returns an IClass class reflector for a class name or null
		/// if no such class is found
		/// </summary>
		com.db4o.reflect.ReflectClass forName(string className);

		/// <summary>returns an IClass for a Class</summary>
		com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz);

		/// <summary>returns an IClass for an object or null if the passed object is null.</summary>
		/// <remarks>returns an IClass for an object or null if the passed object is null.</remarks>
		com.db4o.reflect.ReflectClass forObject(object a_object);

		bool isCollection(com.db4o.reflect.ReflectClass claxx);

		void registerCollection(j4o.lang.Class clazz);

		void registerCollectionUpdateDepth(j4o.lang.Class clazz, int depth);

		int collectionUpdateDepth(com.db4o.reflect.ReflectClass claxx);
	}
}
