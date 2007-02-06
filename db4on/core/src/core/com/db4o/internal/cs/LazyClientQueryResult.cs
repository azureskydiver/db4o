namespace com.db4o.@internal.cs
{
	/// <exclude></exclude>
	public class LazyClientQueryResult : com.db4o.@internal.query.result.AbstractQueryResult
	{
		private const int SIZE_NOT_SET = -1;

		private readonly com.db4o.@internal.cs.ClientObjectContainer _client;

		private readonly int _queryResultID;

		private int _size = SIZE_NOT_SET;

		private readonly com.db4o.@internal.cs.LazyClientIdIterator _iterator;

		public LazyClientQueryResult(com.db4o.@internal.Transaction trans, com.db4o.@internal.cs.ClientObjectContainer
			 client, int queryResultID) : base(trans)
		{
			_client = client;
			_queryResultID = queryResultID;
			_iterator = new com.db4o.@internal.cs.LazyClientIdIterator(this);
		}

		public override object Get(int index)
		{
			lock (StreamLock())
			{
				return ActivatedObject(GetId(index));
			}
		}

		public override int GetId(int index)
		{
			return AskServer(com.db4o.@internal.cs.messages.Msg.OBJECTSET_GET_ID, index);
		}

		public override int IndexOf(int id)
		{
			return AskServer(com.db4o.@internal.cs.messages.Msg.OBJECTSET_INDEXOF, id);
		}

		private int AskServer(com.db4o.@internal.cs.messages.MsgD message, int param)
		{
			_client.WriteMsg(message.GetWriterForInts(_transaction, new int[] { _queryResultID
				, param }));
			return ((com.db4o.@internal.cs.messages.MsgD)_client.ExpectedResponse(message)).ReadInt
				();
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			return _iterator;
		}

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return new com.db4o.@internal.cs.ClientQueryResultIterator(this);
		}

		public override int Size()
		{
			if (_size == SIZE_NOT_SET)
			{
				_client.WriteMsg(com.db4o.@internal.cs.messages.Msg.OBJECTSET_SIZE.GetWriterForInt
					(_transaction, _queryResultID));
				_size = ((com.db4o.@internal.cs.messages.MsgD)_client.ExpectedResponse(com.db4o.@internal.cs.messages.Msg
					.OBJECTSET_SIZE)).ReadInt();
			}
			return _size;
		}

		~LazyClientQueryResult()
		{
			_client.WriteMsg(com.db4o.@internal.cs.messages.Msg.OBJECTSET_FINALIZED.GetWriterForInt
				(_transaction, _queryResultID));
		}

		public override void LoadFromIdReader(com.db4o.@internal.Buffer reader)
		{
			_iterator.LoadFromIdReader(reader, reader.ReadInt());
		}

		public virtual void Reset()
		{
			_client.WriteMsg(com.db4o.@internal.cs.messages.Msg.OBJECTSET_RESET.GetWriterForInt
				(_transaction, _queryResultID));
		}

		public virtual void FetchIDs(int batchSize)
		{
			_client.WriteMsg(com.db4o.@internal.cs.messages.Msg.OBJECTSET_FETCH.GetWriterForInts
				(_transaction, new int[] { _queryResultID, batchSize }));
			com.db4o.@internal.Buffer reader = _client.ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
				.ID_LIST);
			LoadFromIdReader(reader);
		}
	}
}
