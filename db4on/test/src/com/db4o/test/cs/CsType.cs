using System;

namespace com.db4o.test.cs
{
	public class CsType
	{
		Type myType;
		Type stringType;

		public void StoreOne() 
		{
			myType = this.GetType();
			stringType = typeof(String);
		}

		public void TestOne() 
		{
			Tester.EnsureEquals(this.GetType(), myType);
			Tester.EnsureEquals(typeof(String), stringType);
		}
	}
}
