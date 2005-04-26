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
	public class GenericField : com.db4o.reflect.ReflectField, com.db4o.DeepClone
	{
		private readonly string _name;

		private readonly com.db4o.reflect.generic.GenericClass _type;

		private readonly bool _primitive;

		private readonly bool _array;

		private readonly bool _nDimensionalArray;

		private int _index = -1;

		public GenericField(string name, com.db4o.reflect.ReflectClass clazz, bool primitive
			, bool array, bool nDimensionalArray)
		{
			_name = name;
			_type = (com.db4o.reflect.generic.GenericClass)clazz;
			_primitive = primitive;
			_array = array;
			_nDimensionalArray = nDimensionalArray;
		}

		public virtual object deepClone(object obj)
		{
			com.db4o.reflect.Reflector reflector = (com.db4o.reflect.Reflector)obj;
			com.db4o.reflect.ReflectClass newReflectClass = null;
			if (_type != null)
			{
				newReflectClass = reflector.forName(_type.getName());
			}
			return new com.db4o.reflect.generic.GenericField(_name, newReflectClass, _primitive
				, _array, _nDimensionalArray);
		}

		public virtual object get(object onObject)
		{
			return ((com.db4o.reflect.generic.GenericObject)onObject)._values[_index];
		}

		public virtual string getName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass getType()
		{
			if (_array)
			{
				return _type.arrayClass();
			}
			return _type;
		}

		public virtual bool isPublic()
		{
			return true;
		}

		public virtual bool isPrimitive()
		{
			return _primitive;
		}

		public virtual bool isStatic()
		{
			return false;
		}

		public virtual bool isTransient()
		{
			return false;
		}

		public virtual void set(object onObject, object value)
		{
			((com.db4o.reflect.generic.GenericObject)onObject)._values[_index] = value;
		}

		public virtual void setAccessible()
		{
		}

		internal virtual void setIndex(int index)
		{
			_index = index;
		}
	}
}
