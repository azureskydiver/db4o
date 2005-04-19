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
	/// <exclude></exclude>
	public class JDK
	{
		internal virtual j4o.lang.Thread addShutdownHook(j4o.lang.Runnable a_runnable)
		{
			return null;
		}

		internal virtual com.db4o.types.Db4oCollections collections(com.db4o.YapStream a_stream
			)
		{
			return null;
		}

		internal virtual j4o.lang.Class constructorClass()
		{
			return null;
		}

		internal virtual object createReferenceQueue()
		{
			return null;
		}

		internal virtual com.db4o.YapRef createYapRef(object a_queue, com.db4o.YapObject 
			a_yapObject, object a_object)
		{
			return null;
		}

		internal virtual void flattenCollection2(com.db4o.YapStream a_stream, object a_object
			, com.db4o.Collection4 col)
		{
		}

		internal virtual void forEachCollectionElement(object a_object, com.db4o.Visitor4
			 a_visitor)
		{
		}

		internal virtual j4o.lang.ClassLoader getContextClassLoader()
		{
			return null;
		}

		internal virtual object getYapRefObject(object a_object)
		{
			return null;
		}

		public virtual int ver()
		{
			return 1;
		}

		internal virtual void killYapRef(object obj)
		{
		}

		internal virtual void Lock(j4o.io.RandomAccessFile file)
		{
			lock (this)
			{
			}
		}

		/// <summary>
		/// use for system classes only, since not ClassLoader
		/// or Reflector-aware
		/// </summary>
		internal virtual bool methodIsAvailable(string className, string methodName, j4o.lang.Class[]
			 _params)
		{
			return false;
		}

		internal virtual void pollReferenceQueue(com.db4o.YapStream a_stream, object a_referenceQueue
			)
		{
		}

		public virtual void registerCollections(com.db4o.reflect.generic.GenericReflector
			 reflector)
		{
		}

		internal virtual void removeShutdownHook(j4o.lang.Thread a_thread)
		{
		}

		public virtual j4o.lang.reflect.Constructor serializableConstructor(j4o.lang.Class
			 clazz)
		{
			return null;
		}

		internal virtual void setAccessible(object a_accessible)
		{
		}

		internal virtual bool isEnum(com.db4o.reflect.Reflector reflector, com.db4o.reflect.ReflectClass
			 clazz)
		{
			return false;
		}

		internal virtual void unlock(j4o.io.RandomAccessFile file)
		{
			lock (this)
			{
			}
		}
	}
}
