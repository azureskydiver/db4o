using System;
using Db4oUnit;
using Db4oUnit.Extensions;

namespace Db4objects.Db4o.Tests.CLI1.Soda
{
	public class CoerceUnsignedTypesTestUnit : AbstractDb4oTestCase
	{
		protected override void Store()
		{
			Store(TestVariables.Current.NewMinValue());
			Store(TestVariables.Current.NewMaxValue());
		}

		public void TestSimple()
		{
			var result = RunGreaterThanQuery(0);

			Assert.AreEqual(1, result.Count);
			Assert.AreEqual(TestVariables.Current.NewMaxValue(), result[0], TestVariables.Current.TypeName);
		}		
		
		public void TestRangeError()
		{
			Assert.Expect<OverflowException>(() => RunGreaterThanQuery(TestVariables.Current.InvalidValue));
		}

		//public void TestUnsignedRange()
		//{
		//}

		public void TestInvalidType()
		{
			Assert.Expect<FormatException>(() => RunGreaterThanQuery("string's cannot be cast to unsigned types"));
		}
		
		private IObjectSet RunGreaterThanQuery(object value)
		{
			var query = NewQuery();

			query.Descend("_value").Constrain(value).Greater();
			return query.Execute();
		}
	}
}
