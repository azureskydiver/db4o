namespace com.db4o.@internal.cs.messages
{
	public sealed class MCreateClass : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			com.db4o.@internal.Transaction trans = stream.GetSystemTransaction();
			com.db4o.reflect.ReflectClass claxx = trans.Reflector().ForName(ReadString());
			if (claxx == null)
			{
				return WriteFailedMessage(serverThread);
			}
			lock (StreamLock())
			{
				try
				{
					com.db4o.@internal.ClassMetadata yapClass = stream.ProduceYapClass(claxx);
					if (yapClass == null)
					{
						return WriteFailedMessage(serverThread);
					}
					stream.CheckStillToSet();
					yapClass.SetStateDirty();
					yapClass.Write(trans);
					trans.Commit();
					com.db4o.@internal.StatefulBuffer returnBytes = stream.ReadWriterByID(trans, yapClass
						.GetID());
					com.db4o.@internal.cs.messages.MsgD createdClass = com.db4o.@internal.cs.messages.Msg
						.OBJECT_TO_CLIENT.GetWriter(returnBytes);
					serverThread.Write(createdClass);
				}
				catch (com.db4o.foundation.Db4oRuntimeException)
				{
					WriteFailedMessage(serverThread);
				}
			}
			return true;
		}
	}
}
