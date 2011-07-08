
using System;
using Db4oDoc.Code.Concurrency.Transactions;
using Db4oDoc.Code.indexing.traverse;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            TraverseIndexExample.Main(args);

            Console.Read();
        }
    }
}