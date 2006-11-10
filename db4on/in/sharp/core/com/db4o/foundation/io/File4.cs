/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
using System.IO;

namespace com.db4o.foundation.io
{
    public class File4
    {
        public static void Delete(string file)
        {
            if (File.Exists(file))
            {
                File.Delete(file);
            }
        }

        public static void Copy(string from, string to)
        {
            File.Copy(from, to, true);
        }
    }
}