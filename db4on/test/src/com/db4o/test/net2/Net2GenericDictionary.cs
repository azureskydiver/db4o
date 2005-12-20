using System;
using System.Collections.Generic;
using System.Text;

namespace com.db4o.test.net2
{
#if NET_2_0
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Text;
    using com.db4o;
    using com.db4o.test;
    using com.db4o.query;


    public class Net2GenericDictionary
    {


        public void store()
        {
            DHolder1 dh1 = new DHolder1();
            dh1._name = "root";
            dh1.CreateDicts();
            dh1.CreateLinkList(8);

            DHolder2 dh2 = new DHolder2();
            dh2.nDict1 = dh1.nDict1;
            dh2.nDict2 = dh1.nDict2;
            dh2.gDict1 = dh1.gDict1;
            dh2.gDict2 = dh1.gDict2;

            DHolder1 dh3 = new DHolder1();
            dh3.CreateDicts();
            dh3._name = "update";

            Tester.store(dh1);
            Tester.store(dh2);
            Tester.store(dh3);
        }


        public void test()
        {
            DHolder1 root = QueryForNamedHolder("root");
            root.CheckDictsBeforeUpdate();

            DHolder1 updateHolder = QueryForNamedHolder("update");
            updateHolder.UpdateDicts();
            updateHolder.StoreDicts(Tester.objectContainer());

            Tester.reOpen();

            updateHolder = QueryForNamedHolder("update");
            updateHolder.CheckDictsAfterUpdate();
        }

        private DHolder1 QueryForNamedHolder(string name)
        {
            IList<DHolder1> holderList = Tester.objectContainer().query<DHolder1>(delegate(DHolder1 holder)
            {
                return holder._name == name;
            });
            return holderList[0];
        }


    }

    public class DHolder1
    {
        public string _name;

        public IDictionary nDict1;
        public IDictionary nDict2;

        public IDictionary<DItem1,string> gDict1;
        public IDictionary<DItem2, string> gDict2;

        public DHolder1 _next;

        public void CreateLinkList(int length)
        {
            if (length < 1)
            {
                return;
            }
            _next = new DHolder1();
            _next._name = "Linked lHolder1 " + length;
            _next.CreateDicts();
            _next.CreateLinkList(length - 1);
        }

        public void UpdateDicts()
        {
            nDict1.Add(new DItem1("update"),"update");
            nDict2.Add(new DItem2("update"), "update");
            gDict1.Add(new DItem1("update"), "update");
            gDict2.Add(new DItem2("update"), "update");
        }

        public void CreateDicts()
        {
            nDict1 = new Dictionary<DItem1, string>();
            nDict2 = new Dictionary<DItem2, string>();
            gDict1 = new Dictionary<DItem1, string>();
            gDict2 = new Dictionary<DItem2, string>();

            nDict1.Add(new DItem1("n11"), "n11");
            nDict1.Add(new DItem1("n12"), "n12");

            nDict2.Add(new DItem2("n21"), "n21");
            nDict2.Add(new DItem2("n22"),"n22");
            nDict2.Add(new DItem2("n23"),"n23");

            gDict1.Add(new DItem1("g11"),"g11");
            gDict1.Add(new DItem1("g12"),"g12");
            gDict1.Add(new DItem1("g13"),"g13");

            gDict2.Add(new DItem2("g21"),"g21");
            gDict2.Add(new DItem2("g22"),"g22");
        }

        public void StoreDicts(ObjectContainer oc)
        {
            oc.set(nDict1);
            oc.set(nDict2);
            oc.set(gDict1);
            oc.set(gDict2);
        }

        public void CheckDictsBeforeUpdate()
        {
            CheckDict(nDict1, new object[] { new DItem1("n11"), new DItem1("n12") });
            CheckDict(nDict2, new object[] { new DItem2("n21"), new DItem2("n22"), new DItem2("n23") });
            CheckDict((IDictionary)gDict1, new object[] { new DItem1("g11"), new DItem1("g12"), new DItem1("g13") });
            CheckDict((IDictionary)gDict2, new object[] { new DItem2("g21"), new DItem2("g22") });
        }

        public void CheckDictsAfterUpdate()
        {
            CheckDict(nDict1, new object[] { new DItem1("n11"), new DItem1("n12"), new DItem1("update") });
            CheckDict(nDict2, new object[] { new DItem2("n21"), new DItem2("n22"), new DItem2("n23"), new DItem2("update") });
            CheckDict((IDictionary)gDict1, new object[] { new DItem1("g11"), new DItem1("g12"), new DItem1("g13"), new DItem1("update") });
            CheckDict((IDictionary)gDict2, new object[] { new DItem2("g21"), new DItem2("g22"), new DItem2("update") });
        }


        private void CheckDict(IDictionary dict, object[] expectedContent)
        {
            Tester.ensure(dict.Count == expectedContent.Length);
            for (int i = 0; i < expectedContent.Length; i++) 
            {
                Named named = expectedContent[i] as Named;
                String name = named.Name();
                string str = (string)dict[expectedContent[i]];
                Tester.ensure(str.Equals(name));
            }
        }

    }

    public class DHolder2
    {
        public IDictionary nDict1;
        public IDictionary nDict2;

        public IDictionary<DItem1, string> gDict1;
        public IDictionary<DItem2, string> gDict2;
    }


    public class DItem1 : Named
    {
        string _name;

        public DItem1()
        {
        }

        public DItem1(string name)
        {
            _name = name;
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            DItem1 other = obj as DItem1;

            if (other == null)
            {
                return false;
            }

            return _name.Equals(other._name);
        }

        public override int GetHashCode()
        {
            return _name.GetHashCode();
        }

        public string Name()
        {
            return _name;
        }

    }

    public class DItem2 :Named
    {
        string _name;

        public DItem2()
        {
        }

        public DItem2(string name)
        {
            _name = name;
        }

        public override int GetHashCode()
        {
            return _name.GetHashCode();
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            DItem2 other = obj as DItem2;

            if (other == null)
            {
                return false;
            }

            return _name.Equals(other._name);
        }

        public string Name()
        {
            return _name;
        }

    }

    public interface Named
    {
        string Name();
    }

#endif
}
