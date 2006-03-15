using System;

namespace com.db4o.test.cs
{
	public class CsDisposableTestCase
	{
		public void testDispose()
		{
			Tester.ensure(!Tester.objectContainer().isClosed());
			(Tester.objectContainer() as System.IDisposable).Dispose();
			Tester.ensure(Tester.objectContainer().isClosed());
		}
	}
}
