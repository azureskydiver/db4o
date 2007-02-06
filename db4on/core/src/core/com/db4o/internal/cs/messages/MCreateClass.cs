namespace com.db4o.@internal.cs.messages
{
	public sealed class MCreateClass : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			com.db4o.@internal.Transaction trans = stream.GetSystemTransaction();
			try
			{
				com.db4o.reflect.ReflectClass claxx = trans.Reflector().ForName(ReadString());
				if (claxx != null)
				{
					lock (StreamLock())
					{
						try
						{
							com.db4o.@internal.ClassMetadata yapClass = stream.ProduceYapClass(claxx);
							if (yapClass != null)
							{
								stream.CheckStillToSet();
								yapClass.SetStateDirty();
								yapClass.Write(trans);
								trans.Commit();
								com.db4o.@internal.StatefulBuffer returnBytes = stream.ReadWriterByID(trans, yapClass
									.GetID());
								serverThread.Write(com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT.GetWriter(
									returnBytes));
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
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.FAILED);
			return true;
		}
	}
}
