using System;

namespace com.db4o.test.cs
{
	/// <summary>
	/// Summary description for CsMarshalByRef.
	/// </summary>
	public class CsMarshalByRef : System.MarshalByRefObject
	{
		int _placeHolder;
		string _field;

		public void storeOne() 
		{
			_field = "foo";
			_placeHolder = 42;
		}

		public void testOne()
		{
			Tester.ensureEquals("foo", _field);
			Tester.ensureEquals(42, _placeHolder);
		}
	}
}
