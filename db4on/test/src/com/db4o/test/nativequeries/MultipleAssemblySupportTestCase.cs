using System;
using System.IO;
using System.Reflection;
using com.db4o;
using com.db4o.inside.query;
using com.db4o.query;

namespace com.db4o.test.nativequeries
{
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

		public void store()
		{
			Tester.store(new Author(1, "Kurt Vonnegut"));
			Tester.store(new Author(2, "Kilgore Trout"));
			Tester.store(new InnerAuthor(3, "Joao Saramago"));
			Tester.store(new InnerAuthor(4, "Douglas Adams"));
		}

		public void testPredicateAccessingTopLevelType()
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

		public void testPredicateAccessingNestedType()
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
			Assembly assembly = EmitAssemblyAndLoad(predicateTypeName + ".dll", predicateCode);
	
			Predicate predicate = (Predicate)Activator.CreateInstance(assembly.GetType(predicateTypeName));
	
			ObjectContainer container = Tester.objectContainer();
			SetUpListener(container);
			try
			{
				ObjectSet os = container.query(predicate);
				Tester.ensureEquals(1, os.size());
				Tester.ensureEquals(expectedId, ((Author)os.next()).Id);
			}
			finally
			{
				ClearListeners(container);
			}
		}

		class Listener : com.db4o.inside.query.Db4oQueryExecutionListener
		{
			public void notifyQueryExecuted(object filter, string msg)
			{
				Tester.ensureEquals("DYNOPTIMIZED", msg);
			}
		}

		private void ClearListeners(ObjectContainer container)
		{
			GetNativeQueryHandler(container).clearListeners();
		}

		private static void SetUpListener(ObjectContainer container)
		{
			GetNativeQueryHandler(container).addListener(new Listener());
		}

		private static NativeQueryHandler GetNativeQueryHandler(ObjectContainer container)
		{
			return ((YapStream)container).getNativeQueryHandler();
		}

		private static Assembly EmitAssemblyAndLoad(string assemblyName, string code)
		{	
			AppDomain domain = AppDomain.CurrentDomain;
			string assemblyFile = Path.Combine(domain.BaseDirectory, assemblyName);
			CompilationServices.EmitAssembly(assemblyFile, code);
			return System.Reflection.Assembly.LoadFrom(assemblyFile);
		}
	}
}
