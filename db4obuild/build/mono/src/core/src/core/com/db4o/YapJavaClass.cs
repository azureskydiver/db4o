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
	internal abstract class YapJavaClass : com.db4o.YapDataType
	{
		internal readonly com.db4o.YapStream _stream;

		private com.db4o.reflect.ReflectClass _classReflector;

		private com.db4o.reflect.ReflectClass _primitiveClassReflector;

		public YapJavaClass(com.db4o.YapStream stream)
		{
			_stream = stream;
		}

		private bool i_compareToIsNull;

		public virtual void appendEmbedded3(com.db4o.YapWriter a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		public virtual bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			return claxx == classReflector();
		}

		public virtual void cascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
		}

		public virtual void copyValue(object a_from, object a_to)
		{
		}

		public abstract object defaultValue();

		public virtual void deleteEmbedded(com.db4o.YapWriter a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		public virtual bool equals(com.db4o.YapDataType a_dataType)
		{
			return (this == a_dataType);
		}

		public virtual int getType()
		{
			return com.db4o.YapConst.TYPE_SIMPLE;
		}

		public virtual com.db4o.YapClass getYapClass(com.db4o.YapStream a_stream)
		{
			return a_stream.i_handlers.i_yapClasses[getID() - 1];
		}

		public virtual object indexEntry(object a_object)
		{
			return a_object;
		}

		public virtual object indexObject(com.db4o.Transaction a_trans, object a_object)
		{
			return a_object;
		}

		public virtual void prepareLastIoComparison(com.db4o.Transaction a_trans, object 
			obj)
		{
			prepareComparison(obj);
		}

		protected abstract j4o.lang.Class primitiveJavaClass();

		internal abstract object primitiveNull();

		public virtual com.db4o.YapDataType readArrayWrapper(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			return null;
		}

		public virtual object readQuery(com.db4o.Transaction trans, com.db4o.YapReader reader
			, bool toArray)
		{
			return read1(reader);
		}

		public virtual object read(com.db4o.YapWriter writer)
		{
			return read1(writer);
		}

		internal abstract object read1(com.db4o.YapReader reader);

		public virtual void readCandidates(com.db4o.YapReader a_bytes, com.db4o.QCandidates
			 a_candidates)
		{
		}

		public virtual object readIndexEntry(com.db4o.YapReader a_reader)
		{
			try
			{
				return read1(a_reader);
			}
			catch (com.db4o.CorruptionException e)
			{
			}
			return null;
		}

		public virtual object readIndexObject(com.db4o.YapWriter a_writer)
		{
			return read(a_writer);
		}

		public virtual com.db4o.reflect.ReflectClass classReflector()
		{
			if (_classReflector == null)
			{
				_classReflector = _stream.reflector().forClass(j4o.lang.Class.getClassForObject(defaultValue
					()));
				j4o.lang.Class clazz = primitiveJavaClass();
				if (clazz != null)
				{
					_primitiveClassReflector = _stream.reflector().forClass(clazz);
				}
			}
			return _classReflector;
		}

		/// <summary>classReflector() has to be called first, before this returns a value</summary>
		public virtual com.db4o.reflect.ReflectClass primitiveClassReflector()
		{
			return _primitiveClassReflector;
		}

		public virtual bool supportsIndex()
		{
			return true;
		}

		public abstract void write(object a_object, com.db4o.YapWriter a_bytes);

		public virtual void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object)
		{
			write(a_object, a_writer);
		}

		public virtual void writeNew(object a_object, com.db4o.YapWriter a_bytes)
		{
			write(a_object, a_bytes);
		}

		public virtual com.db4o.YapComparable prepareComparison(object obj)
		{
			if (obj == null)
			{
				i_compareToIsNull = true;
				return com.db4o.Null.INSTANCE;
			}
			i_compareToIsNull = false;
			prepareComparison1(obj);
			return this;
		}

		internal abstract void prepareComparison1(object obj);

		public virtual int compareTo(object obj)
		{
			if (i_compareToIsNull)
			{
				if (obj == null)
				{
					return 0;
				}
				return 1;
			}
			if (obj == null)
			{
				return -1;
			}
			if (isEqual1(obj))
			{
				return 0;
			}
			if (isGreater1(obj))
			{
				return 1;
			}
			return -1;
		}

		public virtual bool isEqual(object obj)
		{
			if (i_compareToIsNull)
			{
				return obj == null;
			}
			return isEqual1(obj);
		}

		internal abstract bool isEqual1(object obj);

		public virtual bool isGreater(object obj)
		{
			if (i_compareToIsNull)
			{
				return obj != null;
			}
			return isGreater1(obj);
		}

		internal abstract bool isGreater1(object obj);

		public virtual bool isSmaller(object obj)
		{
			if (i_compareToIsNull)
			{
				return false;
			}
			return isSmaller1(obj);
		}

		internal abstract bool isSmaller1(object obj);

		public abstract int getID();

		public abstract int linkLength();
	}
}
