namespace com.db4o.db4ounit.common.soda.arrays.@object
{
	public class STArrIntegerWUONTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object intArr;

		public STArrIntegerWUONTestCase()
		{
		}

		public STArrIntegerWUONTestCase(object[][][] arr)
		{
			intArr = arr;
		}

		public override object[] CreateData()
		{
			com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase[] arr = new 
				com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase[5];
			arr[0] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				();
			object[][][] content = new object[][][] {  };
			arr[1] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				(content);
			content = new object[][][] { new object[][] { new object[3], new object[3] } };
			content[0][0][1] = 0;
			content[0][1][0] = 0;
			arr[2] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				(content);
			content = new object[][][] { new object[][] { new object[3], new object[3] } };
			content[0][0][0] = 1;
			content[0][1][0] = 17;
			content[0][1][1] = int.MaxValue - 1;
			arr[3] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				(content);
			content = new object[][][] { new object[][] { new object[2], new object[2] } };
			content[0][0][0] = 3;
			content[0][0][1] = 17;
			content[0][1][0] = 25;
			content[0][1][1] = int.MaxValue - 2;
			arr[4] = new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				(content);
			object[] ret = new object[arr.Length];
			System.Array.Copy(arr, 0, ret, 0, arr.Length);
			return ret;
		}

		public virtual void TestDefaultContainsOne()
		{
			com.db4o.query.Query q = NewQuery();
			object[][][] content = new object[][][] { new object[][] { new object[1] } };
			content[0][0][0] = 17;
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				(content));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			object[][][] content = new object[][][] { new object[][] { new object[1] }, new object[]
				[] { new object[1] } };
			content[0][0][0] = 17;
			content[1][0][0] = 25;
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase
				(content));
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase)
				);
			q.Descend("intArr").Constrain(17);
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(17);
			qElements.Constrain(25);
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(3).Smaller();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestDescendNotSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(3).Smaller();
			Expect(q, new int[] { 2, 3 });
		}
	}
}
