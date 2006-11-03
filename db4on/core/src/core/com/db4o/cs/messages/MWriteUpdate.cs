namespace com.db4o.cs.messages
{
	public sealed class MWriteUpdate : com.db4o.cs.messages.MsgObject
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int yapClassId = _payLoad.ReadInt();
			com.db4o.YapFile stream = (com.db4o.YapFile)GetStream();
			Unmarshall(com.db4o.YapConst.INT_LENGTH);
			lock (stream.i_lock)
			{
				com.db4o.YapClass yc = stream.GetYapClass(yapClassId);
				_payLoad.WriteEmbedded();
				com.db4o.inside.slots.Slot oldSlot = _trans.GetCommittedSlotOfID(_payLoad.GetID()
					);
				stream.GetSlotForUpdate(_payLoad);
				yc.AddFieldIndices(_payLoad, oldSlot);
				_payLoad.WriteEncrypt();
			}
			return true;
		}
	}
}
