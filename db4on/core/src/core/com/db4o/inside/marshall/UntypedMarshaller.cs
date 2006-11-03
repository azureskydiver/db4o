namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public abstract class UntypedMarshaller
	{
		internal com.db4o.inside.marshall.MarshallerFamily _family;

		public abstract void DeleteEmbedded(com.db4o.YapWriter reader);

		public abstract object WriteNew(object obj, bool restoreLinkOffset, com.db4o.YapWriter
			 writer);

		public abstract object Read(com.db4o.YapWriter reader);

		public abstract com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes);

		public abstract bool UseNormalClassRead();

		public abstract object ReadQuery(com.db4o.Transaction trans, com.db4o.YapReader reader
			, bool toArray);

		public abstract com.db4o.QCandidate ReadSubCandidate(com.db4o.YapReader reader, com.db4o.QCandidates
			 candidates, bool withIndirection);

		public abstract void Defrag(com.db4o.ReaderPair readers);
	}
}
