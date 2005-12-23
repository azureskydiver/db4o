/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

    internal class ConsoleWriter : PrintStream {

        public override void println() {
            Console.WriteLine();
        }

        public override void println(Object obj) {
            Console.WriteLine(obj);
        }

        public override void print(Object obj) {
            Console.WriteLine(obj);
        }
    }

}
