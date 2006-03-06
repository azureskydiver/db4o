using System;
using System.Collections.Generic;
using com.db4o;
using com.db4o.ext;

namespace com.db4o.test.nativequeries
{
#if NET_2_0

    public class ListElementByIdentity
    {

        IList<LebiElement> _list;
        
        public void store()
        {
            store("1");
            store("2");
            store("3");
            store("4");
        }

        public void test()
        {
            ExtObjectContainer oc = Tester.objectContainer();

            LebiElement elem = (LebiElement)oc.get(new LebiElement("23"))[0];

            IList <ListElementByIdentity> res = oc.query<ListElementByIdentity>(delegate(ListElementByIdentity lebi)
            {
                return lebi._list.Contains(elem);
            });

            Tester.ensure(res.Count == 1);
            Tester.ensure(res[0]._list[3]._name.Equals("23"));

        }

        private void store(string prefix)
        {
            ListElementByIdentity lebi = new ListElementByIdentity();
            lebi.createListElements(prefix);
            Tester.store(lebi);
        }

        private void createListElements(string prefix)
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
