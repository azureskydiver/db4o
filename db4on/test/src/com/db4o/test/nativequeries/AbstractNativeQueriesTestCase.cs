using System;
using System.Collections;
using System.Reflection;
using System.Text;

using Db4oTools.NativeQueries;

using com.db4o.@internal.query;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.optimization;
using com.db4o.query;

namespace com.db4o.test.nativequeries
{
	public class AbstractNativeQueriesTestCase
	{
		protected void AssertNQResult(object predicate, params object[] expected)
		{
			ObjectSet os = QueryFromPredicate(predicate).Execute();
			string actualString = ToString(os);
			if (!Tester.Ensure("Expected: " + ToString(expected) + ", Actual: " + actualString, expected.Length == os.Size()))
			{
				return;
			}

			foreach (object item in expected)
			{
				Tester.Ensure("Expected item: " + item + " but got: " + actualString, os.Contains(item));
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
			Expression expression = (new QueryExpressionBuilder ()).FromMethod(match);
			Query q = Tester.Query();
			q.Constrain(match.GetParameters()[0].ParameterType);
			new SODAQueryBuilder().OptimizeQuery(expression, q, predicate);
			return q;
		}
	}
}