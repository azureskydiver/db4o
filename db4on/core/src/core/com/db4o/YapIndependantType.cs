namespace com.db4o
{
	/// <summary>
	/// Common base class for YapString and YapArray:
	/// There is one indirection in the database file to this.
	/// </summary>
	/// <remarks>
	/// Common base class for YapString and YapArray:
	/// There is one indirection in the database file to this.
	/// </remarks>
	/// <exclude></exclude>
	public abstract class YapIndependantType : com.db4o.TypeHandler4
	{
		internal readonly com.db4o.YapStream _stream;

		public YapIndependantType(com.db4o.YapStream stream)
		{
			_stream = stream;
		}

		public virtual object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return CanHold(claxx) ? obj : com.db4o.foundation.No4.INSTANCE;
		}

		public void CopyValue(object a_from, object a_to)
		{
		}

		public abstract void DeleteEmbedded(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter a_bytes);

		public virtual bool HasFixedLength()
		{
			return false;
		}

		public int LinkLength()
		{
			return com.db4o.YapConst.INT_LENGTH + com.db4o.YapConst.ID_LENGTH;
		}

		public com.db4o.reflect.ReflectClass PrimitiveClassReflector()
		{
			return null;
		}

		public virtual bool ReadArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public virtual object ReadIndexEntry(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.YapWriter a_writer)
		{
			return Read(mf, a_writer, true);
		}

		public virtual bool WriteArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public abstract bool IsGreater(object obj);

		public abstract com.db4o.YapComparable PrepareComparison(object obj);

		public abstract int CompareTo(object obj);

		public abstract bool IsEqual(object obj);

		public abstract bool IsSmaller(object obj);

		public abstract object ComparableObject(com.db4o.Transaction trans, object indexEntry
			);

		public abstract object ReadIndexEntry(com.db4o.YapReader a_reader);

		public abstract void WriteIndexEntry(com.db4o.YapReader a_writer, object a_object
			);

		public void Defrag(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader
			 source, com.db4o.YapReader target, com.db4o.IDMapping mapping)
		{
			int linkLength = LinkLength();
			source._offset += linkLength;
			target._offset += linkLength;
		}

		public abstract object Current();

		public abstract bool CanHold(com.db4o.reflect.ReflectClass arg1);

		public abstract void CascadeActivation(com.db4o.Transaction arg1, object arg2, int
			 arg3, bool arg4);

		public abstract com.db4o.reflect.ReflectClass ClassReflector();

		public abstract int GetID();

		public abstract bool Equals(com.db4o.TypeHandler4 arg1);

		public abstract bool IndexNullHandling();

		public abstract int IsSecondClass();

		public abstract void CalculateLengths(com.db4o.Transaction arg1, com.db4o.inside.marshall.ObjectHeaderAttributes
			 arg2, bool arg3, object arg4, bool arg5);

		public abstract object IndexEntryToObject(com.db4o.Transaction arg1, object arg2);

		public abstract void PrepareComparison(com.db4o.Transaction arg1, object arg2);

		public abstract object Read(com.db4o.inside.marshall.MarshallerFamily arg1, com.db4o.YapWriter
			 arg2, bool arg3);

		public abstract object ReadQuery(com.db4o.Transaction arg1, com.db4o.inside.marshall.MarshallerFamily
			 arg2, bool arg3, com.db4o.YapReader arg4, bool arg5);

		public abstract bool SupportsIndex();

		public abstract object WriteNew(com.db4o.inside.marshall.MarshallerFamily arg1, object
			 arg2, bool arg3, com.db4o.YapWriter arg4, bool arg5, bool arg6);

		public abstract int GetTypeID();

		public abstract com.db4o.YapClass GetYapClass(com.db4o.YapStream arg1);

		public abstract void ReadCandidates(com.db4o.inside.marshall.MarshallerFamily arg1
			, com.db4o.YapReader arg2, com.db4o.QCandidates arg3);

		public abstract com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction arg1, 
			com.db4o.inside.marshall.MarshallerFamily arg2, com.db4o.YapReader[] arg3);

		public abstract com.db4o.QCandidate ReadSubCandidate(com.db4o.inside.marshall.MarshallerFamily
			 arg1, com.db4o.YapReader arg2, com.db4o.QCandidates arg3, bool arg4);
	}
}
