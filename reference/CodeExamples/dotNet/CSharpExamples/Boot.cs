
using System;
using Db4oDoc.Code.Concurrency.Transactions;
namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            TransactionOperations.Main(args);

            Console.Read();
        }
    }
}