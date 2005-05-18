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
	public class GenericClass : com.db4o.reflect.ReflectClass, com.db4o.DeepClone
	{
		private static readonly com.db4o.reflect.generic.GenericField[] NO_FIELDS = new com.db4o.reflect.generic.GenericField
			[0];

		private readonly com.db4o.reflect.generic.GenericReflector _reflector;

		private readonly com.db4o.reflect.ReflectClass _delegate;

		private readonly string _name;

		private com.db4o.reflect.ReflectClass _superclass;

		private com.db4o.reflect.generic.GenericClass _array;

		private bool _isSecondClass;

		private com.db4o.reflect.generic.GenericField[] _fields = NO_FIELDS;

		public GenericClass(com.db4o.reflect.generic.GenericReflector reflector, com.db4o.reflect.ReflectClass
			 delegateClass, string name, com.db4o.reflect.ReflectClass superclass)
		{
			_reflector = reflector;
			_delegate = delegateClass;
			_name = name;
			_superclass = superclass;
		}

		public virtual com.db4o.reflect.generic.GenericClass arrayClass()
		{
			if (_array != null)
			{
				return _array;
			}
			_array = new com.db4o.reflect.generic.GenericArrayClass(_reflector, this, _name, 
				_superclass);
			_array._isSecondClass = _isSecondClass;
			return _array;
		}

		public virtual object deepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector reflector = (com.db4o.reflect.generic.GenericReflector
				)obj;
			com.db4o.reflect.ReflectClass superClass = null;
			if (_superclass != null)
			{
				_superclass = reflector.forName(_superclass.getName());
			}
			com.db4o.reflect.generic.GenericClass ret = new com.db4o.reflect.generic.GenericClass
				(reflector, _delegate, _name, superClass);
			ret._isSecondClass = _isSecondClass;
			com.db4o.reflect.generic.GenericField[] fields = new com.db4o.reflect.generic.GenericField
				[_fields.Length];
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = (com.db4o.reflect.generic.GenericField)_fields[i].deepClone(reflector
					);
			}
			ret.initFields(fields);
			return ret;
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType()
		{
			if (_delegate != null)
			{
				return _delegate.getComponentType();
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors()
		{
			if (_delegate != null)
			{
				return _delegate.getDeclaredConstructors();
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectField getDeclaredField(string name)
		{
			if (_delegate != null)
			{
				return _delegate.getDeclaredField(name);
			}
			for (int i = 0; i < _fields.Length; i++)
			{
				if (_fields[i].getName().Equals(name))
				{
					return _fields[i];
				}
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectField[] getDeclaredFields()
		{
			if (_delegate != null)
			{
				return _delegate.getDeclaredFields();
			}
			return _fields;
		}

		public virtual com.db4o.reflect.ReflectClass getDelegate()
		{
			if (_delegate != null)
			{
				return _delegate;
			}
			return this;
		}

		public virtual com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses)
		{
			if (_delegate != null)
			{
				return _delegate.getMethod(methodName, paramClasses);
			}
			return null;
		}

		public virtual string getName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass getSuperclass()
		{
			if (_superclass != null)
			{
				return _superclass;
			}
			if (_delegate == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass delegateSuperclass = _delegate.getSuperclass();
			if (delegateSuperclass != null)
			{
				_superclass = _reflector.ensureDelegate(delegateSuperclass);
			}
			return _superclass;
		}

		public virtual void initFields(com.db4o.reflect.generic.GenericField[] fields)
		{
			_fields = fields;
			for (int i = 0; i < _fields.Length; i++)
			{
				_fields[i].setIndex(i);
			}
		}

		public virtual bool isAbstract()
		{
			if (_delegate != null)
			{
				return _delegate.isAbstract();
			}
			return false;
		}

		public virtual bool isArray()
		{
			if (_delegate != null)
			{
				return _delegate.isArray();
			}
			return false;
		}

		public virtual bool isAssignableFrom(com.db4o.reflect.ReflectClass subclassCandidate
			)
		{
			if (subclassCandidate == null)
			{
				return false;
			}
			if (_delegate != null)
			{
				if (subclassCandidate is com.db4o.reflect.generic.GenericClass)
				{
					subclassCandidate = ((com.db4o.reflect.generic.GenericClass)subclassCandidate).getDelegate
						();
				}
				return _delegate.isAssignableFrom(subclassCandidate);
			}
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

		public virtual bool isCollection()
		{
			return _reflector.isCollection(this);
		}

		public virtual bool isInstance(object candidate)
		{
			if (_delegate != null)
			{
				return _delegate.isInstance(candidate);
			}
			if (!(candidate is com.db4o.reflect.generic.GenericObject))
			{
				return false;
			}
			return isAssignableFrom(((com.db4o.reflect.generic.GenericObject)candidate)._class
				);
		}

		public virtual bool isInterface()
		{
			if (_delegate != null)
			{
				return _delegate.isInterface();
			}
			return false;
		}

		public virtual bool isPrimitive()
		{
			if (_delegate != null)
			{
				return _delegate.isPrimitive();
			}
			return false;
		}

		public virtual bool isSecondClass()
		{
			if (isPrimitive())
			{
				return true;
			}
			return _isSecondClass;
		}

		public virtual object newInstance()
		{
			if (_delegate != null)
			{
				return _delegate.newInstance();
			}
			return new com.db4o.reflect.generic.GenericObject(this);
		}

		public virtual com.db4o.reflect.Reflector reflector()
		{
			if (_delegate != null)
			{
				return _delegate.reflector();
			}
			return _reflector;
		}

		internal virtual void setSecondClass()
		{
			_isSecondClass = true;
		}

		public virtual bool skipConstructor(bool flag)
		{
			if (_delegate != null)
			{
				return _delegate.skipConstructor(flag);
			}
			return false;
		}

		public virtual object[] toArray(object obj)
		{
			if (!isCollection())
			{
				return new object[] { obj };
			}
			return com.db4o.Platform.collectionToArray(_reflector.getStream(), obj);
		}

		public override string ToString()
		{
			return "GenericClass " + _name;
		}

		public virtual void useConstructor(com.db4o.reflect.ReflectConstructor constructor
			, object[] _params)
		{
			if (_delegate != null)
			{
				_delegate.useConstructor(constructor, _params);
			}
		}

		public virtual j4o.lang.Class getJdkClass()
		{
			return (_delegate == null) ? j4o.lang.Class.getClassForType(typeof(object)) : _delegate
				.getJdkClass();
		}
	}
}
