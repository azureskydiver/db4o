namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public interface FieldMarshaller
	{
		void Write(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.FieldMetadata field, com.db4o.@internal.Buffer writer
			);

		com.db4o.@internal.marshall.RawFieldSpec ReadSpec(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.Buffer reader);

		com.db4o.@internal.FieldMetadata Read(com.db4o.@internal.ObjectContainerBase stream
			, com.db4o.@internal.FieldMetadata field, com.db4o.@internal.Buffer reader);

		int MarshalledLength(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.FieldMetadata
			 field);

		void Defrag(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.FieldMetadata
			 yapField, com.db4o.@internal.LatinStringIO sio, com.db4o.@internal.ReaderPair readers
			);
	}
}
