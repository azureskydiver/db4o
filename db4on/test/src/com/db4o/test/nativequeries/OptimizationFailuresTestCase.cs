using System;
using System.Collections;
using System.IO;
using com.db4o.ext;
using com.db4o.@internal;
using com.db4o.@internal.query;

namespace com.db4o.test.nativequeries
{
	public class Node
	{	
		Node _next;
		int _id;

		public Node(Node next, int id)
		{
			_next = next;
			_id = id;
		}

		public Node Next
		{
			get { return _next; }
		}

		public int Id
		{
			get { return _id; }
		}

		public bool FollowedBy3
		{
			get
			{
				return _next.Id == 3 || _next.FollowedBy3;
			}
		}
	}

	public class FollowedByPredicate : com.db4o.query.Predicate
	{
		int _id;

		public FollowedByPredicate(int id)
		{
			_id = id;
		}

		public bool Match(Node candidate)
		{
			return candidate.Next.Id == _id
				|| Match(candidate.Next);
		}
	}

	public class FollowedBy3Predicate : com.db4o.query.Predicate
	{
		public bool Match(Node candidate)
		{
			return candidate.FollowedBy3;
		}
	}

	/// <summary>
	/// </summary>
	public class OptimizationFailuresTestCase
	{
		ArrayList _failures = new ArrayList();

		/*
		public void Store()
		{
			Node node = null;
			Tester.Store(node = new Node(null, 3));
			Tester.Store(node = new Node(node, 2));
			Tester.Store(node = new Node(node, 1));
		}*/

		public void TestRecursiveCandidateMethod()
		{
			ExpectFailure("this._next.get_FollowedBy3()", new FollowedBy3Predicate());
		}

		public void TestRecursivePredicateMethod()
		{
			ExpectFailure("this.Match(candidate.get_Next())", new FollowedByPredicate(2));
		}

		private void ExpectFailure(string expression, com.db4o.query.Predicate predicate)
		{
			_failures.Clear();

			ExtObjectContainer container = Tester.ObjectContainer();
			container.Configure().OptimizeNativeQueries(true);

			NativeQueryHandler handler = ((ObjectContainerBase)container).GetNativeQueryHandler();
			handler.QueryOptimizationFailure += new QueryOptimizationFailureHandler(OnOptimizationFailure);
			try
			{
				container.Query(predicate);
				Tester.EnsureEquals(1, _failures.Count,  Join(_failures));
				Tester.EnsureEquals("Unsupported expression: " + expression, ((Exception)_failures[0]).Message);
			}
			finally
			{
				handler.QueryOptimizationFailure -= new QueryOptimizationFailureHandler(OnOptimizationFailure);
			}
		}

		private string Join(IList items)
		{
			StringWriter writer = new StringWriter();
			foreach (object item in items)
			{
				writer.WriteLine(item);
			}
			return writer.ToString();
		}

		private void OnOptimizationFailure(object sender, QueryOptimizationFailureEventArgs args)
		{
			_failures.Add(args.Reason);
		}
	}
}
