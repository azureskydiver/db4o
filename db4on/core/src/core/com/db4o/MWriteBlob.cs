
namespace com.db4o
{
	internal class MWriteBlob : com.db4o.MsgBlob
	{
		internal override void processClient(com.db4o.foundation.network.YapSocket sock)
		{
			com.db4o.Msg message = com.db4o.Msg.readMessage(getTransaction(), sock);
			if (message.Equals(com.db4o.Msg.OK))
			{
				try
				{
					i_currentByte = 0;
					i_length = this.i_blob.getLength();
					i_blob.getStatusFrom(this);
					i_blob.setStatus(com.db4o.ext.Status.PROCESSING);
					j4o.io.FileInputStream inBlob = this.i_blob.getClientInputStream();
					copy(inBlob, sock, true);
					sock.flush();
					com.db4o.YapStream stream = getStream();
					message = com.db4o.Msg.readMessage(getTransaction(), sock);
					if (message.Equals(com.db4o.Msg.OK))
					{
						stream.deactivate(i_blob, int.MaxValue);
						stream.activate(i_blob, int.MaxValue);
						this.i_blob.setStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this.i_blob.setStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch (System.Exception e)
				{
					j4o.lang.JavaSystem.printStackTrace(e);
				}
			}
		}

		internal override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			try
			{
				com.db4o.YapStream stream = getStream();
				com.db4o.BlobImpl blobImpl = this.serverGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.setTrans(getTransaction());
					j4o.io.File file = blobImpl.serverFile(null, true);
					com.db4o.Msg.OK.write(stream, sock);
					j4o.io.FileOutputStream fout = new j4o.io.FileOutputStream(file);
					copy(sock, fout, blobImpl.getLength(), false);
					com.db4o.Msg.OK.write(stream, sock);
				}
			}
			catch (System.Exception e)
			{
			}
			return true;
		}
	}
}
