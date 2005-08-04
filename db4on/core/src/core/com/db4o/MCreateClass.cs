namespace com.db4o
{
	internal sealed class MCreateClass : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.reflect.ReflectClass claxx = null;
			com.db4o.YapStream stream = getStream();
			com.db4o.Transaction trans = stream.getSystemTransaction();
			com.db4o.YapWriter returnBytes = new com.db4o.YapWriter(trans, 0);
			claxx = trans.reflector().forName(this.readString());
			if (claxx != null)
			{
				lock (stream.i_lock)
				{
					try
					{
						com.db4o.YapClass yapClass = stream.getYapClass(claxx, true);
						if (yapClass != null)
						{
							stream.checkStillToSet();
							yapClass.setStateDirty();
							yapClass.write(stream, trans);
							trans.commit();
							returnBytes = stream.readWriterByID(trans, yapClass.getID());
						}
					}
					catch (System.Exception t)
					{
					}
				}
			}
			com.db4o.Msg.OBJECT_TO_CLIENT.getWriter(returnBytes).write(stream, sock);
			return true;
		}
	}
}
