using System.Diagnostics;
using System.IO;
using Mono.Cecil;
using NUnit.Framework;

namespace Cecil.FlowAnalysis.Tests
{
	public class AbstractFlowAnalysisTestFixture
	{
		protected string normalize(string s)
		{
			return s.Trim().Replace("\r\n", "\n");
		}

		private void CompileTestCase(string name)
		{
			string sourceFile = MapTestCasePath(name + ".il");
			Assert.IsTrue(File.Exists(sourceFile), sourceFile + " not found!");
			ilasm(string.Format("/DLL /OUTPUT:{0} {1}", TestAssemblyPath, sourceFile));
		}

		protected string LoadTestCaseFile(string fname)
		{
			using (StreamReader reader=File.OpenText(MapTestCasePath(fname)))
			{
				return reader.ReadToEnd();
			}
		}

		protected string MapTestCasePath(string name)
		{
			return Path.Combine(TestCasesDirectory, name);
		}

		private void ilasm(string arguments)
		{
			Process p = new Process();
			p.StartInfo.Arguments = arguments;
			p.StartInfo.CreateNoWindow = true;
			p.StartInfo.UseShellExecute = false;
			p.StartInfo.RedirectStandardOutput = true;
			p.StartInfo.RedirectStandardInput = true;
			p.StartInfo.RedirectStandardError = true;
			p.StartInfo.FileName = "ilasm";
			p.Start();
			string output = p.StandardOutput.ReadToEnd();
			string error = p.StandardError.ReadToEnd();
			p.WaitForExit();
			Assert.AreEqual(0, p.ExitCode, output + error);
		}

		protected IMethodDefinition LoadTestCaseMethod(string testCaseName)
		{
			CompileTestCase(testCaseName);

			IAssemblyDefinition assembly = AssemblyFactory.GetAssembly(TestAssemblyPath);
			ITypeDefinition type = assembly.MainModule.Types["TestCase"];
			Assert.IsNotNull(type, "Type TestCase not found!");
			IMethodDefinition[] found = type.Methods.GetMethod("Main");
			Assert.AreEqual(1, found.Length, "Method TestCase.Main not found!");
			return found[0];
		}

		public string TestCasesDirectory
		{
			get
			{
				return Path.GetFullPath(Path.Combine(Path.GetDirectoryName(GetCurrentModulePath()), "../testcases/FlowAnalysis"));
			}
		}

		private string GetCurrentModulePath()
		{
			return new System.Uri(GetType().Assembly.CodeBase).LocalPath;
		}

		public string TestAssemblyPath
		{
			get
			{
				return Path.Combine(Path.GetTempPath(), "TestCase.dll");
			}
		}
	}
}