/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

    internal class ConsoleWriter : PrintStream {

        public override void Println() {
            Console.WriteLine();
        }

        public override void Println(Object obj) {
            Console.WriteLine(obj);
        }

        public override void Print(Object obj) {
            Console.WriteLine(obj);
        }
    }
}
