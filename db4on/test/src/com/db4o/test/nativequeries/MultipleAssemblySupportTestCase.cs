using System;
using System.IO;
using System.Reflection;
using com.db4o;
using com.db4o.inside.query;
using com.db4o.query;
using com.db4o.ext;

namespace com.db4o.test.nativequeries
{
#if !CF_1_0 && !CF_2_0
	public class Author
	{
		private int _id;
		private string _name;

		public int Id
		{
			get { return _id; }
		}

		public string Name
		{
			get { return _name; }
		}

		public Author(int id, string name)
		{
			_id = id;
			_name = name;
		}
	}

	public class MultipleAssemblySupportTestCase
	{	
		public class InnerAuthor : Author
		{
			public InnerAuthor(int id, string name) : base(id, name)
			{
			}
		}

		public MultipleAssemblySupportTestCase()
		{
		}

		public void Store()
		{            
			Tester.Store(new Author(1, "Kurt Vonnegut"));
			Tester.Store(new Author(2, "Kilgore Trout"));
			Tester.Store(new InnerAuthor(3, "Joao Saramago"));
			Tester.Store(new InnerAuthor(4, "Douglas Adams"));
		}

		public void TestPredicateAccessingTopLevelType()
		{
			string predicateCode = @"
using com.db4o.query;
using com.db4o.test.nativequeries;

public class AuthorNamePredicate : Predicate
{
	public bool Match(Author candidate)
	{
		return candidate.Name == ""Kilgore Trout"";
	}
}
";
			AssertPredicate(2, predicateCode, "AuthorNamePredicate");
		}

		public void TestPredicateAccessingNestedType()
		{
			string predicateCode = @"
using com.db4o.query;
using com.db4o.test.nativequeries;

public class InnerAuthorNamePredicate : Predicate
{
	public bool Match(MultipleAssemblySupportTestCase.InnerAuthor candidate)
	{
		return candidate.Name == ""Joao Saramago"" && candidate.Id > 1;
	}
}
";
			AssertPredicate(3, predicateCode, "InnerAuthorNamePredicate");
		}

		private void AssertPredicate(int expectedId, string predicateCode, string predicateTypeName)
		{
            // use a prefix to avoid conflicts between SOLO and C/S tests run
            string assemblyNamePrefix = Tester.IsClientServer() ? "CS" : "";
			Assembly assembly = EmitAssemblyAndLoad(assemblyNamePrefix + predicateTypeName + ".dll", predicateCode);
	
			Predicate predicate = (Predicate)Activator.CreateInstance(assembly.GetType(predicateTypeName));
	
			ExtObjectContainer container = Tester.ObjectContainer();
            container.Configure().OptimizeNativeQueries(true);

			NativeQueryHandler handler = GetNativeQueryHandler(container);
            handler.QueryExecution += new QueryExecutionHandler(OnQueryExecution);
			try
			{
				ObjectSet os = container.Query(predicate);
				Tester.EnsureEquals(1, os.Size());
				Tester.EnsureEquals(expectedId, ((Author)os.Next()).Id);
			}
			finally
			{
                handler.QueryExecution -= new QueryExecutionHandler(OnQueryExecution);
			}
		}
		
		private static NativeQueryHandler GetNativeQueryHandler(ObjectContainer container)
		{
			return ((YapStream)container).GetNativeQueryHandler();
		}

		private static Assembly EmitAssemblyAndLoad(string assemblyName, string code)
		{	
			string assemblyFile = Path.Combine(Path.GetTempPath(), assemblyName);
			CompilationServices.EmitAssembly(assemblyFile, code);
			return System.Reflection.Assembly.LoadFrom(assemblyFile);
		}

		private void OnQueryExecution(object sender, QueryExecutionEventArgs args)
		{
			Tester.EnsureEquals(QueryExecutionKind.DynamicallyOptimized, args.ExecutionKind);
		}
	}
#endif
}
