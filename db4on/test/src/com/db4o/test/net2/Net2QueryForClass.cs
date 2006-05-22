namespace com.db4o.test.net2
{
#if NET_2_0 || CF_2_0
    using System;
    using System.Collections.Generic;
    using System.Text;
    using com.db4o;
    using com.db4o.test;
    using com.db4o.query;

    public class Net2QueryForClass
    {
        public string _name;

        public Net2QueryForClass()
        {

        }

        public Net2QueryForClass(string name)
        {
            _name = name;
        }

        public void StoreOne()
        {
            _name = "one";
        }

        public void Test(){
            ObjectContainer oc = Tester.ObjectContainer();
            IList <Net2QueryForClass> list = oc.Query<Net2QueryForClass>(typeof(Net2QueryForClass));
            foreach (Net2QueryForClass res in list)
            {
                Tester.Ensure(res._name.Equals("one"));
            }
    }






    }
#endif
}
