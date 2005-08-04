using System;
using j4o.lang;

namespace com.db4o.test.j4otest
{
	public class ClassTest
	{
		public void testForName()
		{
			string name = typeof(System.ComponentModel.ListChangedEventHandler).FullName + ", System";

			Class found1 = Class.forName(name);
			Class found2 = Class.forName(name);

			Tester.ensure(null != found1);
			Tester.ensure(null != found2);
			Tester.ensureEquals(found1, found2);
		}
	}
}
