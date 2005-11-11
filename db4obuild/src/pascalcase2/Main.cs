using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using ICSharpCode.NRefactory;
using ICSharpCode.NRefactory.Parser;
using ICSharpCode.NRefactory.Parser.AST;
using ICSharpCode.NRefactory.PrettyPrinter;
using Boo.Lang.Useful.IO;

namespace HelloWorld
{	
	class PascalCaseConverter : AbstractASTVisitor
	{
		public override object Visit(MethodDeclaration method, object data)
		{
			method.Name = ToPascalCase(method.Name);
			return base.Visit(method, data);
		}
		
		public override object Visit(InvocationExpression invocation, object data)
		{
			//System.Console.WriteLine(invocation.TargetObject);
			FieldReferenceExpression memberRef = invocation.TargetObject as FieldReferenceExpression;
			if (null != memberRef)
			{
				memberRef.FieldName = ToPascalCase(memberRef.FieldName);
			}
			else
			{
				IdentifierExpression identifier = invocation.TargetObject as IdentifierExpression;
				if (null != identifier)
				{
					identifier.Identifier = ToPascalCase(identifier.Identifier);
				}
			}
			return base.Visit(invocation, data);
		}
		
		private string ToPascalCase(string name)
		{
			return name.Substring(0, 1).ToUpper() + name.Substring(1);
		}
	}
	
	class Program
	{
		
		public static void Main(string[] args)
		{		
			new Program(args).Run();
		}
		
		string _srcDir;
		string _targetDir;
		
		public Program(string[] args)
		{
			_srcDir = args[0];
			_targetDir = args[1];
		}
		
		public void Run()
		{
			ConvertDirectory();
		}
		
		void ConvertDirectory()
		{
			foreach (string fname in ListFiles(_srcDir, "*.cs"))
			{
				string targetFile = Path.Combine(_targetDir, fname.Substring(_srcDir.Length + 1));
				Console.Write(".");
				Directory.CreateDirectory(Path.GetDirectoryName(targetFile));
				WriteFile(targetFile, ConvertFile(fname));
			}
		}
		
		static string ConvertFile(string fname)
		{
			using (StreamReader reader = File.OpenText(fname))
			{
				return ConvertReader(reader);
			}
		}
		
		static string ConvertReader(System.IO.TextReader reader)
		{
			IParser parser = ParserFactory.CreateParser(SupportedLanguage.CSharp, reader);
			parser.Parse();
			
			CompilationUnit cu = parser.CompilationUnit;
			cu.AcceptVisitor(new PascalCaseConverter(), null);
			
			PrettyPrintOptions options = new PrettyPrintOptions();
			options.PropertyBraceStyle = BraceStyle.NextLine;
			options.PropertyGetBraceStyle = BraceStyle.NextLine;
			options.PropertySetBraceStyle = BraceStyle.NextLine;
			options.IndentationChar = ' ';
			options.IndentSize = 4;
			options.TabSize = 4;
			
			CSharpOutputVisitor visitor = new CSharpOutputVisitor();
			visitor.Options = options;
			
			List<ISpecial> specials = parser.Lexer.SpecialTracker.CurrentSpecials;
			
			SpecialNodesInserter sni = new SpecialNodesInserter(specials,
			                                                    new SpecialOutputVisitor(visitor.OutputFormatter));
			visitor.NodeTracker.NodeVisiting += sni.AcceptNodeStart;
			visitor.NodeTracker.NodeVisited  += sni.AcceptNodeEnd;
			visitor.NodeTracker.NodeChildrenVisited += sni.AcceptNodeEnd;
			cu.AcceptVisitor(visitor, null);
			sni.Finish();
			return visitor.Text;
		}
		
		static IEnumerable<string> ListFiles(string dir, string glob)
		{
			foreach (string fname in Directory.GetFiles(dir, glob))
			{
				yield return fname;
			}
			foreach (string subDir in Directory.GetDirectories(dir))
			{
				foreach (string fname in ListFiles(subDir, glob))
				{
					yield return fname;
				}
			}
		}
		
		static void WriteFile(string fname, string contents)
		{
			using (StreamWriter writer = new StreamWriter(fname, false, System.Text.Encoding.ASCII))
			{
				writer.Write(contents);
			}
		}
	}
}
