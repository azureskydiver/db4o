/* Copyright (C) 2004 - 2009  Versant Inc.  http://www.db4o.com */

#if !SILVERLIGHT
using System;
using System.Collections;
using Db4oUnit;
using Db4objects.Db4o;
using Db4objects.Db4o.CS;
using Db4objects.Db4o.CS.Config;
using Db4objects.Db4o.CS.Internal;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Freespace;
using Db4objects.Db4o.Internal.Ids;
using Db4objects.Db4o.Internal.Slots;
using Db4objects.Db4o.Tests.Common.Api;
using Db4objects.Db4o.Tests.Common.CS;

namespace Db4objects.Db4o.Tests.Common.CS
{
	public class PrefetchIDCountTestCase : TestWithTempFile
	{
		public static void Main(string[] args)
		{
			new ConsoleTestRunner(typeof(PrefetchIDCountTestCase)).Run();
		}

		private const int PrefetchIdCount = 100;

		private static readonly string User = "db4o";

		private static readonly string Password = "db4o";

		public class Item
		{
		}

		/// <exception cref="System.Exception"></exception>
		public virtual void Test()
		{
			ObjectServerImpl server = (ObjectServerImpl)Db4oClientServer.OpenServer(TempFile(
				), Db4oClientServer.ArbitraryPort);
			Lock4 Lock = new Lock4();
			server.ClientDisconnected += new System.EventHandler<Db4objects.Db4o.Events.StringEventArgs>
				(new _IEventListener4_38(Lock).OnEvent);
			server.GrantAccess(User, Password);
			IObjectContainer client = OpenClient(server.Port());
			ServerMessageDispatcherImpl msgDispatcher = FirstMessageDispatcherFor(server);
			Transaction transaction = msgDispatcher.Transaction();
			LocalObjectContainer container = (LocalObjectContainer)server.ObjectContainer();
			StandardIdSystem standardIdSystem = (StandardIdSystem)container.IdSystem();
			int prefetchedID = standardIdSystem.PrefetchID(transaction);
			Assert.IsGreater(0, prefetchedID);
			PrefetchIDCountTestCase.DebugFreespaceManager freespaceManager = new PrefetchIDCountTestCase.DebugFreespaceManager
				(container);
			container.InstallDebugFreespaceManager(freespaceManager);
			Lock.Run(new _IClosure4_60(client, Lock, freespaceManager, prefetchedID));
			server.Close();
		}

		private sealed class _IEventListener4_38
		{
			public _IEventListener4_38(Lock4 Lock)
			{
				this.Lock = Lock;
			}

			public void OnEvent(object sender, Db4objects.Db4o.Events.StringEventArgs args)
			{
				Lock.Run(new _IClosure4_39(Lock));
			}

			private sealed class _IClosure4_39 : IClosure4
			{
				public _IClosure4_39(Lock4 Lock)
				{
					this.Lock = Lock;
				}

				public object Run()
				{
					Lock.Awake();
					return null;
				}

				private readonly Lock4 Lock;
			}

			private readonly Lock4 Lock;
		}

		private sealed class _IClosure4_60 : IClosure4
		{
			public _IClosure4_60(IObjectContainer client, Lock4 Lock, PrefetchIDCountTestCase.DebugFreespaceManager
				 freespaceManager, int prefetchedID)
			{
				this.client = client;
				this.Lock = Lock;
				this.freespaceManager = freespaceManager;
				this.prefetchedID = prefetchedID;
			}

			public object Run()
			{
				client.Close();
				Lock.Snooze(100000);
				Assert.IsTrue(freespaceManager.WasFreed(prefetchedID));
				return null;
			}

			private readonly IObjectContainer client;

			private readonly Lock4 Lock;

			private readonly PrefetchIDCountTestCase.DebugFreespaceManager freespaceManager;

			private readonly int prefetchedID;
		}

		private ServerMessageDispatcherImpl FirstMessageDispatcherFor(ObjectServerImpl server
			)
		{
			IEnumerator dispatchers = server.IterateDispatchers();
			Assert.IsTrue(dispatchers.MoveNext());
			ServerMessageDispatcherImpl msgDispatcher = (ServerMessageDispatcherImpl)dispatchers
				.Current;
			return msgDispatcher;
		}

		private IObjectContainer OpenClient(int port)
		{
			IClientConfiguration config = Db4oClientServer.NewClientConfiguration();
			config.PrefetchIDCount = PrefetchIdCount;
			return Db4oClientServer.OpenClient(config, "localhost", port, User, Password);
		}

		public class DebugFreespaceManager : AbstractFreespaceManager
		{
			public DebugFreespaceManager(LocalObjectContainer file) : base(file)
			{
			}

			private readonly IList _freedSlots = new ArrayList();

			public virtual bool WasFreed(int id)
			{
				return _freedSlots.Contains(id);
			}

			public override Slot AllocateSlot(int length)
			{
				return null;
			}

			public override Slot AllocateTransactionLogSlot(int length)
			{
				return null;
			}

			public override void BeginCommit()
			{
			}

			// TODO Auto-generated method stub
			public override void Commit()
			{
			}

			// TODO Auto-generated method stub
			public override void EndCommit()
			{
			}

			// TODO Auto-generated method stub
			public override void Free(Slot slot)
			{
				_freedSlots.Add(slot.Address());
			}

			public override void FreeSelf()
			{
			}

			// TODO Auto-generated method stub
			public override void FreeTransactionLogSlot(Slot slot)
			{
			}

			// TODO Auto-generated method stub
			public override void Listener(IFreespaceListener listener)
			{
			}

			// TODO Auto-generated method stub
			public override void MigrateTo(IFreespaceManager fm)
			{
			}

			// TODO Auto-generated method stub
			public override void Read(int freeSpaceID)
			{
			}

			// TODO Auto-generated method stub
			public override int SlotCount()
			{
				// TODO Auto-generated method stub
				return 0;
			}

			public override void Start(int slotAddress)
			{
			}

			// TODO Auto-generated method stub
			public override byte SystemType()
			{
				// TODO Auto-generated method stub
				return 0;
			}

			public override int TotalFreespace()
			{
				// TODO Auto-generated method stub
				return 0;
			}

			public override void Traverse(IVisitor4 visitor)
			{
			}

			// TODO Auto-generated method stub
			public override int Write()
			{
				// TODO Auto-generated method stub
				return 0;
			}
		}
	}
}
#endif // !SILVERLIGHT
