namespace com.db4o.inside.mapping
{
	public class MappedIDPairHandler : com.db4o.inside.ix.Indexable4
	{
		private readonly com.db4o.YInt _origHandler;

		private readonly com.db4o.YInt _mappedHandler;

		private readonly com.db4o.YBoolean _seenHandler;

		public MappedIDPairHandler(com.db4o.YapStream stream)
		{
			_origHandler = new com.db4o.YInt(stream);
			_mappedHandler = new com.db4o.YInt(stream);
			_seenHandler = new com.db4o.YBoolean(stream);
		}

		public virtual object ComparableObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			throw new System.NotImplementedException();
		}

		public virtual void DefragIndexEntry(com.db4o.ReaderPair readers)
		{
			throw new System.NotImplementedException();
		}

		public virtual int LinkLength()
		{
			return _origHandler.LinkLength() + _mappedHandler.LinkLength() + _seenHandler.LinkLength
				();
		}

		public virtual object ReadIndexEntry(com.db4o.YapReader reader)
		{
			int origID = ReadID(reader);
			int mappedID = ReadID(reader);
			bool seen = ReadSeen(reader);
			return new com.db4o.inside.mapping.MappedIDPair(origID, mappedID, seen);
		}

		public virtual void WriteIndexEntry(com.db4o.YapReader reader, object obj)
		{
			com.db4o.inside.mapping.MappedIDPair mappedIDs = (com.db4o.inside.mapping.MappedIDPair
				)obj;
			_origHandler.WriteIndexEntry(reader, mappedIDs.Orig());
			_mappedHandler.WriteIndexEntry(reader, mappedIDs.Mapped());
			_seenHandler.WriteIndexEntry(reader, (mappedIDs.Seen() ? true : false));
		}

		public virtual int CompareTo(object obj)
		{
			if (null == obj)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.inside.mapping.MappedIDPair mappedIDs = (com.db4o.inside.mapping.MappedIDPair
				)obj;
			int result = _origHandler.CompareTo(mappedIDs.Orig());
			return result;
		}

		public virtual object Current()
		{
			return new com.db4o.inside.mapping.MappedIDPair(_origHandler.CurrentInt(), _mappedHandler
				.CurrentInt(), ((bool)_seenHandler.Current()));
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

		public virtual com.db4o.YapComparable PrepareComparison(object obj)
		{
			com.db4o.inside.mapping.MappedIDPair mappedIDs = (com.db4o.inside.mapping.MappedIDPair
				)obj;
			_origHandler.PrepareComparison(mappedIDs.Orig());
			_mappedHandler.PrepareComparison(mappedIDs.Mapped());
			_seenHandler.PrepareComparison((mappedIDs.Seen() ? true : false));
			return this;
		}

		private int ReadID(com.db4o.YapReader a_reader)
		{
			return ((int)_origHandler.ReadIndexEntry(a_reader));
		}

		private bool ReadSeen(com.db4o.YapReader a_reader)
		{
			return ((bool)_seenHandler.ReadIndexEntry(a_reader));
		}
	}
}
