namespace com.db4o.cs.messages
{
	public sealed class MCreateClass : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			com.db4o.Transaction trans = stream.GetSystemTransaction();
			com.db4o.YapWriter returnBytes = new com.db4o.YapWriter(trans, 0);
			try
			{
				com.db4o.reflect.ReflectClass claxx = trans.Reflector().ForName(ReadString());
				if (claxx != null)
				{
					lock (stream.i_lock)
					{
						try
						{
							com.db4o.YapClass yapClass = stream.GetYapClass(claxx, true);
							if (yapClass != null)
							{
								stream.CheckStillToSet();
								yapClass.SetStateDirty();
								yapClass.Write(trans);
								trans.Commit();
								returnBytes = stream.ReadWriterByID(trans, yapClass.GetID());
								com.db4o.cs.messages.Msg.OBJECT_TO_CLIENT.GetWriter(returnBytes).Write(stream, sock
									);
								return true;
							}
						}
						catch
						{
						}
					}
				}
			}
			catch
			{
			}
			com.db4o.cs.messages.Msg.FAILED.Write(stream, sock);
			return true;
		}
	}
}
