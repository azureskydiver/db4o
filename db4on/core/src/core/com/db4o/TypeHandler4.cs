namespace com.db4o
{
	/// <exclude></exclude>
	public interface TypeHandler4 : com.db4o.inside.ix.Indexable4
	{
		void appendEmbedded3(com.db4o.YapWriter a_bytes);

		bool canHold(com.db4o.reflect.ReflectClass claxx);

		void cascadeActivation(com.db4o.Transaction a_trans, object a_object, int a_depth
			, bool a_activate);

		com.db4o.reflect.ReflectClass classReflector();

		object coerce(com.db4o.reflect.ReflectClass claxx, object obj);

		void copyValue(object a_from, object a_to);

		void deleteEmbedded(com.db4o.YapWriter a_bytes);

		int getID();

		bool equals(com.db4o.TypeHandler4 a_dataType);

		object indexEntry(object a_object);

		bool indexNullHandling();

		void prepareLastIoComparison(com.db4o.Transaction a_trans, object obj);

		com.db4o.reflect.ReflectClass primitiveClassReflector();

		object read(com.db4o.YapWriter writer);

		object readIndexValueOrID(com.db4o.YapWriter writer);

		object readQuery(com.db4o.Transaction trans, com.db4o.YapReader reader, bool toArray
			);

		bool supportsIndex();

		int writeNew(object a_object, com.db4o.YapWriter a_bytes);

		int getType();

		com.db4o.YapClass getYapClass(com.db4o.YapStream a_stream);

		/// <summary>performance optimized read (only used for byte[] so far)</summary>
		bool readArray(object array, com.db4o.YapWriter reader);

		void readCandidates(com.db4o.YapReader a_bytes, com.db4o.QCandidates a_candidates
			);

		com.db4o.TypeHandler4 readArrayWrapper(com.db4o.Transaction a_trans, com.db4o.YapReader[]
			 a_bytes);

		/// <summary>performance optimized write (only used for byte[] so far)</summary>
		bool writeArray(object array, com.db4o.YapWriter reader);
	}
}
