/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.VersionUpdate
{
    class UpdateExample
    {
        public static void Main(string[] args)
        {
            Db4oFactory.Configure().AllowVersionUpdates(true);
            IObjectContainer objectContainer = Db4oFactory.OpenFile(args[0]);
            objectContainer.Close();
            System.Console.WriteLine("The database is ready for the version " + Db4o.Db4oVersion.Name);
        }
    }
}
