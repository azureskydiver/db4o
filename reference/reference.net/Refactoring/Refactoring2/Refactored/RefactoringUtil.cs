/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4odoc.Refactoring.Initial;

namespace Db4objects.Db4odoc.Refactoring.Refactored
{
    class RefactoringUtil
    {

        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            MoveValues();
        }

        public static void MoveValues()
        {
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                IObjectSet result = container.Get(new C());
                for (int i = 0; i < result.Count; i++)
                {
                    C c = (C)result[i];
                    E e = new E();
                    e.name = c.name;
                    e.number = c.number;
                    container.Delete(c);
                    container.Set(e);
                }

            }
            finally
            {
                container.Close();
                System.Console.WriteLine("Done");
            }
        }
        // end moveValues


    }

}
