using System;

namespace com.db4o.test.cs
{
	public class CsDisposableTestCase
	{
		public void TestDispose()
		{
			Tester.Ensure(!Tester.ObjectContainer().IsClosed());
			(Tester.ObjectContainer() as System.IDisposable).Dispose();
			Tester.Ensure(Tester.ObjectContainer().IsClosed());
		}
	}
}
