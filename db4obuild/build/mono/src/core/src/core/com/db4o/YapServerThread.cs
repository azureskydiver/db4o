/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	internal sealed class YapServerThread : j4o.lang.Thread
	{
		private string i_clientName;

		private bool i_loggedin;

		private long i_lastClientMessage;

		private readonly com.db4o.YapFile i_mainStream;

		private com.db4o.Transaction i_mainTrans;

		private int i_pingAttempts = 0;

		private int i_nullMessages;

		private bool i_rollbackOnClose = true;

		private bool i_sendCloseMessage = true;

		private readonly com.db4o.YapServer i_server;

		private com.db4o.YapSocket i_socket;

		private com.db4o.YapFile i_substituteStream;

		private com.db4o.Transaction i_substituteTrans;

		private com.db4o.Config4Impl i_config;

		internal readonly int i_threadID;

		internal YapServerThread(com.db4o.YapServer aServer, com.db4o.YapFile aStream, com.db4o.YapSocket
			 aSocket, int aThreadID, bool loggedIn)
		{
			i_loggedin = loggedIn;
			i_lastClientMessage = j4o.lang.JavaSystem.currentTimeMillis();
			i_server = aServer;
			i_config = (com.db4o.Config4Impl)i_server.configure();
			i_mainStream = aStream;
			i_threadID = aThreadID;
			setName("db4o message server " + aThreadID);
			i_mainTrans = new com.db4o.Transaction(aStream, aStream.getSystemTransaction());
			try
			{
				i_socket = aSocket;
				i_socket.setSoTimeout(((com.db4o.Config4Impl)aServer.configure()).i_timeoutServerSocket
					);
			}
			catch (System.Exception e)
			{
				i_socket.close();
				throw (e);
			}
		}

		public void close()
		{
			closeSubstituteStream();
			try
			{
				if (i_sendCloseMessage)
				{
					com.db4o.Msg.CLOSE.write(i_mainStream, i_socket);
				}
			}
			catch (System.Exception e)
			{
			}
			if (i_mainStream != null && i_mainTrans != null)
			{
				i_mainTrans.close(i_rollbackOnClose);
			}
			try
			{
				i_socket.close();
			}
			catch (System.Exception e)
			{
			}
			i_socket = null;
			try
			{
				i_server.removeThread(this);
			}
			catch (System.Exception e)
			{
			}
		}

		private void closeSubstituteStream()
		{
			if (i_substituteStream != null)
			{
				if (i_substituteTrans != null)
				{
					i_substituteTrans.close(i_rollbackOnClose);
					i_substituteTrans = null;
				}
				try
				{
					i_substituteStream.close();
				}
				catch (System.Exception e)
				{
				}
				i_substituteStream = null;
			}
		}

		private com.db4o.YapFile getStream()
		{
			if (i_substituteStream != null)
			{
				return i_substituteStream;
			}
			return i_mainStream;
		}

		internal com.db4o.Transaction getTransaction()
		{
			if (i_substituteTrans != null)
			{
				return i_substituteTrans;
			}
			return i_mainTrans;
		}

		public override void run()
		{
			while (i_socket != null)
			{
				try
				{
					if (!messageProcessor())
					{
						break;
					}
				}
				catch (System.Exception e)
				{
					if (i_mainStream == null || i_mainStream.isClosed())
					{
						break;
					}
				}
				if (i_nullMessages > 20 || (j4o.lang.JavaSystem.currentTimeMillis() - i_lastClientMessage
					 > i_config.i_timeoutPingClients))
				{
					if (i_pingAttempts > 5)
					{
						getStream().logMsg(33, i_clientName);
						break;
					}
					com.db4o.Msg.PING.write(getStream(), i_socket);
					i_pingAttempts++;
				}
			}
			close();
		}

		private bool messageProcessor()
		{
			com.db4o.Msg message = com.db4o.Msg.readMessage(getTransaction(), i_socket);
			if (message == null)
			{
				i_nullMessages++;
				return true;
			}
			i_lastClientMessage = j4o.lang.JavaSystem.currentTimeMillis();
			i_nullMessages = 0;
			i_pingAttempts = 0;
			if (!i_loggedin)
			{
				if (com.db4o.Msg.LOGIN.Equals(message))
				{
					string userName = ((com.db4o.MsgD)message).readString();
					string password = ((com.db4o.MsgD)message).readString();
					com.db4o.User user = new com.db4o.User();
					user.name = userName;
					i_mainStream.showInternalClasses(true);
					com.db4o.User found = (com.db4o.User)i_mainStream.get(user).next();
					i_mainStream.showInternalClasses(false);
					if (found != null)
					{
						if (found.password.Equals(password))
						{
							i_clientName = userName;
							i_mainStream.logMsg(32, i_clientName);
							com.db4o.Msg.OK.write(i_mainStream, i_socket);
							i_loggedin = true;
							setName("db4o server socket for client " + i_clientName);
						}
						else
						{
							com.db4o.Msg.FAILED.write(i_mainStream, i_socket);
							return false;
						}
					}
					else
					{
						com.db4o.Msg.FAILED.write(i_mainStream, i_socket);
						return false;
					}
				}
				return true;
			}
			if (message.processMessageAtServer(i_socket))
			{
				return true;
			}
			if (com.db4o.Msg.CLOSE.Equals(message))
			{
				com.db4o.Msg.CLOSE.write(getStream(), i_socket);
				getTransaction().commit();
				i_sendCloseMessage = false;
				getStream().logMsg(34, i_clientName);
				return false;
			}
			if (com.db4o.Msg.IDENTITY.Equals(message))
			{
				respondInt((int)getStream().getID(getStream().i_bootRecord.i_db));
				return true;
			}
			if (com.db4o.Msg.CURRENT_VERSION.Equals(message))
			{
				long ver = getStream().i_bootRecord.i_versionGenerator;
				com.db4o.Msg.ID_LIST.getWriterForLong(getTransaction(), ver).write(getStream(), i_socket
					);
				return true;
			}
			if (com.db4o.Msg.RAISE_VERSION.Equals(message))
			{
				long minimumVersion = ((com.db4o.MsgD)message).readLong();
				com.db4o.YapStream stream = getStream();
				lock (stream)
				{
					stream.raiseVersion(minimumVersion);
				}
				return true;
			}
			if (com.db4o.Msg.GET_THREAD_ID.Equals(message))
			{
				respondInt(i_threadID);
				return true;
			}
			if (com.db4o.Msg.SWITCH_TO_FILE.Equals(message))
			{
				switchToFile(message);
				return true;
			}
			if (com.db4o.Msg.SWITCH_TO_MAIN_FILE.Equals(message))
			{
				switchToMainFile();
				return true;
			}
			if (com.db4o.Msg.USE_TRANSACTION.Equals(message))
			{
				useTransaction(message);
				return true;
			}
			return true;
		}

		private void switchToFile(com.db4o.Msg message)
		{
			lock (i_mainStream.i_lock)
			{
				string fileName = ((com.db4o.MsgD)message).readString();
				try
				{
					closeSubstituteStream();
					i_substituteStream = (com.db4o.YapFile)com.db4o.Db4o.openFile(fileName);
					i_substituteTrans = new com.db4o.Transaction(i_substituteStream, i_substituteStream
						.getSystemTransaction());
					i_substituteStream.i_config.i_messageRecipient = i_mainStream.i_config.i_messageRecipient;
					com.db4o.Msg.OK.write(getStream(), i_socket);
				}
				catch (System.Exception e)
				{
					closeSubstituteStream();
					com.db4o.Msg.ERROR.write(getStream(), i_socket);
				}
			}
		}

		private void switchToMainFile()
		{
			lock (i_mainStream.i_lock)
			{
				closeSubstituteStream();
				com.db4o.Msg.OK.write(getStream(), i_socket);
			}
		}

		private void useTransaction(com.db4o.Msg message)
		{
			int threadID = ((com.db4o.MsgD)message).readInt();
			com.db4o.YapServerThread transactionThread = i_server.findThread(threadID);
			if (transactionThread != null)
			{
				com.db4o.Transaction transToUse = transactionThread.getTransaction();
				if (i_substituteTrans != null)
				{
					i_substituteTrans = transToUse;
				}
				else
				{
					i_mainTrans = transToUse;
				}
				i_rollbackOnClose = false;
			}
		}

		private void respondInt(int response)
		{
			com.db4o.Msg.ID_LIST.getWriterForInt(getTransaction(), response).write(getStream(
				), i_socket);
		}
	}
}
