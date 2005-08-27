
namespace com.db4o
{
	internal class MReadBlob : com.db4o.MsgBlob
	{
		internal override void processClient(com.db4o.foundation.network.YapSocket sock)
		{
			com.db4o.Msg message = com.db4o.Msg.readMessage(getTransaction(), sock);
			if (message.Equals(com.db4o.Msg.LENGTH))
			{
				try
				{
					i_currentByte = 0;
					i_length = message.getPayLoad().readInt();
					i_blob.getStatusFrom(this);
					i_blob.setStatus(com.db4o.ext.Status.PROCESSING);
					copy(sock, this.i_blob.getClientOutputStream(), i_length, true);
					message = com.db4o.Msg.readMessage(getTransaction(), sock);
					if (message.Equals(com.db4o.Msg.OK))
					{
						this.i_blob.setStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this.i_blob.setStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch (System.Exception e)
				{
				}
			}
			else
			{
				if (message.Equals(com.db4o.Msg.ERROR))
				{
					this.i_blob.setStatus(com.db4o.ext.Status.ERROR);
				}
			}
		}

		internal override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = getStream();
			try
			{
				com.db4o.BlobImpl blobImpl = this.serverGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.setTrans(getTransaction());
					j4o.io.File file = blobImpl.serverFile(null, false);
					int length = (int)file.length();
					com.db4o.Msg.LENGTH.getWriterForInt(getTransaction(), length).write(stream, sock);
					j4o.io.FileInputStream fin = new j4o.io.FileInputStream(file);
					copy(fin, sock, false);
					sock.flush();
					com.db4o.Msg.OK.write(stream, sock);
				}
			}
			catch (System.Exception e)
			{
				com.db4o.Msg.ERROR.write(stream, sock);
			}
			return true;
		}
	}
}
