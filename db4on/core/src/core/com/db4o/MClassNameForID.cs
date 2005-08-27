
namespace com.db4o
{
	/// <summary>get the classname for an internal ID</summary>
	internal sealed class MClassNameForID : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int id = payLoad.readInt();
			string name = "";
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				try
				{
					com.db4o.YapClass yapClass = stream.getYapClass(id);
					if (yapClass != null)
					{
						name = yapClass.getName();
					}
				}
				catch (System.Exception t)
				{
				}
			}
			com.db4o.Msg.CLASS_NAME_FOR_ID.getWriterForString(getTransaction(), name).write(stream
				, sock);
			return true;
		}
	}
}
