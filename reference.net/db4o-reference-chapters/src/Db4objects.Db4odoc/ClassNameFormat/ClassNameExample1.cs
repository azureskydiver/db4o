/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.ClassNameFormat
{
    class ClassNameExample1
    {
        public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args) 
        {
            StoreObjects();
        }
        // end Main


        public static void StoreObjects()
        {
            File.Delete(YapFileName);
            IObjectContainer container = Db4oFactory.OpenFile(YapFileName);
            try
            {
                // Store a simple class to the database
                Test test = new Test();
                container.Set(test);
            }
            finally
            {
                container.Commit();
            }
        }
        // end StoreObjects

    }
}
