namespace com.db4o.db4ounit.common.soda.arrays.@object
{
	public class STArrStringONTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object strArr;

		public STArrStringONTestCase()
		{
		}

		public STArrStringONTestCase(object[][][] arr)
		{
			strArr = arr;
		}

		public override object[] CreateData()
		{
			com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase[] arr = new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase
				[5];
			arr[0] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase();
			string[][][] content = new string[][][] { new string[][] { new string[2] } };
			arr[1] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase(content
				);
			content = new string[][][] { new string[][] { new string[3], new string[3] } };
			arr[2] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase(content
				);
			content = new string[][][] { new string[][] { new string[3], new string[3] } };
			content[0][0][1] = "foo";
			content[0][1][0] = "bar";
			content[0][1][2] = "fly";
			arr[3] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase(content
				);
			content = new string[][][] { new string[][] { new string[3], new string[3] } };
			content[0][0][0] = "bar";
			content[0][1][0] = "wohay";
			content[0][1][1] = "johy";
			arr[4] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase(content
				);
			object[] ret = new object[arr.Length];
			System.Array.Copy(arr, 0, ret, 0, arr.Length);
			return ret;
		}

		public virtual void TestDefaultContainsOne()
		{
			com.db4o.query.Query q = NewQuery();
			string[][][] content = new string[][][] { new string[][] { new string[1] } };
			content[0][0][0] = "bar";
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase
				(content));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			string[][][] content = new string[][][] { new string[][] { new string[1] }, new string[]
				[] { new string[1] } };
			content[0][0][0] = "bar";
			content[1][0][0] = "foo";
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase
				(content));
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase)
				);
			q.Descend("strArr").Constrain("bar");
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("strArr");
			qElements.Constrain("foo");
			qElements.Constrain("bar");
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOneNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase)
				);
			q.Descend("strArr").Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}

		public virtual void TestDescendTwoNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("strArr");
			qElements.Constrain("foo").Not();
			qElements.Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}
	}
}
