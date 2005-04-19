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
namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericArrayReflector : com.db4o.reflect.ReflectArray
	{
		private readonly com.db4o.reflect.generic.GenericReflector _reflector;

		private readonly com.db4o.reflect.ReflectArray _delegate;

		public GenericArrayReflector(com.db4o.reflect.generic.GenericReflector reflector)
		{
			_reflector = reflector;
			_delegate = reflector.getDelegate().array();
		}

		public virtual int[] dimensions(object arr)
		{
			return _delegate.dimensions(arr);
		}

		public virtual int flatten(object a_shaped, int[] a_dimensions, int a_currentDimension
			, object[] a_flat, int a_flatElement)
		{
			return _delegate.flatten(a_shaped, a_dimensions, a_currentDimension, a_flat, a_flatElement
				);
		}

		public virtual object get(object onArray, int index)
		{
			if (onArray is com.db4o.reflect.generic.GenericArray)
			{
				return ((com.db4o.reflect.generic.GenericObject)onArray)._values[index];
			}
			return _delegate.get(onArray, index);
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType(com.db4o.reflect.ReflectClass
			 claxx)
		{
			claxx = claxx.getDelegate();
			if (claxx is com.db4o.reflect.generic.GenericClass)
			{
				return claxx;
			}
			return _delegate.getComponentType(claxx);
		}

		public virtual int getLength(object array)
		{
			if (array is com.db4o.reflect.generic.GenericArray)
			{
				return ((com.db4o.reflect.generic.GenericArray)array).getLength();
			}
			return _delegate.getLength(array);
		}

		public virtual bool isNDimensional(com.db4o.reflect.ReflectClass a_class)
		{
			if (a_class is com.db4o.reflect.generic.GenericArrayClass)
			{
				return false;
			}
			return _delegate.isNDimensional(a_class.getDelegate());
		}

		public virtual object newInstance(com.db4o.reflect.ReflectClass componentType, int
			 length)
		{
			componentType = componentType.getDelegate();
			if (componentType is com.db4o.reflect.generic.GenericClass)
			{
				return new com.db4o.reflect.generic.GenericArray(((com.db4o.reflect.generic.GenericClass
					)componentType).arrayClass(), length);
			}
			return _delegate.newInstance(componentType, length);
		}

		public virtual object newInstance(com.db4o.reflect.ReflectClass componentType, int[]
			 dimensions)
		{
			return _delegate.newInstance(componentType.getDelegate(), dimensions);
		}

		public virtual void set(object onArray, int index, object element)
		{
			if (onArray is com.db4o.reflect.generic.GenericArray)
			{
				((com.db4o.reflect.generic.GenericArray)onArray)._values[index] = element;
				return;
			}
			_delegate.set(onArray, index, element);
		}

		public virtual int shape(object[] a_flat, int a_flatElement, object a_shaped, int[]
			 a_dimensions, int a_currentDimension)
		{
			return _delegate.shape(a_flat, a_flatElement, a_shaped, a_dimensions, a_currentDimension
				);
		}
	}
}
