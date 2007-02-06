namespace com.db4o.@internal.handlers
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
	public abstract class BuiltinTypeHandler : com.db4o.@internal.TypeHandler4
	{
		internal readonly com.db4o.@internal.ObjectContainerBase _stream;

		public BuiltinTypeHandler(com.db4o.@internal.ObjectContainerBase stream)
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

		public abstract void DeleteEmbedded(com.db4o.@internal.marshall.MarshallerFamily 
			mf, com.db4o.@internal.StatefulBuffer a_bytes);

		public virtual bool HasFixedLength()
		{
			return false;
		}

		public int LinkLength()
		{
			return com.db4o.@internal.Const4.INT_LENGTH + com.db4o.@internal.Const4.ID_LENGTH;
		}

		public virtual com.db4o.reflect.ReflectClass PrimitiveClassReflector()
		{
			return null;
		}

		public virtual bool ReadArray(object array, com.db4o.@internal.Buffer reader)
		{
			return false;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.StatefulBuffer a_writer)
		{
			return Read(mf, a_writer, true);
		}

		public virtual bool WriteArray(object array, com.db4o.@internal.Buffer reader)
		{
			return false;
		}

		public abstract bool IsGreater(object obj);

		public abstract com.db4o.@internal.Comparable4 PrepareComparison(object obj);

		public abstract int CompareTo(object obj);

		public abstract bool IsEqual(object obj);

		public abstract bool IsSmaller(object obj);

		public abstract object ComparableObject(com.db4o.@internal.Transaction trans, object
			 indexEntry);

		public abstract object ReadIndexEntry(com.db4o.@internal.Buffer a_reader);

		public abstract void WriteIndexEntry(com.db4o.@internal.Buffer a_writer, object a_object
			);

		public abstract void Defrag(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.ReaderPair
			 readers, bool redirect);

		public abstract object Current();

		public abstract void DefragIndexEntry(com.db4o.@internal.ReaderPair arg1);

		public abstract void CalculateLengths(com.db4o.@internal.Transaction arg1, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 arg2, bool arg3, object arg4, bool arg5);

		public abstract bool CanHold(com.db4o.reflect.ReflectClass arg1);

		public abstract void CascadeActivation(com.db4o.@internal.Transaction arg1, object
			 arg2, int arg3, bool arg4);

		public abstract com.db4o.reflect.ReflectClass ClassReflector();

		public abstract bool Equals(com.db4o.@internal.TypeHandler4 arg1);

		public abstract int GetID();

		public abstract int GetTypeID();

		public abstract com.db4o.@internal.ClassMetadata GetYapClass(com.db4o.@internal.ObjectContainerBase
			 arg1);

		public abstract object IndexEntryToObject(com.db4o.@internal.Transaction arg1, object
			 arg2);

		public abstract bool IndexNullHandling();

		public abstract int IsSecondClass();

		public abstract void PrepareComparison(com.db4o.@internal.Transaction arg1, object
			 arg2);

		public abstract object Read(com.db4o.@internal.marshall.MarshallerFamily arg1, com.db4o.@internal.StatefulBuffer
			 arg2, bool arg3);

		public abstract com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 arg1, com.db4o.@internal.marshall.MarshallerFamily arg2, com.db4o.@internal.Buffer[]
			 arg3);

		public abstract void ReadCandidates(com.db4o.@internal.marshall.MarshallerFamily 
			arg1, com.db4o.@internal.Buffer arg2, com.db4o.@internal.query.processor.QCandidates
			 arg3);

		public abstract object ReadQuery(com.db4o.@internal.Transaction arg1, com.db4o.@internal.marshall.MarshallerFamily
			 arg2, bool arg3, com.db4o.@internal.Buffer arg4, bool arg5);

		public abstract com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.marshall.MarshallerFamily
			 arg1, com.db4o.@internal.Buffer arg2, com.db4o.@internal.query.processor.QCandidates
			 arg3, bool arg4);

		public abstract bool SupportsIndex();

		public abstract object WriteNew(com.db4o.@internal.marshall.MarshallerFamily arg1
			, object arg2, bool arg3, com.db4o.@internal.StatefulBuffer arg4, bool arg5, bool
			 arg6);
	}
}
