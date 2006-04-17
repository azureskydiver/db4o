using System;
using System.IO;
using System.Collections.Generic;
using com.db4o;
using com.db4o.inside.query;

namespace CFNativeQueriesEnabler.Tests.Subject
{
	// TODO: query invocation with comparator
	// TODO: query invocation with comparison
	public class Program
	{
		public const string DatabaseFile = "subject.yap";

		public static event QueryExecutionHandler QueryExecution;

		public static int Main(string[] args)
		{
			try
			{
				Run();
			}
			catch (Exception x)
			{
				Console.WriteLine(x);
				return -1;
			}
			return 0;
		}

		public static void Run()
		{
			SetUp();
			TestInlineStaticDelegate();
			TestInlineClosureDelegate();
			TestStaticMemberDelegate();
			TestInstanceMemberDelegate();
			TestInlineStaticDelegateInsideExpression();
			TestMultipleQueryInvocations();
		}

		public static void SetUp()
		{
			if (File.Exists(DatabaseFile)) File.Delete(DatabaseFile);
			using (ObjectContainer container = OpenDatabase())
			{
				container.Set(new Item("foo"));
				container.Set(new Item("bar"));
			}
		}

		public static void TestInlineStaticDelegate()
		{
			using (ObjectContainer container = OpenDatabase())
			{
				IList<Item> items = container.Query<Item>(delegate(Item candidate)
				{
					return candidate.Name == "foo";
				});
				CheckResult(items);
			}
		}

		public static void TestInlineClosureDelegate()
		{
			using (ObjectContainer container = OpenDatabase())
			{
				string name = null;
				name = "foo";
				IList<Item> items = container.Query<Item>(delegate(Item candidate)
				{
					return candidate.Name == name;
				});
				CheckResult(items);
			}
		}

		public static void TestStaticMemberDelegate()
		{
			using (ObjectContainer container = OpenDatabase())
			{
				IList<Item> items = container.Query<Item>(Program.MatchFoo);
				CheckResult(items);
			}
		}

		public static void TestMultipleQueryInvocations()
		{
			using (ObjectContainer container = OpenDatabase())
			{
				CheckResult(container.Query<Item>(Program.MatchFoo));
				CheckResult(container.Query<Item>(Program.MatchFoo));
				CheckResult(container.Query<Item>(Program.MatchFoo));
			}
		}

		delegate ObjectContainer ObjectContainerAccessor();

		public static void TestInlineStaticDelegateInsideExpression()
		{
			using (ObjectContainer container = OpenDatabase())
			{
				ObjectContainerAccessor getter = delegate { return container; };
				CheckResult(getter().Query<Item>(delegate(Item candidate)
				{
					return candidate.Name == "foo";
				}));
			}
		}

		private static ObjectContainer OpenDatabase()
		{
			ObjectContainer container = Db4o.OpenFile(DatabaseFile);
			NativeQueryHandler handler = ((YapStream)container).GetNativeQueryHandler();
			handler.QueryExecution += OnQueryExecution;
			handler.QueryOptimizationFailure += OnQueryOptimizationFailure;
			return container;
		}

		static void OnQueryExecution(object sender, QueryExecutionEventArgs args)
		{
			if (null != QueryExecution) QueryExecution(sender, args);
		}

		static void OnQueryOptimizationFailure(object sender, com.db4o.inside.query.QueryOptimizationFailureEventArgs args)
		{
			throw new ApplicationException(args.Reason.Message, args.Reason);
		}

		public static void TestInstanceMemberDelegate()
		{
			using (ObjectContainer container = OpenDatabase())
			{
				IList<Item> items = container.Query<Item>(new QueryItemByName("foo").Match);
				CheckResult(items);
			}
		}

		private static void CheckResult(IList<Item> items)
		{
			Assert.AreEqual(1, items.Count);
			Assert.AreEqual("foo", items[0].Name);
		}

		static bool MatchFoo(Item candidate)
		{
			return candidate.Name == "foo";
		}

		class QueryItemByName
		{
			string _name;

			public QueryItemByName(string name)
			{
				_name = name;
			}

			public bool Match(Item candidate)
			{
				return candidate.Name == _name;
			}
		}
	}
}
