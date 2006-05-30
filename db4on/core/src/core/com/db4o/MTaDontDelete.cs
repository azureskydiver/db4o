namespace com.db4o
{
	internal class MTaDontDelete : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			int classID = _payLoad.ReadInt();
			int id = _payLoad.ReadInt();
			com.db4o.Transaction trans = GetTransaction();
			com.db4o.YapStream stream = trans.i_stream;
			lock (stream.i_lock)
			{
				trans.DontDelete(classID, id);
				return true;
			}
		}
	}
}
