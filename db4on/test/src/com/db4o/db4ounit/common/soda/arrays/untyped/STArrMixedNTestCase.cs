namespace com.db4o.db4ounit.common.soda.arrays.untyped
{
	public class STArrMixedNTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object[][][] arr;

		public STArrMixedNTestCase()
		{
		}

		public STArrMixedNTestCase(object[][][] arr)
		{
			this.arr = arr;
		}

		public override object[] CreateData()
		{
			com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase[] arrMixed = new 
				com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase[5];
			arrMixed[0] = new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase
				();
			object[][][] content = new object[][][] { new object[][] { new object[2] } };
			arrMixed[1] = new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase
				(content);
			content = new object[][][] { new object[][] { new object[3], new object[3] }, new 
				object[][] { new object[3], new object[3] } };
			arrMixed[2] = new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase
				(content);
			content = new object[][][] { new object[][] { new object[3], new object[3] }, new 
				object[][] { new object[3], new object[3] } };
			content[0][0][1] = "foo";
			content[0][1][0] = "bar";
			content[0][1][2] = "fly";
			content[1][0][0] = false;
			arrMixed[3] = new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase
				(content);
			content = new object[][][] { new object[][] { new object[3], new object[3] }, new 
				object[][] { new object[3], new object[3] } };
			content[0][0][0] = "bar";
			content[0][1][0] = "wohay";
			content[0][1][1] = "johy";
			content[1][0][0] = 12;
			arrMixed[4] = new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase
				(content);
			object[] ret = new object[arrMixed.Length];
			System.Array.Copy(arrMixed, 0, ret, 0, arrMixed.Length);
			return ret;
		}

		public virtual void TestDefaultContainsString()
		{
			com.db4o.query.Query q = NewQuery();
			object[][][] content = new object[][][] { new object[][] { new object[1] } };
			content[0][0][0] = "bar";
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase(
				content));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsInteger()
		{
			com.db4o.query.Query q = NewQuery();
			object[][][] content = new object[][][] { new object[][] { new object[1] } };
			content[0][0][0] = 12;
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase(
				content));
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDefaultContainsBoolean()
		{
			com.db4o.query.Query q = NewQuery();
			object[][][] content = new object[][][] { new object[][] { new object[1] } };
			content[0][0][0] = false;
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase(
				content));
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			object[][][] content = new object[][][] { new object[][] { new object[1] }, new object[]
				[] { new object[1] } };
			content[0][0][0] = "bar";
			content[1][0][0] = 12;
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase(
				content));
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase)
				);
			q.Descend("arr").Constrain("bar");
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("arr");
			qElements.Constrain("foo");
			qElements.Constrain("bar");
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOneNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase)
				);
			q.Descend("arr").Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}

		public virtual void TestDescendTwoNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedNTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("arr");
			qElements.Constrain("foo").Not();
			qElements.Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}
	}
}
