namespace com.db4o.@internal.mapping
{
	/// <exclude></exclude>
	public class MappedIDPairHandler : com.db4o.@internal.ix.Indexable4
	{
		private readonly com.db4o.@internal.handlers.IntHandler _origHandler;

		private readonly com.db4o.@internal.handlers.IntHandler _mappedHandler;

		public MappedIDPairHandler(com.db4o.@internal.ObjectContainerBase stream)
		{
			_origHandler = new com.db4o.@internal.handlers.IntHandler(stream);
			_mappedHandler = new com.db4o.@internal.handlers.IntHandler(stream);
		}

		public virtual object ComparableObject(com.db4o.@internal.Transaction trans, object
			 indexEntry)
		{
			throw new System.NotImplementedException();
		}

		public virtual void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
			throw new System.NotImplementedException();
		}

		public virtual int LinkLength()
		{
			return _origHandler.LinkLength() + _mappedHandler.LinkLength();
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.Buffer reader)
		{
			int origID = ReadID(reader);
			int mappedID = ReadID(reader);
			return new com.db4o.@internal.mapping.MappedIDPair(origID, mappedID);
		}

		public virtual void WriteIndexEntry(com.db4o.@internal.Buffer reader, object obj)
		{
			com.db4o.@internal.mapping.MappedIDPair mappedIDs = (com.db4o.@internal.mapping.MappedIDPair
				)obj;
			_origHandler.WriteIndexEntry(reader, mappedIDs.Orig());
			_mappedHandler.WriteIndexEntry(reader, mappedIDs.Mapped());
		}

		public virtual int CompareTo(object obj)
		{
			return _origHandler.CompareTo(((com.db4o.@internal.mapping.MappedIDPair)obj).Orig
				());
		}

		public virtual object Current()
		{
			return new com.db4o.@internal.mapping.MappedIDPair(_origHandler.CurrentInt(), _mappedHandler
				.CurrentInt());
		}

		public virtual bool IsEqual(object obj)
		{
			throw new System.NotImplementedException();
		}

		public virtual bool IsGreater(object obj)
		{
			throw new System.NotImplementedException();
		}

		public virtual bool IsSmaller(object obj)
		{
			throw new System.NotImplementedException();
		}

		public virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			com.db4o.@internal.mapping.MappedIDPair mappedIDs = (com.db4o.@internal.mapping.MappedIDPair
				)obj;
			_origHandler.PrepareComparison(mappedIDs.Orig());
			_mappedHandler.PrepareComparison(mappedIDs.Mapped());
			return this;
		}

		private int ReadID(com.db4o.@internal.Buffer a_reader)
		{
			return ((int)_origHandler.ReadIndexEntry(a_reader));
		}
	}
}
