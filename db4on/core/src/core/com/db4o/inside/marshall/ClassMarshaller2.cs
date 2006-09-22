namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ClassMarshaller2 : com.db4o.inside.marshall.ClassMarshaller
	{
		protected override void ReadIndex(com.db4o.YapStream stream, com.db4o.YapClass clazz
			, com.db4o.YapReader reader)
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
