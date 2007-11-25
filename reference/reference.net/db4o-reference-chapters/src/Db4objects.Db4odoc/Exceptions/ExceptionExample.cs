/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.Exceptions
{
    class ExceptionExample
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            IObjectContainer db = OpenDatabase();
            db.Close();
            OpenClient();
            Work();
        }
        // end Main

        private static IObjectContainer OpenDatabase()
        {
            IObjectContainer db = null;
            try
            {
                db = Db4oFactory.OpenFile(Db4oFileName);
            }
            catch (DatabaseFileLockedException ex)
            {
                //System.Console.WriteLine(ex.Message);
                // ask the user for a new filename, print
                // or log the exception message
                // and close the application,
                // find and fix the reason
                // and try again
            }
            return db;
        }
        // end OpenDatabase

        private static IObjectContainer OpenClient()
        {
            IObjectContainer db = null;
            try
            {
                db = Db4oFactory.OpenClient("host", 0xdb40, "user", "password");
            }
            catch (Exception ex)
            {
                //System.Console.WriteLine(ex.Message);
                // ask the user for new connection details, print
                // or log the exception message
                // and close the application,
                // find and fix the reason
                // and try again
            }
            return db;
        }
        // end OpenClient

        private static void Work()
        {
            IObjectContainer db = OpenDatabase();
            try
            {
                // do some work with db4o
                db.Commit();
            }
            catch (Db4oException ex)
            {
                // handle exception ....
            }
            catch (Exception ex)
            {
                // handle exception ....
            }
            finally
            {
                db.Close();
            }
        }
        // end Work

    }
}
