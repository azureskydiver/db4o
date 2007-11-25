/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.SelectivePersistence
{
    class Test1
    {
        private string name;
        private NotStorable transientClass;

        public Test1(string name, NotStorable transientClass)
        {
            this.name = name;
            this.transientClass = transientClass;
        }

        public override string ToString()
        {
            if (transientClass == null)
            {
                return string.Format("{0}/{1}", name, "null");
            }
            else
            {
                return string.Format("{0}/{1}", name, transientClass);
            }
        }
    }
}
