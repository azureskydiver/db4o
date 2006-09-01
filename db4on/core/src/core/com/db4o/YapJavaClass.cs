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

		public virtual bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return claxx.Equals(ClassReflector());
		}

		public virtual void CascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
		}

		public virtual object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return CanHold(claxx) ? obj : com.db4o.foundation.No4.INSTANCE;
		}

		public virtual object ComparableObject(com.db4o.Transaction a_trans, object a_object
			)
		{
			return a_object;
		}

		public virtual void CopyValue(object a_from, object a_to)
		{
		}

		public abstract object DefaultValue();

		public virtual void DeleteEmbedded(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter a_bytes)
		{
			a_bytes.IncrementOffset(LinkLength());
		}

		public virtual bool Equals(com.db4o.TypeHandler4 a_dataType)
		{
			return (this == a_dataType);
		}

		public virtual int GetTypeID()
		{
			return com.db4o.YapConst.TYPE_SIMPLE;
		}

		public virtual com.db4o.YapClass GetYapClass(com.db4o.YapStream a_stream)
		{
			return a_stream.i_handlers.i_yapClasses[GetID() - 1];
		}

		public virtual bool HasFixedLength()
		{
			return true;
		}

		public virtual object IndexEntryToObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			return indexEntry;
		}

		public virtual bool IndexNullHandling()
		{
			return false;
		}

		public virtual int IsSecondClass()
		{
			return com.db4o.YapConst.YES;
		}

		public virtual void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			if (topLevel)
			{
				header.AddBaseLength(LinkLength());
			}
			else
			{
				header.AddPayLoadLength(LinkLength());
			}
		}

		public virtual void PrepareComparison(com.db4o.Transaction a_trans, object obj)
		{
			PrepareComparison(obj);
		}

		protected abstract j4o.lang.Class PrimitiveJavaClass();

		internal abstract object PrimitiveNull();

		public virtual bool ReadArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public virtual com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader[] a_bytes)
		{
			return null;
		}

		public virtual object ReadQuery(com.db4o.Transaction trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.YapReader reader, bool toArray)
		{
			return Read1(reader);
		}

		public virtual object Read(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 writer, bool redirect)
		{
			return Read1(writer);
		}

		internal abstract object Read1(com.db4o.YapReader reader);

		public virtual void ReadCandidates(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapReader a_bytes, com.db4o.QCandidates a_candidates)
		{
		}

		public virtual com.db4o.QCandidate ReadSubCandidate(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader reader, com.db4o.QCandidates candidates, bool withIndirection
			)
		{
			try
			{
				object obj = ReadQuery(candidates.i_trans, mf, withIndirection, reader, true);
				if (obj != null)
				{
					return new com.db4o.QCandidate(candidates, obj, 0, true);
				}
			}
			catch (com.db4o.CorruptionException e)
			{
			}
			return null;
		}

		public virtual object ReadIndexEntry(com.db4o.YapReader a_reader)
		{
			try
			{
				return Read1(a_reader);
			}
			catch (com.db4o.CorruptionException e)
			{
			}
			return null;
		}

		public virtual object ReadIndexEntry(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.YapWriter a_writer)
		{
			return Read(mf, a_writer, true);
		}

		public virtual com.db4o.reflect.ReflectClass ClassReflector()
		{
			if (_classReflector != null)
			{
				return _classReflector;
			}
			_classReflector = _stream.Reflector().ForClass(j4o.lang.Class.GetClassForObject(DefaultValue
				()));
			j4o.lang.Class clazz = PrimitiveJavaClass();
			if (clazz != null)
			{
				_primitiveClassReflector = _stream.Reflector().ForClass(clazz);
			}
			return _classReflector;
		}

		/// <summary>classReflector() has to be called first, before this returns a value</summary>
		public virtual com.db4o.reflect.ReflectClass PrimitiveClassReflector()
		{
			return _primitiveClassReflector;
		}

		public virtual bool SupportsIndex()
		{
			return true;
		}

		public abstract void Write(object a_object, com.db4o.YapReader a_bytes);

		public virtual bool WriteArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public virtual void WriteIndexEntry(com.db4o.YapReader a_writer, object a_object)
		{
			if (a_object == null)
			{
				a_object = PrimitiveNull();
			}
			Write(a_object, a_writer);
		}

		public virtual object WriteNew(com.db4o.inside.marshall.MarshallerFamily mf, object
			 a_object, bool topLevel, com.db4o.YapWriter a_bytes, bool withIndirection, bool
			 restoreLinkeOffset)
		{
			if (a_object == null)
			{
				a_object = PrimitiveNull();
			}
			Write(a_object, a_bytes);
			return a_object;
		}

		public virtual com.db4o.YapComparable PrepareComparison(object obj)
		{
			if (obj == null)
			{
				i_compareToIsNull = true;
				return com.db4o.Null.INSTANCE;
			}
			i_compareToIsNull = false;
			PrepareComparison1(obj);
			return this;
		}

		public virtual object Current()
		{
			if (i_compareToIsNull)
			{
				return null;
			}
			return Current1();
		}

		internal abstract void PrepareComparison1(object obj);

		public abstract object Current1();

		public virtual int CompareTo(object obj)
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
			if (IsEqual1(obj))
			{
				return 0;
			}
			if (IsGreater1(obj))
			{
				return 1;
			}
			return -1;
		}

		public virtual bool IsEqual(object obj)
		{
			if (i_compareToIsNull)
			{
				return obj == null;
			}
			return IsEqual1(obj);
		}

		internal abstract bool IsEqual1(object obj);

		public virtual bool IsGreater(object obj)
		{
			if (i_compareToIsNull)
			{
				return obj != null;
			}
			return IsGreater1(obj);
		}

		internal abstract bool IsGreater1(object obj);

		public virtual bool IsSmaller(object obj)
		{
			if (i_compareToIsNull)
			{
				return false;
			}
			return IsSmaller1(obj);
		}

		internal abstract bool IsSmaller1(object obj);

		public abstract int LinkLength();

		public void Defrag(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader
			 source, com.db4o.YapReader target, com.db4o.IDMapping mapping)
		{
			int linkLength = LinkLength();
			source.IncrementOffset(linkLength);
			target.IncrementOffset(linkLength);
		}

		public abstract int GetID();
	}
}
