namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public abstract class ArrayMarshaller
	{
		public com.db4o.inside.marshall.MarshallerFamily _family;

		public abstract void DeleteEmbedded(com.db4o.YapArray arrayHandler, com.db4o.YapWriter
			 reader);

		public abstract com.db4o.TreeInt CollectIDs(com.db4o.YapArray arrayHandler, com.db4o.TreeInt
			 tree, com.db4o.YapWriter a_bytes);

		public abstract void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, com.db4o.YapArray handler, object obj, bool topLevel);

		public abstract object Read(com.db4o.YapArray arrayHandler, com.db4o.YapWriter reader
			);

		public abstract void ReadCandidates(com.db4o.YapArray arrayHandler, com.db4o.YapReader
			 reader, com.db4o.QCandidates candidates);

		public abstract object ReadQuery(com.db4o.YapArray arrayHandler, com.db4o.Transaction
			 trans, com.db4o.YapReader reader);

		public abstract object WriteNew(com.db4o.YapArray arrayHandler, object obj, bool 
			topLevel, com.db4o.YapWriter writer);
	}
}
