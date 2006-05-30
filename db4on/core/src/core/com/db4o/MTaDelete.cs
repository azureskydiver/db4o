namespace com.db4o
{
	internal class MTaDelete : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			int id = _payLoad.ReadInt();
			int cascade = _payLoad.ReadInt();
			com.db4o.Transaction trans = GetTransaction();
			com.db4o.YapStream stream = trans.i_stream;
			lock (stream.i_lock)
			{
				object[] arr = stream.GetObjectAndYapObjectByID(trans, id);
				trans.Delete((com.db4o.YapObject)arr[1], cascade);
				return true;
			}
		}
	}
}
