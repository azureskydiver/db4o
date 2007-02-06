namespace com.db4o.@internal.cs.messages
{
	public class MObjectByUuid : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			long uuid = ReadLong();
			byte[] signature = ReadBytes();
			int id = 0;
			com.db4o.@internal.Transaction trans = Transaction();
			lock (StreamLock())
			{
				try
				{
					object[] arr = trans.ObjectAndYapObjectBySignature(uuid, signature);
					if (arr[1] != null)
					{
						com.db4o.@internal.ObjectReference yo = (com.db4o.@internal.ObjectReference)arr[1
							];
						id = yo.GetID();
					}
				}
				catch (System.Exception e)
				{
				}
			}
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.OBJECT_BY_UUID.GetWriterForInt
				(trans, id));
			return true;
		}
	}
}
