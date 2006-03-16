using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Reflection;
using System.Text;
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

            int nFailures = 0;
            int nTests = 0;
            foreach (MethodInfo method in typeof(CFNativeQueriesEnabler.Tests.Subject.Program).GetMethods(BindingFlags.Public | BindingFlags.Static))
            {
                if (!method.Name.StartsWith("Test")) continue;

                ++nTests;
                try
                {
                    method.Invoke(null, null);
                }
                catch (TargetInvocationException x)
                {
                    Console.WriteLine("{0}) {1}: {2}", ++nFailures, method.Name, x.InnerException);
                    Console.WriteLine();
                }
            }
            Console.WriteLine("{0} out of {1} tests passed.", nTests-nFailures, nTests);
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
            Process p = StartProcess(TestSubject);
            string output = p.StandardOutput.ReadToEnd();
            p.WaitForExit();
            if (p.ExitCode != 0)
            {
                throw new ApplicationException(output);
            }
        }

        public static Process StartProcess(string filename)
        {
            Process p = new Process();
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.RedirectStandardInput = true;
            p.StartInfo.RedirectStandardError = true;
            p.StartInfo.FileName = filename;
            p.Start();
            return p;
        }
    }
}
