using System;
using System.Diagnostics;
using com.db4o.inside.query;

namespace CFNativeQueriesEnabler.Tests
{
    class Program
    {
        const string TestSubject = "CFNativeQueriesEnabler.Tests.Subject.exe";
        
        static void Main(string[] args)
        {
            try
            {
                new Program().Run();
            }
            catch (Exception x)
            {
                Console.WriteLine(x);
            }
        }
        
        void Run()
        {
            // Run the tests before any instrumentation.
            // The tests must be run out of process to avoid locking
            // the assembly file
            RunTestsOutOfProcess();
            InstrumentTestSubject();
            RunTests();
        }

        private void InstrumentTestSubject()
        {
            new CFNativeQueriesEnabler.Program(TestSubject).Run();
        }
        
        private void RunTests()
        {
            CFNativeQueriesEnabler.Tests.Subject.Program.QueryExecution += AssertIsMetaPredicateExecution;
            CFNativeQueriesEnabler.Tests.Subject.Program.SetUp();

            TestRunner runner = new TestRunner();
            runner.RunTestCase(typeof(CFNativeQueriesEnabler.Tests.Subject.Program));
            runner.RunTest("AssemblyVerification", VerifyAssembly);
            runner.Report();
            
        }
        
        private static void VerifyAssembly()
        {
            string output = shell("peverify.exe", TestSubject);
            if (output.ToUpper().Contains("WARNING")) throw new ApplicationException(output);
        }
        
        private void AssertIsMetaPredicateExecution(object sender, QueryExecutionEventArgs args)
        {
            if (!(args.Predicate is MetaDelegate<System.Predicate<CFNativeQueriesEnabler.Tests.Subject.Item>>))
            {
                throw new ApplicationException("Query invocation was not instrumented!");
            }
        }

        private void RunTestsOutOfProcess()
        {
            shell(TestSubject);
        }

        private static string shell(string fname, params string[] args)
        {
            Process p = StartProcess(fname, args);
            string output = p.StandardOutput.ReadToEnd();
            p.WaitForExit();
            if (p.ExitCode != 0)
            {
                throw new ApplicationException(output);
            }
            return output;
        }

        public static Process StartProcess(string filename, params string[] args)
        {
            Process p = new Process();
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.RedirectStandardInput = true;
            p.StartInfo.RedirectStandardError = true;
            p.StartInfo.FileName = filename;
            p.StartInfo.Arguments = string.Join(" ", args);
            p.Start();
            return p;
        }
    }
}
