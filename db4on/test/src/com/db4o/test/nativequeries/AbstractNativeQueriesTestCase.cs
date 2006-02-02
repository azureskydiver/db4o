using System.Collections;
using System.Reflection;
using System.Text;
using com.db4o.inside.query;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.optimization;
using com.db4o.query;

namespace com.db4o.test.nativequeries
{
	public class AbstractNativeQueriesTestCase
	{
		protected void AssertNQResult(object predicate, params object[] expected)
		{
			ObjectSet os = QueryFromPredicate(predicate).execute();
			string actualString = ToString(os);
			if (!Tester.ensure("Expected: " + ToString(expected) + ", Actual: " + actualString, expected.Length == os.size()))
			{
				return;
			}

			foreach (object item in expected)
			{
				Tester.ensure("Expected item: " + item + " but got: " + actualString, os.Contains(item));
			}
		}

		private string ToString(IEnumerable os)
		{	
			StringBuilder buffer = new StringBuilder();
			buffer.Append('[');
			foreach (object item in os)
			{
				if (buffer.Length > 1) buffer.Append(", ");
				buffer.Append(item.ToString());
			}
			buffer.Append(']');
			return buffer.ToString();
		}

		private Query QueryFromPredicate(object predicate)
		{
			MethodInfo match = predicate.GetType().GetMethod("Match");
			Expression expression = QueryExpressionBuilder.FromMethod(match);
			Query q = Tester.query();
			q.constrain(match.GetParameters()[0].ParameterType);
			new SODAQueryBuilder().optimizeQuery(expression, q, predicate);
			return q;
		}
	}
}