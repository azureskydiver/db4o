using System;

namespace com.db4o.test.cs
{
	public class CsType
	{
		Type myType;
		Type stringType;

		public void storeOne() 
		{
			myType = this.GetType();
			stringType = typeof(String);
		}

		public void testOne() 
		{
			Test.ensureEquals(this.GetType(), myType);
			Test.ensureEquals(typeof(String), stringType);
		}
	}
}
