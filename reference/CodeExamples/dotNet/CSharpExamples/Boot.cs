
using System;
using Db4oDoc.Code.Strategies.StoringStatic;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            StoringStaticFields.Main(args);
            Console.Read();
        }
    }
}