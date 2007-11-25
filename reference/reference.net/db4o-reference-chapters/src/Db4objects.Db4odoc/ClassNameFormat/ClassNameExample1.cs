/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.ClassNameFormat
{
    class ClassNameExample1
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args) 
        {
            StoreObjects();
        }
        // end Main


        private static void StoreObjects()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
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
