
using System;
using Db4oDoc.Code.Query.NativeQueries;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            NativeQueryDiagnostics.Main(args);
            Console.Read();
        }
    }
}