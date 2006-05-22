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

		public void StoreOne() 
		{
			_field = "foo";
			_placeHolder = 42;
		}

		public void TestOne()
		{
			Tester.EnsureEquals("foo", _field);
			Tester.EnsureEquals(42, _placeHolder);
		}
	}
}
