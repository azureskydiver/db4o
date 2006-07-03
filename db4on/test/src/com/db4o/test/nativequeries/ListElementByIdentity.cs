namespace com.db4o.test.nativequeries
{
#if NET_2_0
	using System;
	using System.Collections.Generic;
	using com.db4o;
	using com.db4o.ext;
    public class ListElementByIdentity
    {

        IList<LebiElement> _list;
        
        public void Store()
        {
            Store("1");
            Store("2");
            Store("3");
            Store("4");
        }

        public void Test()
        {
            ExtObjectContainer oc = Tester.ObjectContainer();

            LebiElement elem = (LebiElement)oc.Get(new LebiElement("23"))[0];

            IList <ListElementByIdentity> res = oc.Query<ListElementByIdentity>(delegate(ListElementByIdentity lebi)
            {
                return lebi._list.Contains(elem);
            });

            Tester.Ensure(res.Count == 1);
            Tester.Ensure(res[0]._list[3]._name.Equals("23"));

        }

        private void Store(string prefix)
        {
            ListElementByIdentity lebi = new ListElementByIdentity();
            lebi.CreateListElements(prefix);
            Tester.Store(lebi);
        }

        private void CreateListElements(string prefix)
        {
            _list = new List<LebiElement>();
            _list.Add(new LebiElement(prefix + "0"));
            _list.Add(new LebiElement(prefix + "1"));
            _list.Add(new LebiElement(prefix + "2"));
            _list.Add(new LebiElement(prefix + "3"));
        }


    }

    public class LebiElement {

        public string _name;

        public LebiElement (string name){
            _name = name;
        }
    }
#endif

}
