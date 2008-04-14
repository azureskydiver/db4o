using System;
using System.Collections;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Types;

namespace Db4objects.Db4odoc.Lists
{
    class MapExample
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            File.Delete(Db4oFileName);
            IObjectContainer objectContainer = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                MyClass myObject = new MyClass();
                myObject.dict = objectContainer.Ext().Collections().NewIdentityHashMap(0);
            }
            finally
            {
                objectContainer.Close();
            }
        }
    }

    public class MyClass{
            public IDictionary  dict;
            }

}
