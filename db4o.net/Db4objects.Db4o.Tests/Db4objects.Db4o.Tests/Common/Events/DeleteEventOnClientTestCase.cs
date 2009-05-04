/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4oUnit.Extensions.Fixtures;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Tests.Common.Events;

namespace Db4objects.Db4o.Tests.Common.Events
{
	public class DeleteEventOnClientTestCase : EventsTestCaseBase, IOptOutSolo
	{
		public static void Main(string[] args)
		{
			new DeleteEventOnClientTestCase().RunAll();
		}

		public virtual void TestAttachingToDeletingEventThrows()
		{
			if (IsMTOC())
			{
				return;
			}
			Assert.Expect(typeof(ArgumentException), new _ICodeBlock_17(this));
		}

		private sealed class _ICodeBlock_17 : ICodeBlock
		{
			public _ICodeBlock_17(DeleteEventOnClientTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			/// <exception cref="System.Exception"></exception>
			public void Run()
			{
				this._enclosing.EventRegistry().Deleting += new Db4objects.Db4o.Events.CancellableObjectEventHandler
					(new _IEventListener4_19().OnEvent);
			}

			private sealed class _IEventListener4_19
			{
				public _IEventListener4_19()
				{
				}

				public void OnEvent(object sender, Db4objects.Db4o.Events.CancellableObjectEventArgs
					 args)
				{
				}
			}

			private readonly DeleteEventOnClientTestCase _enclosing;
		}

		public virtual void TestAttachingToDeleteEventThrows()
		{
			if (IsMTOC())
			{
				return;
			}
			Assert.Expect(typeof(ArgumentException), new _ICodeBlock_30(this));
		}

		private sealed class _ICodeBlock_30 : ICodeBlock
		{
			public _ICodeBlock_30(DeleteEventOnClientTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			/// <exception cref="System.Exception"></exception>
			public void Run()
			{
				this._enclosing.EventRegistry().Deleted += new Db4objects.Db4o.Events.ObjectEventHandler
					(new _IEventListener4_32().OnEvent);
			}

			private sealed class _IEventListener4_32
			{
				public _IEventListener4_32()
				{
				}

				public void OnEvent(object sender, Db4objects.Db4o.Events.ObjectEventArgs args)
				{
				}
			}

			private readonly DeleteEventOnClientTestCase _enclosing;
		}
	}
}
