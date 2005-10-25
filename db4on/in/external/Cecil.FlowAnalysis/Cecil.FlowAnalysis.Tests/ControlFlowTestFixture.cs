using NUnit.Framework;

namespace Cecil.FlowAnalysis.Tests
{
	[TestFixture]
	public class ControlFlowTestFixture : AbstractControlFlowTestFixture
	{
		[Test]
		public void OptimizedNestedOr()
		{
			RunTestCase("OptimizedNestedOr");
		}

		[Test]
		public void NestedOrGreaterThan()
		{
			RunTestCase("NestedOrGreaterThan");
		}

		[Test]
		public void InRange()
		{
			RunTestCase("InRange");
		}

		[Test]
		public void OptimizedAnd()
		{
			RunTestCase("OptimizedAnd");
		}

		[Test]
		public void BoolAndGreaterOrEqualThan()
		{
			RunTestCase("BoolAndGreaterOrEqualThan");
		}

		[Test]
		public void OptimizedOr()
		{
			RunTestCase("OptimizedOr");
		}

		[Test]
		public void NotStringEquality()
		{
			RunTestCase("NotStringEquality");
		}

		[Test]
		public void IsNull()
		{
			RunTestCase("IsNull");
		}

		[Test]
		public void FieldAccessor()
		{
			RunTestCase("FieldAccessor");
		}

		[Test]
		public void NotEqual()
		{
			RunTestCase("NotEqual");
		}

		[Test]
		public void GreaterThanOrEqual()
		{
			RunTestCase("GreaterThanOrEqual");
		}

		[Test]
		public void LessThanOrEqual()
		{
			RunTestCase("LessThanOrEqual");
		}

		[Test]
		public void Empty()
		{
			RunTestCase("Empty");
		}

		[Test]
		public void SimpleReturn()
		{
			RunTestCase("SimpleReturn");
		}

		[Test]
		public void SimpleCalculation()
		{
			RunTestCase("SimpleCalculation");
		}

		[Test]
		public void SimpleCondition()
		{
			RunTestCase("SimpleCondition");
		}

		[Test]
		public void SingleAnd()
		{
			RunTestCase("SingleAnd");
		}

		[Test]
		public void SingleOr()
		{
			RunTestCase("SingleOr");
		}

		[Test]
		public void MultipleOr()
		{
			RunTestCase("MultipleOr");
		}

		[Test]
		public void MixedAndOr()
		{
			RunTestCase("MixedAndOr");
		}

		[Test]
		public void SimpleIf()
		{
			RunTestCase("SimpleIf");
		}

		[Test]
		public void TwoIfs()
		{
			RunTestCase("TwoIfs");
		}

		[Test]
		public void FalseIf()
		{
			RunTestCase("FalseIf");
		}

		[Test]
		public void IfNestedCondition()
		{
			RunTestCase("IfNestedCondition");
		}

		[Test]
		public void ThreeReturns()
		{
			RunTestCase("ThreeReturns");
		}

		[Test]
		public void TernaryExpression()
		{
			RunTestCase("TernaryExpression");
		}

		[Test]
		public void SideEffectExpression()
		{
			RunTestCase("SideEffectExpression");
		}

		[Test]
		public void SimpleWhile()
		{
			RunTestCase("SimpleWhile");
		}

		[Test]
		public void FlowTest()
		{
			RunTestCase("FlowTest");
		}

		[Test]
		public void PropertyPredicate()
		{
			RunTestCase("PropertyPredicate");
		}

		[Test]
		public void MultipleAndOr()
		{
			RunTestCase("MultipleAndOr");
		}

		[Test]
		public void StringPredicate()
		{
			RunTestCase("StringPredicate");
		}
	}
}
