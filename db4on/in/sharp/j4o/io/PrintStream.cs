/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

    public class PrintStream {

        private TextWriter textWriter;

        public PrintStream(TextWriter textWriter) {
            this.textWriter = textWriter;
        }

        protected PrintStream() {
        }

        public virtual void Print(Object obj) {
            textWriter.Write(obj);
        }

        public virtual void Println() {
            textWriter.WriteLine();
        }

        public virtual void Println(Object obj) {
            textWriter.WriteLine(obj);
        }
    }
}
