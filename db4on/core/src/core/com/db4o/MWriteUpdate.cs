namespace com.db4o
{
	internal sealed class MWriteUpdate : com.db4o.MsgObject
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int yapClassId = _payLoad.ReadInt();
			com.db4o.YapFile stream = (com.db4o.YapFile)GetStream();
			Unmarshall(com.db4o.YapConst.INT_LENGTH);
			lock (stream.i_lock)
			{
				com.db4o.YapClass yc = stream.GetYapClass(yapClassId);
				_payLoad.WriteEmbedded();
				stream.GetSlotForUpdate(_payLoad);
				yc.AddFieldIndices(_payLoad, false);
				stream.i_handlers.Encrypt(_payLoad);
				_payLoad.Write();
			}
			return true;
		}
	}
}
