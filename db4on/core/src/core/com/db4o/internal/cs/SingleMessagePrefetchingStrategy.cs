namespace com.db4o.@internal.cs
{
	/// <summary>Prefetchs multiples objects at once (in a single message).</summary>
	/// <remarks>Prefetchs multiples objects at once (in a single message).</remarks>
	/// <exclude></exclude>
	public class SingleMessagePrefetchingStrategy : com.db4o.@internal.cs.PrefetchingStrategy
	{
		public static readonly com.db4o.@internal.cs.PrefetchingStrategy INSTANCE = new com.db4o.@internal.cs.SingleMessagePrefetchingStrategy
			();

		private SingleMessagePrefetchingStrategy()
		{
		}

		public virtual int PrefetchObjects(com.db4o.@internal.cs.ClientObjectContainer container
			, com.db4o.foundation.IntIterator4 ids, object[] prefetched, int prefetchCount)
		{
			int count = 0;
			int toGet = 0;
			int[] idsToGet = new int[prefetchCount];
			int[] position = new int[prefetchCount];
			while (count < prefetchCount)
			{
				if (!ids.MoveNext())
				{
					break;
				}
				int id = ids.CurrentInt();
				if (id > 0)
				{
					object obj = container.ObjectForIdFromCache(id);
					if (obj != null)
					{
						prefetched[count] = obj;
					}
					else
					{
						idsToGet[toGet] = id;
						position[toGet] = count;
						toGet++;
					}
					count++;
				}
			}
			if (toGet > 0)
			{
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.READ_MULTIPLE_OBJECTS
					.GetWriterForIntArray(container.GetTransaction(), idsToGet, toGet);
				container.WriteMsg(msg, true);
				com.db4o.@internal.cs.messages.MsgD message = (com.db4o.@internal.cs.messages.MsgD
					)container.ExpectedResponse(com.db4o.@internal.cs.messages.Msg.READ_MULTIPLE_OBJECTS
					);
				int embeddedMessageCount = message.ReadInt();
				for (int i = 0; i < embeddedMessageCount; i++)
				{
					com.db4o.@internal.cs.messages.MsgObject mso = (com.db4o.@internal.cs.messages.MsgObject
						)com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT.Clone(container.GetTransaction
						());
					mso.PayLoad(message.PayLoad().ReadYapBytes());
					if (mso.PayLoad() != null)
					{
						mso.PayLoad().IncrementOffset(com.db4o.@internal.Const4.MESSAGE_LENGTH);
						com.db4o.@internal.StatefulBuffer reader = mso.Unmarshall(com.db4o.@internal.Const4
							.MESSAGE_LENGTH);
						object obj = container.ObjectForIdFromCache(idsToGet[i]);
						if (obj != null)
						{
							prefetched[position[i]] = obj;
						}
						else
						{
							prefetched[position[i]] = new com.db4o.@internal.ObjectReference(idsToGet[i]).ReadPrefetch
								(container, reader);
						}
					}
				}
			}
			return count;
		}
	}
}
