namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapJavaClass : com.db4o.TypeHandler4
	{
		protected readonly com.db4o.YapStream _stream;

		protected com.db4o.reflect.ReflectClass _classReflector;

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
			return claxx.Equals(classReflector());
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

		public virtual bool equals(com.db4o.TypeHandler4 a_dataType)
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

		public virtual bool indexNullHandling()
		{
			return false;
		}

		public virtual object comparableObject(com.db4o.Transaction a_trans, object a_object
			)
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

		public virtual bool readArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public virtual com.db4o.TypeHandler4 readArrayWrapper(com.db4o.Transaction a_trans
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

		public virtual object readIndexValueOrID(com.db4o.YapWriter a_writer)
		{
			return read(a_writer);
		}

		public virtual com.db4o.reflect.ReflectClass classReflector()
		{
			if (_classReflector != null)
			{
				return _classReflector;
			}
			_classReflector = _stream.reflector().forClass(j4o.lang.Class.getClassForObject(defaultValue
				()));
			j4o.lang.Class clazz = primitiveJavaClass();
			if (clazz != null)
			{
				_primitiveClassReflector = _stream.reflector().forClass(clazz);
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

		public virtual bool writeArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public virtual void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object)
		{
			write(a_object, a_writer);
		}

		public virtual int writeNew(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (a_object == null)
			{
				a_object = primitiveNull();
			}
			write(a_object, a_bytes);
			return -1;
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

		public abstract int linkLength();

		public abstract int getID();
	}
}
