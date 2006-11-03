namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public interface FieldMarshaller
	{
		void Write(com.db4o.Transaction trans, com.db4o.YapClass clazz, com.db4o.YapField
			 field, com.db4o.YapReader writer);

		com.db4o.inside.marshall.RawFieldSpec ReadSpec(com.db4o.YapStream stream, com.db4o.YapReader
			 reader);

		com.db4o.YapField Read(com.db4o.YapStream stream, com.db4o.YapField field, com.db4o.YapReader
			 reader);

		int MarshalledLength(com.db4o.YapStream stream, com.db4o.YapField field);

		void Defrag(com.db4o.YapClass yapClass, com.db4o.YapField yapField, com.db4o.YapStringIO
			 sio, com.db4o.ReaderPair readers);
	}
}
