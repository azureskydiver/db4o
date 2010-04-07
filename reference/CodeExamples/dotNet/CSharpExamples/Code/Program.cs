using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CSharpExamples
{
    class Program
    {
        static void Main(string[] args)
        {
            //#snippet:Hi From CSharp
            Func<int> hihi= () => 1;
            Console.Out.WriteLine("hi {0}",hihi());
            //#endsnippet
        }
    }
}
