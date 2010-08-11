
using System;
using Db4oDoc.Code.Query.Soda;
using Db4oDoc.Code.Strategies.StoringStatic;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            SodaQueryExamples.Main(args);
            Console.Read();
        }
    }
}