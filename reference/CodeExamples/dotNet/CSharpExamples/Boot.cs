
using System;
using System.Collections.Generic;
using Db4oDoc.Code.Tp.Enhancement;
using Db4objects.Db4o;
using Db4oDoc.Code.Basics;
using Db4oDoc.Code.Tp.Rollback;
using Db4oDoc.Code.Validation;
using Db4oDoc.Code.xml;

namespace Db4oDoc
{
    public class Boot
    {
        public static void Main(string[] args)
        {
            TransparentPersistence.Main(args);
        }
    }
}