/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4oUnit.Extensions;
using Db4oUnit.Mocking;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Tests.Common.TA;

namespace Db4objects.Db4o.Tests.Common.TA
{
	public class ActivatableTestCase : TransparentActivationTestCaseBase
	{
		public static void Main(string[] args)
		{
			new ActivatableTestCase().RunAll();
		}

		public virtual void TestActivatorIsBoundUponStore()
		{
			MockActivatable mock = StoreNewMock();
			AssertSingleBindCall(mock);
		}

		/// <exception cref="Exception"></exception>
		public virtual void TestActivatorIsBoundUponRetrieval()
		{
			StoreNewMock();
			Reopen();
			AssertSingleBindCall(RetrieveMock());
		}

		/// <exception cref="Exception"></exception>
		public virtual void TestActivatorIsUnboundUponClose()
		{
			MockActivatable mock = StoreNewMock();
			Reopen();
			AssertBindUnbindCalls(mock);
		}

		public virtual void TestUnbindingIsIsolated()
		{
			if (!IsClientServer())
			{
				return;
			}
			MockActivatable mock1 = StoreNewMock();
			Db().Commit();
			MockActivatable mock2 = RetrieveMockFromNewClientAndClose();
			AssertBindUnbindCalls(mock2);
			// mock1 has only be bound by store so far
			// client.close should have no effect on it
			mock1.Recorder().Verify(new MethodCall[] { new MethodCall("bind", new _IArgumentCondition_51
				()) });
		}

		private sealed class _IArgumentCondition_51 : MethodCall.IArgumentCondition
		{
			public _IArgumentCondition_51()
			{
			}

			public void Verify(object argument)
			{
				Assert.IsNotNull(argument);
			}
		}

		private MockActivatable RetrieveMockFromNewClientAndClose()
		{
			IExtObjectContainer client = OpenNewClient();
			try
			{
				return RetrieveMock(client);
			}
			finally
			{
				client.Close();
			}
		}

		private IExtObjectContainer OpenNewClient()
		{
			return ((IDb4oClientServerFixture)Fixture()).OpenNewClient();
		}

		private void AssertBindUnbindCalls(MockActivatable mock)
		{
			mock.Recorder().Verify(new MethodCall[] { new MethodCall("bind", MethodCall.IgnoredArgument
				), new MethodCall("bind", new object[] { null }) });
		}

		private void AssertSingleBindCall(MockActivatable mock)
		{
			mock.Recorder().Verify(new MethodCall[] { new MethodCall("bind", MethodCall.IgnoredArgument
				) });
		}

		private MockActivatable RetrieveMock()
		{
			return RetrieveMock(Db());
		}

		private MockActivatable RetrieveMock(IExtObjectContainer container)
		{
			return (MockActivatable)RetrieveOnlyInstance(container, typeof(MockActivatable));
		}

		private MockActivatable StoreNewMock()
		{
			MockActivatable mock = new MockActivatable();
			Store(mock);
			return mock;
		}
	}
}
