namespace com.db4o.cs.messages
{
	public sealed class MWriteNew : com.db4o.cs.messages.MsgObject
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int yapClassId = _payLoad.ReadInt();
			com.db4o.YapFile stream = (com.db4o.YapFile)GetStream();
			Unmarshall(com.db4o.YapConst.INT_LENGTH);
			lock (stream.i_lock)
			{
				com.db4o.YapClass yc = yapClassId == 0 ? null : stream.GetYapClass(yapClassId);
				_payLoad.WriteEmbedded();
				stream.PrefetchedIDConsumed(_payLoad.GetID());
				_payLoad.Address(stream.GetSlot(_payLoad.GetLength()));
				if (yc != null)
				{
					yc.AddFieldIndices(_payLoad, null);
				}
				stream.WriteNew(yc, _payLoad);
				GetTransaction().WritePointer(_payLoad.GetID(), _payLoad.GetAddress(), _payLoad.GetLength
					());
			}
			return true;
		}
	}
}
