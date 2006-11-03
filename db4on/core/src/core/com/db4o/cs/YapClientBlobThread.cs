namespace com.db4o.cs
{
	internal class YapClientBlobThread : j4o.lang.Thread
	{
		private com.db4o.cs.YapClient stream;

		private com.db4o.foundation.Queue4 queue = new com.db4o.foundation.Queue4();

		private bool terminated = false;

		internal YapClientBlobThread(com.db4o.cs.YapClient aStream)
		{
			stream = aStream;
			SetPriority(MIN_PRIORITY);
		}

		internal virtual void Add(com.db4o.cs.messages.MsgBlob msg)
		{
			lock (queue)
			{
				queue.Add(msg);
			}
		}

		internal virtual bool IsTerminated()
		{
			lock (this)
			{
				return terminated;
			}
		}

		public override void Run()
		{
			try
			{
				com.db4o.foundation.network.YapSocket socket = stream.CreateParalellSocket();
				com.db4o.cs.messages.MsgBlob msg = null;
				lock (queue)
				{
					msg = (com.db4o.cs.messages.MsgBlob)queue.Next();
				}
				while (msg != null)
				{
					msg.Write(stream, socket);
					msg.ProcessClient(socket);
					lock (stream.blobLock)
					{
						lock (queue)
						{
							msg = (com.db4o.cs.messages.MsgBlob)queue.Next();
						}
						if (msg == null)
						{
							terminated = true;
							com.db4o.cs.messages.Msg.CLOSE.Write(stream, socket);
							try
							{
								socket.Close();
							}
							catch
							{
							}
						}
					}
				}
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
		}
	}
}
