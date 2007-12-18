using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spikes;
using System.CodeDom.Compiler;
using Microsoft.CSharp;

namespace LinqConsole
{
	public abstract class InteractiveQuery
	{
		public FarmSystem FarmSystem { get; set; }
		public IQueryable<Cow> Cows { get { return FarmSystem.Cows; } }
		public IQueryable<CustomCowEvent> Events { get { return FarmSystem.Events; } }
		public IQueryable<Milking> Milkings { get { return FarmSystem.Milkings; } }

		public abstract void Run();
	}

	class Program
	{
		static void Main(string[] args)
		{
			Console.WriteLine(@"LinqConsole .01a
Type a linq query followed by an empty line to see its results.
Type quit followed by an empty line to quit the application.
The following names are available: FarmSystem, Cows, Events, Milkings.

Example:
> from c in Cows select new { c.Code }
");

			using (var system = new FarmSystem())
			{
				EvalLoop(system);
			}
		}

		private static void EvalLoop(FarmSystem system)
		{	
			while (true)
			{
				string queryString = ReadQuery();
				if (queryString.StartsWith("quit")) break;

				InteractiveQuery query = CompileQuery(queryString);
				if (query != null)
				{
					query.FarmSystem = system;
					query.Run();
				}
			}
		}

		private static InteractiveQuery CompileQuery(string queryString)
		{
			string code = @"
using Spikes;
using System;
using System.Linq;
using LinqConsole;

public class TheQuery : InteractiveQuery
{
	public override void Run()
	{
		var result = " + queryString + @";
		result.PrettyPrint();
	}
}
";			
			var compiled = CompileType("TheQuery", code);
			if (null == compiled) return null;
			return (InteractiveQuery)Activator.CreateInstance(compiled);
		}

		private static System.Type CompileType(string typeName, string type)
		{
			using (var provider = new CSharpCodeProvider(new Dictionary<string, string>() { { "CompilerVersion", "v3.5" } }))
			{
				CompilerParameters parameters = new CompilerParameters();
				parameters.IncludeDebugInformation = false;
				parameters.GenerateInMemory = true;
				parameters.ReferencedAssemblies.Add(AssemblyFor(typeof(InteractiveQuery)));
				parameters.ReferencedAssemblies.Add(AssemblyFor(typeof(FarmSystem)));
				parameters.ReferencedAssemblies.Add(AssemblyFor(typeof(System.Linq.Enumerable)));

				var fname = SaveToFile(type);
				CompilerResults results = provider.CompileAssemblyFromFile(parameters, fname);
				if (results.Errors.Count > 0)
				{	
					foreach (var e in results.Errors)
					{
						Console.WriteLine(e);
					}
					return null;
				}
				return results.CompiledAssembly.GetType(typeName);
			}
		}

		private static string SaveToFile(string code)
		{
			var fname = System.IO.Path.GetTempFileName() + ".cs";
			System.IO.File.WriteAllText(fname, code);
			return fname;
		}

		private static string AssemblyFor(Type type)
		{
			return type.Assembly.ManifestModule.FullyQualifiedName;
		}

		private static string ReadQuery()
		{
			Console.Write("> ");
			StringBuilder builder = new StringBuilder();
			string line = null;
			while (null != (line = Console.ReadLine()))
			{
				if (line.Length == 0) break;
				builder.Append(line);
				builder.Append(Environment.NewLine);
			}
			return builder.ToString();
		}
	}
}
