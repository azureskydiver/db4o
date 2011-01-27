
using System;
using Db4oDoc.Code.db4otests;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            ExampleTestCase.Main(args);
            Console.Read();
        }
    }
}