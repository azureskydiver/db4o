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
	public class GenericReflector : com.db4o.reflect.Reflector
	{
		private readonly com.db4o.reflect.Reflector _delegate;

		private readonly com.db4o.Hashtable4 _dataClassByName = new com.db4o.Hashtable4(1
			);

		public GenericReflector(com.db4o.reflect.Reflector reflector)
		{
			_delegate = reflector;
		}

		public virtual com.db4o.reflect.ReflectArray array()
		{
			return _delegate.array();
		}

		public virtual int collectionUpdateDepth(com.db4o.reflect.ReflectClass claxx)
		{
			return _delegate.collectionUpdateDepth(claxx);
		}

		public virtual bool constructorCallsSupported()
		{
			return false;
		}

		public virtual com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz)
		{
			return _delegate.forClass(clazz);
		}

		public virtual com.db4o.reflect.ReflectClass forName(string className)
		{
			com.db4o.reflect.ReflectClass dataClass = (com.db4o.reflect.ReflectClass)_dataClassByName
				.get(className);
			return dataClass != null ? dataClass : _delegate.forName(className);
		}

		public virtual com.db4o.reflect.ReflectClass forObject(object _object)
		{
			if (_object is com.db4o.reflect.generic.GenericObject)
			{
				return ((com.db4o.reflect.generic.GenericObject)_object).dataClass();
			}
			return _delegate.forObject(_object);
		}

		public virtual bool isCollection(com.db4o.reflect.ReflectClass claxx)
		{
			return _delegate.isCollection(claxx);
		}

		public virtual void registerCollection(j4o.lang.Class clazz)
		{
			_delegate.registerCollection(clazz);
		}

		public virtual void registerCollectionUpdateDepth(j4o.lang.Class clazz, int depth
			)
		{
			_delegate.registerCollectionUpdateDepth(clazz, depth);
		}

		public virtual void registerDataClass(com.db4o.reflect.generic.GenericClass dataClass
			)
		{
			_dataClassByName.put(dataClass.getName(), dataClass);
		}
	}
}
