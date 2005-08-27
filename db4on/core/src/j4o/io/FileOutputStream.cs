
/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

    public class FileOutputStream : OutputStream {

        public FileOutputStream(File file) : base(new FileStream(file.getPath(), FileMode.Create, FileAccess.Write)) {
        }

    }
}
