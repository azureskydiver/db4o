namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public interface TypeHandler4 : com.db4o.@internal.ix.Indexable4
	{
		bool CanHold(com.db4o.reflect.ReflectClass claxx);

		void CascadeActivation(com.db4o.@internal.Transaction a_trans, object a_object, int
			 a_depth, bool a_activate);

		com.db4o.reflect.ReflectClass ClassReflector();

		object Coerce(com.db4o.reflect.ReflectClass claxx, object obj);

		void CopyValue(object a_from, object a_to);

		void DeleteEmbedded(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes);

		int GetID();

		bool Equals(com.db4o.@internal.TypeHandler4 a_dataType);

		bool HasFixedLength();

		bool IndexNullHandling();

		int IsSecondClass();

		/// <summary>
		/// The length calculation is different, depending from where we
		/// calculate.
		/// </summary>
		/// <remarks>
		/// The length calculation is different, depending from where we
		/// calculate. If we are still in the link area at the beginning of
		/// the slot, no data needs to be written to the payload area for
		/// primitive types, since they fully fit into the link area. If
		/// we are already writing something like an array (or deeper) to
		/// the payload area when we come here, a primitive does require
		/// space in the payload area.
		/// Differentiation is expressed with the 'topLevel' parameter.
		/// If 'topLevel==true' we are asking for a size calculation for
		/// the link area. If 'topLevel==false' we are asking for a size
		/// calculation for the payload area at the end of the slot.
		/// </remarks>
		void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection);

		object IndexEntryToObject(com.db4o.@internal.Transaction trans, object indexEntry
			);

		void PrepareComparison(com.db4o.@internal.Transaction a_trans, object obj);

		com.db4o.reflect.ReflectClass PrimitiveClassReflector();

		object Read(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 writer, bool redirect);

		object ReadIndexEntry(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 writer);

		object ReadQuery(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.@internal.Buffer reader, bool toArray);

		bool SupportsIndex();

		object WriteNew(com.db4o.@internal.marshall.MarshallerFamily mf, object a_object, 
			bool topLevel, com.db4o.@internal.StatefulBuffer a_bytes, bool withIndirection, 
			bool restoreLinkOffset);

		int GetTypeID();

		com.db4o.@internal.ClassMetadata GetYapClass(com.db4o.@internal.ObjectContainerBase
			 a_stream);

		/// <summary>performance optimized read (only used for byte[] so far)</summary>
		bool ReadArray(object array, com.db4o.@internal.Buffer reader);

		void ReadCandidates(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.Buffer
			 reader, com.db4o.@internal.query.processor.QCandidates candidates);

		com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction a_trans
			, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.Buffer[] a_bytes
			);

		/// <summary>performance optimized write (only used for byte[] so far)</summary>
		bool WriteArray(object array, com.db4o.@internal.Buffer reader);

		com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates, bool withIndirection);

		void Defrag(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.ReaderPair
			 readers, bool redirect);
	}
}
