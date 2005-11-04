namespace com.db4o
{
	internal class YapClientBlobThread : j4o.lang.Thread
	{
		private com.db4o.YapClient stream;

		private com.db4o.foundation.Queue4 queue = new com.db4o.foundation.Queue4();

		private bool terminated = false;

		internal YapClientBlobThread(com.db4o.YapClient aStream)
		{
			stream = aStream;
			setPriority(MIN_PRIORITY);
		}

		internal virtual void add(com.db4o.MsgBlob msg)
		{
			lock (queue)
			{
				queue.add(msg);
			}
		}

		internal virtual bool isTerminated()
		{
			lock (this)
			{
				return terminated;
			}
		}

		public override void run()
		{
			try
			{
				com.db4o.foundation.network.YapSocket socket = stream.createParalellSocket();
				com.db4o.MsgBlob msg = null;
				lock (queue)
				{
					msg = (com.db4o.MsgBlob)queue.next();
				}
				while (msg != null)
				{
					msg.write(stream, socket);
					msg.processClient(socket);
					lock (stream.blobLock)
					{
						lock (queue)
						{
							msg = (com.db4o.MsgBlob)queue.next();
						}
						if (msg == null)
						{
							terminated = true;
							com.db4o.Msg.CLOSE.write(stream, socket);
							try
							{
								socket.close();
							}
							catch (System.Exception e)
							{
							}
						}
					}
				}
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
		}
	}
}
