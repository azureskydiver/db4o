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
	public class GenericReflector : com.db4o.reflect.Reflector, com.db4o.DeepClone
	{
		private com.db4o.reflect.Reflector _delegate;

		private com.db4o.reflect.generic.GenericArrayReflector _array;

		private readonly com.db4o.Hashtable4 _classByName = new com.db4o.Hashtable4(1);

		private readonly com.db4o.Collection4 _classes = new com.db4o.Collection4();

		private readonly com.db4o.Hashtable4 _classByID = new com.db4o.Hashtable4(1);

		private com.db4o.Collection4 _collectionClasses = new com.db4o.Collection4();

		private com.db4o.Collection4 _collectionUpdateDepths = new com.db4o.Collection4();

		private com.db4o.Transaction _trans;

		private com.db4o.YapStream _stream;

		public GenericReflector(com.db4o.Transaction trans, com.db4o.reflect.Reflector delegateReflector
			)
		{
			setTransaction(trans);
			_delegate = delegateReflector;
			if (_delegate != null)
			{
				_delegate.setParent(this);
			}
		}

		public virtual object deepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector myClone = new com.db4o.reflect.generic.GenericReflector
				(null, (com.db4o.reflect.Reflector)_delegate.deepClone(this));
			myClone._collectionClasses = (com.db4o.Collection4)_collectionClasses.deepClone(myClone
				);
			myClone._collectionUpdateDepths = (com.db4o.Collection4)_collectionUpdateDepths.deepClone
				(myClone);
			return myClone;
		}

		internal virtual com.db4o.YapStream getStream()
		{
			return _stream;
		}

		public virtual bool hasTransaction()
		{
			return _trans != null;
		}

		public virtual void setTransaction(com.db4o.Transaction trans)
		{
			if (trans != null)
			{
				_trans = trans;
				_stream = trans.i_stream;
			}
		}

		public virtual com.db4o.reflect.ReflectArray array()
		{
			if (_array == null)
			{
				_array = new com.db4o.reflect.generic.GenericArrayReflector(this);
			}
			return _array;
		}

		public virtual int collectionUpdateDepth(com.db4o.reflect.ReflectClass candidate)
		{
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

		public virtual bool constructorCallsSupported()
		{
			return _delegate.constructorCallsSupported();
		}

		internal virtual com.db4o.reflect.generic.GenericClass ensureDelegate(com.db4o.reflect.ReflectClass
			 clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass claxx = (com.db4o.reflect.generic.GenericClass
				)_classByName.get(clazz.getName());
			if (claxx == null)
			{
				string name = clazz.getName();
				claxx = new com.db4o.reflect.generic.GenericClass(this, clazz, name, null);
				_classes.add(claxx);
				_classByName.put(name, claxx);
			}
			return claxx;
		}

		public virtual com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass claxx = forName(clazz.getName());
			if (claxx != null)
			{
				return claxx;
			}
			claxx = _delegate.forClass(clazz);
			return ensureDelegate(claxx);
		}

		public virtual com.db4o.reflect.ReflectClass forName(string className)
		{
			com.db4o.reflect.ReflectClass clazz = (com.db4o.reflect.ReflectClass)_classByName
				.get(className);
			if (clazz != null)
			{
				return clazz;
			}
			clazz = _delegate.forName(className);
			if (clazz != null)
			{
				return ensureDelegate(clazz);
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectClass forObject(object obj)
		{
			if (obj is com.db4o.reflect.generic.GenericObject)
			{
				return ((com.db4o.reflect.generic.GenericObject)obj)._class;
			}
			com.db4o.reflect.ReflectClass clazz = _delegate.forObject(obj);
			if (clazz != null)
			{
				return ensureDelegate(clazz);
			}
			return null;
		}

		public virtual com.db4o.reflect.Reflector getDelegate()
		{
			return _delegate;
		}

		public virtual bool isCollection(com.db4o.reflect.ReflectClass candidate)
		{
			candidate = candidate.getDelegate();
			com.db4o.Iterator4 i = _collectionClasses.iterator();
			while (i.hasNext())
			{
				com.db4o.reflect.ReflectClass claxx = ((com.db4o.reflect.ReflectClass)i.next()).getDelegate
					();
				if (claxx.isAssignableFrom(candidate))
				{
					return true;
				}
			}
			return _delegate.isCollection(candidate);
		}

		public virtual void registerCollection(j4o.lang.Class clazz)
		{
			_collectionClasses.add(forClass(clazz));
		}

		public virtual void registerCollectionUpdateDepth(j4o.lang.Class clazz, int depth
			)
		{
			object[] entry = new object[] { forClass(clazz), System.Convert.ToInt32(depth) };
			_collectionUpdateDepths.add(entry);
		}

		public virtual void register(com.db4o.reflect.generic.GenericClass clazz)
		{
			string name = clazz.getName();
			if (_classByName.get(name) == null)
			{
				_classByName.put(name, clazz);
				_classes.add(clazz);
			}
		}

		public virtual com.db4o.reflect.ReflectClass[] knownClasses()
		{
			readAll();
			com.db4o.Collection4 classes = new com.db4o.Collection4();
			com.db4o.Iterator4 i = _classes.iterator();
			while (i.hasNext())
			{
				com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
					)i.next();
				if (!_stream.i_handlers.ICLASS_INTERNAL.isAssignableFrom(clazz))
				{
					if (!clazz.isSecondClass())
					{
						if (!clazz.isArray())
						{
							classes.add(clazz);
						}
					}
				}
			}
			com.db4o.reflect.ReflectClass[] ret = new com.db4o.reflect.ReflectClass[classes.size
				()];
			int j = 0;
			i = classes.iterator();
			while (i.hasNext())
			{
				ret[j++] = (com.db4o.reflect.ReflectClass)i.next();
			}
			return ret;
		}

		private void readAll()
		{
			int classCollectionID = _stream.i_classCollection.getID();
			com.db4o.YapWriter classcollreader = _stream.readWriterByID(_trans, classCollectionID
				);
			int numclasses = classcollreader.readInt();
			int[] classIDs = new int[numclasses];
			for (int classidx = 0; classidx < numclasses; classidx++)
			{
				classIDs[classidx] = classcollreader.readInt();
				ensureClassAvailability(classIDs[classidx]);
			}
			for (int classidx = 0; classidx < numclasses; classidx++)
			{
				ensureClassRead(classIDs[classidx]);
			}
		}

		private com.db4o.reflect.generic.GenericClass ensureClassAvailability(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass ret = (com.db4o.reflect.generic.GenericClass
				)_classByID.get(id);
			if (ret != null)
			{
				return ret;
			}
			com.db4o.YapWriter classreader = _stream.readWriterByID(_trans, id);
			int namelength = classreader.readInt();
			string classname = _stream.stringIO().read(classreader, namelength);
			ret = (com.db4o.reflect.generic.GenericClass)_classByName.get(classname);
			if (ret != null)
			{
				_classByID.put(id, ret);
				return ret;
			}
			classreader.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			int ancestorid = classreader.readInt();
			com.db4o.reflect.ReflectClass nativeClass = _delegate.forName(classname);
			ret = new com.db4o.reflect.generic.GenericClass(this, nativeClass, classname, ensureClassAvailability
				(ancestorid));
			_classByID.put(id, ret);
			return ret;
		}

		private void ensureClassRead(int id)
		{
			com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
				)_classByID.get(id);
			com.db4o.YapWriter classreader = _stream.readWriterByID(_trans, id);
			int namelength = classreader.readInt();
			string classname = _stream.stringIO().read(classreader, namelength);
			if (_classByName.get(classname) != null)
			{
				return;
			}
			_classByName.put(classname, clazz);
			_classes.add(clazz);
			classreader.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH * 3);
			int numfields = classreader.readInt();
			com.db4o.reflect.generic.GenericField[] fields = new com.db4o.reflect.generic.GenericField
				[numfields];
			for (int i = 0; i < numfields; i++)
			{
				string fieldname = null;
				int fieldnamelength = classreader.readInt();
				fieldname = _stream.stringIO().read(classreader, fieldnamelength);
				if (fieldname.IndexOf(com.db4o.YapConst.VIRTUAL_FIELD_PREFIX) == 0)
				{
					fields[i] = new com.db4o.reflect.generic.GenericVirtualField(fieldname);
				}
				else
				{
					com.db4o.reflect.generic.GenericClass fieldClass = null;
					int handlerid = classreader.readInt();
					switch (handlerid)
					{
						case com.db4o.YapHandlers.ANY_ID:
						{
							fieldClass = (com.db4o.reflect.generic.GenericClass)forClass(j4o.lang.Class.getClassForType
								(typeof(object)));
							break;
						}

						case com.db4o.YapHandlers.ANY_ARRAY_ID:
						{
							fieldClass = ((com.db4o.reflect.generic.GenericClass)forClass(j4o.lang.Class.getClassForType
								(typeof(object)))).arrayClass();
							break;
						}

						default:
						{
							fieldClass = (com.db4o.reflect.generic.GenericClass)_classByID.get(handlerid);
							break;
						}
					}
					com.db4o.YapBit attribs = new com.db4o.YapBit(classreader.readByte());
					bool isprimitive = attribs.get();
					bool isarray = attribs.get();
					bool ismultidimensional = attribs.get();
					fields[i] = new com.db4o.reflect.generic.GenericField(fieldname, fieldClass, isprimitive
						, isarray, ismultidimensional);
				}
			}
			clazz.initFields(fields);
		}

		public virtual void registerPrimitiveClass(int id, string name)
		{
			com.db4o.reflect.generic.GenericClass existing = (com.db4o.reflect.generic.GenericClass
				)_classByID.get(id);
			if (existing != null)
			{
				existing.setSecondClass();
				return;
			}
			com.db4o.reflect.ReflectClass clazz = _delegate.forName(name);
			com.db4o.reflect.generic.GenericClass claxx = ensureDelegate(clazz);
			claxx.setSecondClass();
			_classByID.put(id, claxx);
		}

		public virtual void setParent(com.db4o.reflect.Reflector reflector)
		{
		}
	}
}
