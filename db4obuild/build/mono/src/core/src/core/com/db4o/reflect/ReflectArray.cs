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
	/// <summary>representation for java.lang.reflect.Array.</summary>
	/// <remarks>
	/// representation for java.lang.reflect.Array.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectArray
	{
		int[] dimensions(object arr);

		int flatten(object a_shaped, int[] a_dimensions, int a_currentDimension, object[]
			 a_flat, int a_flatElement);

		object get(object onArray, int index);

		com.db4o.reflect.ReflectClass getComponentType(com.db4o.reflect.ReflectClass a_class
			);

		int getLength(object array);

		bool isNDimensional(com.db4o.reflect.ReflectClass a_class);

		object newInstance(com.db4o.reflect.ReflectClass componentType, int length);

		object newInstance(com.db4o.reflect.ReflectClass componentType, int[] dimensions);

		void set(object onArray, int index, object element);

		int shape(object[] a_flat, int a_flatElement, object a_shaped, int[] a_dimensions
			, int a_currentDimension);
	}
}
