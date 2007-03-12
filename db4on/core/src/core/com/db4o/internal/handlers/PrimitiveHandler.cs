namespace com.db4o.@internal.handlers
{
	/// <exclude></exclude>
	public abstract class PrimitiveHandler : com.db4o.@internal.TypeHandler4
	{
		protected readonly com.db4o.@internal.ObjectContainerBase _stream;

		protected com.db4o.reflect.ReflectClass _classReflector;

		private com.db4o.reflect.ReflectClass _primitiveClassReflector;

		public PrimitiveHandler(com.db4o.@internal.ObjectContainerBase stream)
		{
			_stream = stream;
		}

		private bool i_compareToIsNull;

		public virtual bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return claxx.Equals(ClassReflector());
		}

		public virtual void CascadeActivation(com.db4o.@internal.Transaction a_trans, object
			 a_object, int a_depth, bool a_activate)
		{
		}

		public virtual object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return CanHold(claxx) ? obj : com.db4o.foundation.No4.INSTANCE;
		}

		public virtual object ComparableObject(com.db4o.@internal.Transaction a_trans, object
			 a_object)
		{
			return a_object;
		}

		public virtual void CopyValue(object a_from, object a_to)
		{
		}

		public abstract object DefaultValue();

		public virtual void DeleteEmbedded(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			a_bytes.IncrementOffset(LinkLength());
		}

		public virtual bool Equals(com.db4o.@internal.TypeHandler4 a_dataType)
		{
			return (this == a_dataType);
		}

		public virtual int GetTypeID()
		{
			return com.db4o.@internal.Const4.TYPE_SIMPLE;
		}

		public virtual com.db4o.@internal.ClassMetadata GetYapClass(com.db4o.@internal.ObjectContainerBase
			 a_stream)
		{
			return a_stream.i_handlers.PrimitiveClassById(GetID());
		}

		public virtual bool HasFixedLength()
		{
			return true;
		}

		public virtual object IndexEntryToObject(com.db4o.@internal.Transaction trans, object
			 indexEntry)
		{
			return indexEntry;
		}

		public virtual bool IndexNullHandling()
		{
			return false;
		}

		public virtual com.db4o.foundation.TernaryBool IsSecondClass()
		{
			return com.db4o.foundation.TernaryBool.YES;
		}

		public virtual void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
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

		public virtual void PrepareComparison(com.db4o.@internal.Transaction a_trans, object
			 obj)
		{
			PrepareComparison(obj);
		}

		protected abstract j4o.lang.Class PrimitiveJavaClass();

		public abstract object PrimitiveNull();

		public virtual bool ReadArray(object array, com.db4o.@internal.Buffer reader)
		{
			return false;
		}

		public virtual com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.Buffer[]
			 a_bytes)
		{
			return null;
		}

		public virtual object ReadQuery(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.@internal.Buffer reader, bool toArray)
		{
			return Read1(reader);
		}

		public virtual object Read(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 writer, bool redirect)
		{
			return Read1(writer);
		}

		internal abstract object Read1(com.db4o.@internal.Buffer reader);

		public virtual void ReadCandidates(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.Buffer a_bytes, com.db4o.@internal.query.processor.QCandidates
			 a_candidates)
		{
		}

		public virtual com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates, bool withIndirection)
		{
			try
			{
				object obj = ReadQuery(candidates.i_trans, mf, withIndirection, reader, true);
				if (obj != null)
				{
					return new com.db4o.@internal.query.processor.QCandidate(candidates, obj, 0, true
						);
				}
			}
			catch (com.db4o.CorruptionException)
			{
			}
			return null;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.Buffer a_reader)
		{
			try
			{
				return Read1(a_reader);
			}
			catch (com.db4o.CorruptionException)
			{
			}
			return null;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.StatefulBuffer a_writer)
		{
			return Read(mf, a_writer, true);
		}

		public virtual com.db4o.reflect.ReflectClass ClassReflector()
		{
			if (_classReflector != null)
			{
				return _classReflector;
			}
			_classReflector = _stream.Reflector().ForClass(j4o.lang.JavaSystem.GetClassForObject
				(DefaultValue()));
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

		public abstract void Write(object a_object, com.db4o.@internal.Buffer a_bytes);

		public virtual bool WriteArray(object array, com.db4o.@internal.Buffer reader)
		{
			return false;
		}

		public virtual void WriteIndexEntry(com.db4o.@internal.Buffer a_writer, object a_object
			)
		{
			if (a_object == null)
			{
				a_object = PrimitiveNull();
			}
			Write(a_object, a_writer);
		}

		public virtual object WriteNew(com.db4o.@internal.marshall.MarshallerFamily mf, object
			 a_object, bool topLevel, com.db4o.@internal.StatefulBuffer a_bytes, bool withIndirection
			, bool restoreLinkeOffset)
		{
			if (a_object == null)
			{
				a_object = PrimitiveNull();
			}
			Write(a_object, a_bytes);
			return a_object;
		}

		public virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			if (obj == null)
			{
				i_compareToIsNull = true;
				return com.db4o.@internal.Null.INSTANCE;
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

		public void Defrag(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.ReaderPair
			 readers, bool redirect)
		{
			int linkLength = LinkLength();
			readers.IncrementOffset(linkLength);
		}

		public virtual void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
			try
			{
				Read1(readers.Source());
				Read1(readers.Target());
			}
			catch (com.db4o.CorruptionException)
			{
				com.db4o.@internal.Exceptions4.VirtualException();
			}
		}

		public abstract int GetID();
	}
}
