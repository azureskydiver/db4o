
using System;
using System.Diagnostics;
using System.Linq;
using Db4objects.Db4o;
using Db4objects.Db4o.Diagnostic;
using Db4oDoc.Code.TypeHandling.TypeHandler;
using Db4objects.Db4o.Linq;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            TypeHandlerExample.Main(args);
            Console.Read();
        }
    }
}