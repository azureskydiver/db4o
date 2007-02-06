namespace com.db4o.@internal.cs.messages
{
	public sealed class MWriteUpdate : com.db4o.@internal.cs.messages.MsgObject
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int yapClassId = _payLoad.ReadInt();
			com.db4o.@internal.LocalObjectContainer stream = (com.db4o.@internal.LocalObjectContainer
				)Stream();
			Unmarshall(_payLoad._offset);
			lock (StreamLock())
			{
				com.db4o.@internal.ClassMetadata yc = stream.GetYapClass(yapClassId);
				_payLoad.WriteEmbedded();
				int id = _payLoad.GetID();
				Transaction().DontDelete(id);
				com.db4o.@internal.slots.Slot oldSlot = ((com.db4o.@internal.LocalTransaction)_trans
					).GetCommittedSlotOfID(id);
				stream.GetSlotForUpdate(_payLoad);
				yc.AddFieldIndices(_payLoad, oldSlot);
				_payLoad.WriteEncrypt();
			}
			return true;
		}
	}
}
