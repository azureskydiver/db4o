/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

    public class FileInputStream : InputStream {

        public FileInputStream(File file) : base(new FileStream(file.getPath(), FileMode.Open, FileAccess.Read)) {
        }

    }
}
