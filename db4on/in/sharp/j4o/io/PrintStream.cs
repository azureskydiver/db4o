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

        public virtual void print(Object obj) {
            textWriter.Write(obj);
        }

        public virtual void println() {
            textWriter.WriteLine();
        }

        public virtual void println(Object obj) {
            textWriter.WriteLine(obj);
        }
    }
}
