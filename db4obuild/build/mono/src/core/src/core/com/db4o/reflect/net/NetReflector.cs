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
namespace com.db4o.reflect.net
{
	public class NetReflector : com.db4o.reflect.Reflector
	{
		private readonly com.db4o.reflect.ReflectArray _array;

		private readonly com.db4o.Hashtable4 _byClass;

		private readonly com.db4o.Hashtable4 _byName;

		private readonly com.db4o.Collection4 _collectionClasses;

		private readonly com.db4o.Collection4 _collectionUpdateDepths;

		public NetReflector()
		{
			_array = new com.db4o.reflect.net.NetArray(this);
			_byClass = new com.db4o.Hashtable4(1);
			_byName = new com.db4o.Hashtable4(1);
			_collectionClasses = new com.db4o.Collection4();
			_collectionUpdateDepths = new com.db4o.Collection4();
		}

		public virtual com.db4o.reflect.ReflectArray array()
		{
			return _array;
		}

		private com.db4o.reflect.ReflectClass addClass(string className, j4o.lang.Class clazz
			)
		{
			com.db4o.reflect.net.NetClass cClass = new com.db4o.reflect.net.NetClass(this, clazz
				);
			_byClass.put(clazz, cClass);
			_byName.put(className, cClass);
			return cClass;
		}

		public virtual bool constructorCallsSupported()
		{
			return true;
		}

		public virtual com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass iClass = (com.db4o.reflect.ReflectClass)_byClass.get
				(clazz);
			if (iClass != null)
			{
				return iClass;
			}
			return addClass(clazz.getName(), clazz);
		}

		public virtual com.db4o.reflect.ReflectClass forName(string className)
		{
			com.db4o.reflect.ReflectClass iClass = (com.db4o.reflect.ReflectClass)_byName.get
				(className);
			if (iClass != null)
			{
				return iClass;
			}
			j4o.lang.Class clazz;
			try
			{
				clazz = j4o.lang.Class.forName(className);
			}
			catch (j4o.lang.ClassNotFoundException exc)
			{
				clazz = null;
			}
			if (clazz == null)
			{
				return null;
			}
			return addClass(className, clazz);
		}

		public virtual com.db4o.reflect.ReflectClass forObject(object a_object)
		{
			if (a_object == null)
			{
				return null;
			}
			return forClass(j4o.lang.Class.getClassForObject(a_object));
		}

		public virtual bool isCollection(com.db4o.reflect.ReflectClass candidate)
		{
            if(candidate.isArray()){
                return false;
            }
            if ( typeof(System.Collections.ICollection).IsAssignableFrom(
                ((com.db4o.reflect.net.NetClass)candidate).getNetType())){
                return true;
            }


			com.db4o.Iterator4 it = _collectionClasses.iterator();
			while (it.hasNext())
			{
				com.db4o.reflect.ReflectClass claxx = (com.db4o.reflect.ReflectClass)it.next();
				if (claxx.isAssignableFrom(candidate))
				{
					return true;
				}
			}
			return false;
		}

		public virtual bool methodCallsSupported()
		{
			return true;
		}

		public virtual void registerCollection(j4o.lang.Class clazz)
		{
			com.db4o.reflect.ReflectClass claxx = forClass(clazz);
			_collectionClasses.add(claxx);
		}

		public virtual void registerCollectionUpdateDepth(j4o.lang.Class clazz, int depth
			)
		{
			object[] entry = new object[] { forClass(clazz), System.Convert.ToInt32(depth) };
			_collectionUpdateDepths.add(entry);
		}

		internal static j4o.lang.Class[] toNative(com.db4o.reflect.ReflectClass[] claxx)
		{
			j4o.lang.Class[] clazz = null;
			if (claxx != null)
			{
				clazz = new j4o.lang.Class[claxx.Length];
				for (int i = 0; i < claxx.Length; i++)
				{
					if (claxx[i] != null)
					{
						clazz[i] = ((com.db4o.reflect.net.NetClass)claxx[i]).getJavaClass();
					}
				}
			}
			return clazz;
		}

		public static com.db4o.reflect.ReflectClass[] toMeta(com.db4o.reflect.Reflector reflector
			, j4o.lang.Class[] clazz)
		{
			com.db4o.reflect.ReflectClass[] claxx = null;
			if (clazz != null)
			{
				claxx = new com.db4o.reflect.ReflectClass[clazz.Length];
				for (int i = 0; i < clazz.Length; i++)
				{
					if (clazz[i] != null)
					{
						claxx[i] = reflector.forClass(clazz[i]);
					}
				}
			}
			return claxx;
		}

		public virtual int collectionUpdateDepth(com.db4o.reflect.ReflectClass candidate)
		{
            if(typeof(System.Collections.IDictionary).
                IsAssignableFrom(((com.db4o.reflect.net.NetClass)candidate).getNetType())){
                return 3;
            };

			com.db4o.Iterator4 i = _collectionUpdateDepths.iterator();
			while (i.hasNext())
			{
				object[] entry = (object[])i.next();
				com.db4o.reflect.ReflectClass claxx = (com.db4o.reflect.ReflectClass)entry[0];
				if (claxx.isAssignableFrom(candidate))
				{
					return ((int)entry[1]);
				}
			}
			return 2;
		}
	}
}
