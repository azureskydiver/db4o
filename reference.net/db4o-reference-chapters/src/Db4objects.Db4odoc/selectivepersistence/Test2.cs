using System;
using System.Collections.Generic;
using System.Text;

namespace Db4objects.Db4odoc.selectivepersistence
{
    class Test2
    {
        private Test1 test1;
        private string name;
        private NotStorable transientClass;


        public Test2(string name, NotStorable transientClass, Test1 test1)
        {
            this.test1 = test1;
            this.name = name;
            this.transientClass = transientClass;
        }

        public override string ToString()
        {
            if (transientClass == null)
            {
                return string.Format("{0}/{1}; test1: {2}", name, "null", test1);
            }
            else
            {
                return string.Format("{0}/{1}; test1: {2}", name, transientClass, test1);
            }
        }
    }
}
