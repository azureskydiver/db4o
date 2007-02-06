namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class ClassMarshaller2 : com.db4o.@internal.marshall.ClassMarshaller
	{
		protected override void ReadIndex(com.db4o.@internal.ObjectContainerBase stream, 
			com.db4o.@internal.ClassMetadata clazz, com.db4o.@internal.Buffer reader)
		{
			int indexID = reader.ReadInt();
			clazz.Index().Read(stream, indexID);
		}

		protected override int IndexIDForWriting(int indexID)
		{
			return indexID;
		}
	}
}
