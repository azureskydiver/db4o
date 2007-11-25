/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o.Types;

namespace Db4ojects.Db4odoc.TAExamples
{
    public class Image
    {
        IBlob _blob = null;
        private string _fileName = null;

        public Image(string fileName)
        {
            _fileName = fileName;
        }

        // Image recording and reading functionality to be implemented ...
    }
}
