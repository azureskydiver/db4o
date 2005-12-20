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


    public class Net2GenericList
    {


        public void store()
        {
            LHolder1 lh1 = new LHolder1();
            lh1._name = "root";
            lh1.CreateLists();
            lh1.CreateLinkList(8);

            LHolder2 lh2 = new LHolder2();
            lh2.nList1 = lh1.nList1;
            lh2.nList2 = lh1.nList2;
            lh2.gList1 = lh1.gList1;
            lh2.gList2 = lh1.gList2;

            LHolder1 lh3 = new LHolder1();
            lh3.CreateLists();
            lh3._name = "update";

            Tester.store(lh1);
            Tester.store(lh2);
            Tester.store(lh3);
        }


        public void test()
        {
            LHolder1 root = QueryForNamedHolder("root");
            root.CheckListsBeforeUpdate();

            LHolder1 updateHolder = QueryForNamedHolder("update");
            updateHolder.UpdateLists();
            updateHolder.StoreLists(Tester.objectContainer());

            Tester.reOpen();

            updateHolder = QueryForNamedHolder("update");
            updateHolder.CheckListsAfterUpdate();

            Query q = Tester.objectContainer().query();
            q.constrain(typeof(LHolder1));
            q.descend("nList1").descend("_name").constrain("update");
            ObjectSet objectSet = q.execute();
            Tester.ensure(objectSet.size() == 1);
            LHolder1 lh1 = (LHolder1 )objectSet.next();
            Tester.ensure(updateHolder == lh1);


        }

        private LHolder1 QueryForNamedHolder(string name)
        {
            IList<LHolder1> holderList = Tester.objectContainer().query<LHolder1>(delegate(LHolder1 holder)
            {
                return holder._name == name;
            });
            return holderList[0];
        }


    }

    public class LHolder1
    {
        public string _name;

        public IList nList1;
        public IList nList2;

        public IList<LItem1> gList1;
        public IList<LItem2> gList2;

        public LHolder1 _next;

        public void CreateLinkList(int length)
        {
            if (length < 1)
            {
                return;
            }
            _next = new LHolder1();
            _next._name = "Linked lHolder1 " + length;
            _next.CreateLists();
            _next.CreateLinkList(length - 1);
        }

        public void UpdateLists()
        {
            nList1.Add(new LItem1("update"));
            nList2.Add(new LItem2("update"));
            gList1.Add(new LItem1("update"));
            gList2.Add(new LItem2("update"));
        }

        public void CreateLists()
        {
            nList1 = new List<LItem1>();
            nList2 = new List<LItem2>();
            gList1 = new List<LItem1>();
            gList2 = new List<LItem2>();

            nList1.Add(new LItem1("n11"));
            nList1.Add(new LItem1("n12"));

            nList2.Add(new LItem2("n21"));
            nList2.Add(new LItem2("n22"));
            nList2.Add(new LItem2("n23"));

            gList1.Add(new LItem1("g11"));
            gList1.Add(new LItem1("g12"));
            gList1.Add(new LItem1("g13"));

            gList2.Add(new LItem2("g21"));
            gList2.Add(new LItem2("g22"));
        }

        public void StoreLists(ObjectContainer oc)
        {
            oc.set(nList1);
            oc.set(nList2);
            oc.set(gList1);
            oc.set(gList2);
        }

        public void CheckListsBeforeUpdate()
        {
            CheckList(nList1, new object[] { new LItem1("n11"), new LItem1("n12") });
            CheckList(nList2, new object[] { new LItem2("n21"), new LItem2("n22"), new LItem2("n23") });
            CheckList((IList)gList1, new object[] { new LItem1("g11"), new LItem1("g12"), new LItem1("g13") });
            CheckList((IList)gList2, new object[] { new LItem2("g21"), new LItem2("g22") });
        }

        public void CheckListsAfterUpdate()
        {
            CheckList(nList1, new object[] { new LItem1("n11"), new LItem1("n12"), new LItem1("update") });
            CheckList(nList2, new object[] { new LItem2("n21"), new LItem2("n22"), new LItem2("n23"), new LItem2("update") });
            CheckList((IList)gList1, new object[] { new LItem1("g11"), new LItem1("g12"), new LItem1("g13"), new LItem1("update") });
            CheckList((IList)gList2, new object[] { new LItem2("g21"), new LItem2("g22"), new LItem2("update") });
        }


        private void CheckList(IList list, object[] expectedContent)
        {
            Tester.ensure(list.Count == expectedContent.Length);
            for (int i = 0; i < expectedContent.Length; i++) 
            { 
                Tester.ensure(list[i].Equals(expectedContent[i]));
            }
        }

    }

    public class LHolder2
    {
        public IList nList1;
        public IList nList2;

        public IList<LItem1> gList1;
        public IList<LItem2> gList2;
    }


    public class LItem1
    {
        string _name;

        public LItem1()
        {
        }

        public LItem1(string name)
        {
            _name = name;
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            LItem1 other = obj as LItem1;

            if (other == null)
            {
                return false;
            }

            return _name.Equals(other._name);
        }
    }

    public class LItem2
    {
        string _name;

        public LItem2()
        {
        }

        public LItem2(string name)
        {
            _name = name;
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            LItem2 other = obj as LItem2;

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

#endif
}
