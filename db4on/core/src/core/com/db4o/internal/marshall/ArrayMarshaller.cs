namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public abstract class ArrayMarshaller
	{
		public com.db4o.@internal.marshall.MarshallerFamily _family;

		public abstract void DeleteEmbedded(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.StatefulBuffer reader);

		public com.db4o.@internal.TreeInt CollectIDs(com.db4o.@internal.handlers.ArrayHandler
			 arrayHandler, com.db4o.@internal.TreeInt tree, com.db4o.@internal.StatefulBuffer
			 reader)
		{
			com.db4o.@internal.Transaction trans = reader.GetTransaction();
			return arrayHandler.CollectIDs1(trans, tree, PrepareIDReader(trans, reader));
		}

		public abstract void DefragIDs(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.ReaderPair readers);

		public abstract void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, com.db4o.@internal.handlers.ArrayHandler handler, object obj, bool topLevel
			);

		public abstract object Read(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.StatefulBuffer reader);

		public abstract void ReadCandidates(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates);

		public abstract object ReadQuery(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer reader);

		public abstract object WriteNew(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, object obj, bool topLevel, com.db4o.@internal.StatefulBuffer writer);

		protected abstract com.db4o.@internal.Buffer PrepareIDReader(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.Buffer reader);
	}
}
