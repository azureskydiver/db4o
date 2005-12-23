namespace com.db4o
{
	internal class YapClientThread : j4o.lang.Thread
	{
		private j4o.lang.Thread streamThread;

		private com.db4o.YapClient i_stream;

		private com.db4o.foundation.network.YapSocket i_socket;

		internal readonly com.db4o.foundation.Queue4 messageQueue;

		internal readonly com.db4o.foundation.Lock4 messageQueueLock;

		internal YapClientThread(com.db4o.YapClient client, com.db4o.foundation.network.YapSocket
			 a_socket, com.db4o.foundation.Queue4 messageQueue, com.db4o.foundation.Lock4 messageQueueLock
			)
		{
			lock (this)
			{
				i_stream = client;
				this.messageQueue = messageQueue;
				i_socket = a_socket;
				streamThread = j4o.lang.Thread.currentThread();
				this.messageQueueLock = messageQueueLock;
			}
		}

		internal virtual bool isClosed()
		{
			lock (this)
			{
				return i_socket == null;
			}
		}

		internal virtual void close()
		{
			lock (this)
			{
				i_stream = null;
				i_socket = null;
			}
		}

		public override void run()
		{
			while (i_socket != null)
			{
				try
				{
					if (i_stream == null)
					{
						return;
					}
					com.db4o.Msg message = com.db4o.Msg.readMessage(i_stream.getTransaction(), i_socket
						);
					if (i_stream == null)
					{
						return;
					}
					if (com.db4o.Msg.PING.Equals(message))
					{
						i_stream.writeMsg(com.db4o.Msg.OK);
					}
					else
					{
						if (com.db4o.Msg.CLOSE.Equals(message))
						{
							i_stream.logMsg(35, i_stream.ToString());
							if (i_stream == null)
							{
								return;
							}
							lock (i_stream)
							{
								j4o.lang.JavaSystem.notify(i_stream);
							}
							i_stream = null;
							i_socket = null;
						}
						else
						{
							if (message != null)
							{
								messageQueueLock.run(new _AnonymousInnerClass59(this, message));
							}
						}
					}
				}
				catch (System.Exception e)
				{
				}
			}
		}

		private sealed class _AnonymousInnerClass59 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass59(YapClientThread _enclosing, com.db4o.Msg message)
			{
				this._enclosing = _enclosing;
				this.message = message;
			}

			public object run()
			{
				this._enclosing.messageQueue.add(message);
				this._enclosing.messageQueueLock.awake();
				return null;
			}

			private readonly YapClientThread _enclosing;

			private readonly com.db4o.Msg message;
		}
	}
}
