namespace com.db4o.@internal.cs.messages
{
	public class MClassMeta : com.db4o.@internal.cs.messages.MsgObject
	{
		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			Unmarshall();
			try
			{
				com.db4o.@internal.cs.ClassInfo classMeta = (com.db4o.@internal.cs.ClassInfo)Stream
					().Unmarshall(_payLoad);
				com.db4o.reflect.generic.GenericClass genericClass = stream.GetClassMetaHelper().
					ClassMetaToGenericClass(Stream().Reflector(), classMeta);
				if (genericClass != null)
				{
					lock (StreamLock())
					{
						com.db4o.@internal.Transaction trans = stream.GetSystemTransaction();
						com.db4o.@internal.ClassMetadata yapClass = stream.ProduceYapClass(genericClass);
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
				}
			}
			catch (System.Exception e)
			{
			}
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.FAILED);
			return true;
		}
	}
}
