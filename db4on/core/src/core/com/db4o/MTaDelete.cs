namespace com.db4o
{
	internal class MTaDelete : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			int id = _payLoad.readInt();
			int cascade = _payLoad.readInt();
			com.db4o.Transaction trans = getTransaction();
			com.db4o.YapStream stream = trans.i_stream;
			lock (stream.i_lock)
			{
				object[] arr = stream.getObjectAndYapObjectByID(trans, id);
				trans.delete((com.db4o.YapObject)arr[1], cascade);
				return true;
			}
		}
	}
}
