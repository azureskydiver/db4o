namespace com.db4o.@internal.cs.messages
{
	public sealed class MWriteNew : com.db4o.@internal.cs.messages.MsgObject
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
				com.db4o.@internal.ClassMetadata yc = yapClassId == 0 ? null : stream.GetYapClass
					(yapClassId);
				_payLoad.WriteEmbedded();
				int id = _payLoad.GetID();
				int length = _payLoad.GetLength();
				stream.PrefetchedIDConsumed(id);
				Transaction().SlotFreePointerOnRollback(id);
				int address = stream.GetSlot(length);
				_payLoad.Address(address);
				Transaction().SlotFreeOnRollback(id, address, length);
				if (yc != null)
				{
					yc.AddFieldIndices(_payLoad, null);
				}
				stream.WriteNew(yc, _payLoad);
				Transaction().WritePointer(id, address, length);
			}
			return true;
		}
	}
}
