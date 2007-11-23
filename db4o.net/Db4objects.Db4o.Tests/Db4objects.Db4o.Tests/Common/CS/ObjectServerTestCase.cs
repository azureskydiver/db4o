/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using System.IO;
using Db4oUnit;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4o.Tests.Common.CS
{
	public class ObjectServerTestCase : ITestLifeCycle
	{
		private IExtObjectServer server;

		private string fileName;

		/// <exception cref="Exception"></exception>
		public virtual void SetUp()
		{
			fileName = Path.GetTempFileName();
			server = Db4oFactory.OpenServer(fileName, -1).Ext();
			server.GrantAccess(Credentials(), Credentials());
		}

		/// <exception cref="Exception"></exception>
		public virtual void TearDown()
		{
			server.Close();
			new Sharpen.IO.File(fileName).Delete();
		}

		public virtual void TestClientCount()
		{
			AssertClientCount(0);
			IObjectContainer client1 = Db4oFactory.OpenClient("localhost", Port(), Credentials
				(), Credentials());
			AssertClientCount(1);
			IObjectContainer client2 = Db4oFactory.OpenClient("localhost", Port(), Credentials
				(), Credentials());
			AssertClientCount(2);
			client1.Close();
			client2.Close();
		}

		private void AssertClientCount(int count)
		{
			Assert.AreEqual(count, server.ClientCount());
		}

		private int Port()
		{
			return server.Port();
		}

		private string Credentials()
		{
			return "DB4O";
		}
	}
}
