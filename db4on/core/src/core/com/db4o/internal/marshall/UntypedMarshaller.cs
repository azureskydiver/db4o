namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public abstract class UntypedMarshaller
	{
		internal com.db4o.@internal.marshall.MarshallerFamily _family;

		public abstract void DeleteEmbedded(com.db4o.@internal.StatefulBuffer reader);

		public abstract object WriteNew(object obj, bool restoreLinkOffset, com.db4o.@internal.StatefulBuffer
			 writer);

		public abstract object Read(com.db4o.@internal.StatefulBuffer reader);

		public abstract com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.Buffer[] a_bytes);

		public abstract bool UseNormalClassRead();

		public abstract object ReadQuery(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 reader, bool toArray);

		public abstract com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.Buffer
			 reader, com.db4o.@internal.query.processor.QCandidates candidates, bool withIndirection
			);

		public abstract void Defrag(com.db4o.@internal.ReaderPair readers);
	}
}
