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
	public class GenericClass : com.db4o.reflect.ReflectClass
	{
		private static readonly com.db4o.reflect.generic.GenericField[] NO_FIELDS = new com.db4o.reflect.generic.GenericField
			[0];

		private readonly string _name;

		private readonly com.db4o.reflect.ReflectClass _superclass;

		private com.db4o.reflect.generic.GenericField[] _fields = NO_FIELDS;

		public GenericClass(string name, com.db4o.reflect.ReflectClass superclass)
		{
			_name = name;
			_superclass = superclass;
		}

		public virtual void initFields(com.db4o.reflect.generic.GenericField[] fields)
		{
			_fields = fields;
			for (int i = 0; i < _fields.Length; i++)
			{
				_fields[i].setIndex(i);
			}
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType()
		{
			return null;
		}

		public virtual com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors()
		{
			return null;
		}

		public virtual com.db4o.reflect.ReflectField[] getDeclaredFields()
		{
			return _fields;
		}

		public virtual com.db4o.reflect.ReflectField getDeclaredField(string name)
		{
			for (int i = 0; i < _fields.Length; i++)
			{
				if (_fields[i].getName().Equals(name))
				{
					return _fields[i];
				}
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses)
		{
			return null;
		}

		public virtual string getName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass getSuperclass()
		{
			return _superclass;
		}

		public virtual bool isAbstract()
		{
			return false;
		}

		public virtual bool isArray()
		{
			return false;
		}

		public virtual bool isAssignableFrom(com.db4o.reflect.ReflectClass subclassCandidate
			)
		{
			if (subclassCandidate == this)
			{
				return true;
			}
			if (!(subclassCandidate is com.db4o.reflect.generic.GenericClass))
			{
				return false;
			}
			return isAssignableFrom(subclassCandidate.getSuperclass());
		}

		public virtual bool isInstance(object candidate)
		{
			if (!(candidate is com.db4o.reflect.generic.GenericObject))
			{
				return false;
			}
			return isAssignableFrom(((com.db4o.reflect.generic.GenericObject)candidate).dataClass
				());
		}

		public virtual bool isInterface()
		{
			return false;
		}

		public virtual bool isPrimitive()
		{
			return false;
		}

		public virtual object newInstance()
		{
			return new com.db4o.reflect.generic.GenericObject(this);
		}

		public virtual bool skipConstructor(bool flag)
		{
			return false;
		}

		public virtual void useConstructor(com.db4o.reflect.ReflectConstructor constructor
			, object[] _params)
		{
		}
	}
}
