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
				stream.PrefetchedIDConsumed(_payLoad.GetID());
				_payLoad.Address(stream.GetSlot(_payLoad.GetLength()));
				if (yc != null)
				{
					yc.AddFieldIndices(_payLoad, null);
				}
				stream.WriteNew(yc, _payLoad);
				Transaction().WritePointer(_payLoad.GetID(), _payLoad.GetAddress(), _payLoad.GetLength
					());
			}
			return true;
		}
	}
}
