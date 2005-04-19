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
	/// <summary>representation for java.lang.Class.</summary>
	/// <remarks>
	/// representation for java.lang.Class.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectClass
	{
		com.db4o.reflect.ReflectClass getComponentType();

		com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors();

		com.db4o.reflect.ReflectField[] getDeclaredFields();

		com.db4o.reflect.ReflectField getDeclaredField(string name);

		com.db4o.reflect.ReflectClass getDelegate();

		com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses);

		string getName();

		com.db4o.reflect.ReflectClass getSuperclass();

		bool isAbstract();

		bool isArray();

		bool isAssignableFrom(com.db4o.reflect.ReflectClass type);

		bool isCollection();

		bool isInstance(object obj);

		bool isInterface();

		bool isPrimitive();

		bool isSecondClass();

		object newInstance();

		com.db4o.reflect.Reflector reflector();

		/// <summary>
		/// instructs to install or uninstall a special constructor for the
		/// respective platform that avoids calling the constructor for the
		/// respective class
		/// </summary>
		/// <param name="flag">
		/// true to try to install a special constructor, false if
		/// such a constructor is to be removed if present
		/// </param>
		/// <returns>true if the special constructor is in place after the call</returns>
		bool skipConstructor(bool flag);

		object[] toArray(object obj);

		void useConstructor(com.db4o.reflect.ReflectConstructor constructor, object[] _params
			);
	}
}
