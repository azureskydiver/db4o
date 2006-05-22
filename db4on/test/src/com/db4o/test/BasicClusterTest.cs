using System;
using System.IO;

using com.db4o;
using com.db4o.query;
using com.db4o.cluster;

namespace com.db4o.test
{
	/// <summary>
	/// Summary description for BasicClusterTest.
	/// </summary>
	public class BasicClusterTest
	{
        public String _name;
    
        public static String SECOND_FILE = "second.yap";
    
        public BasicClusterTest(){
        }
    
        public BasicClusterTest(String name){
            _name = name;
        }
    
        public void Store(){
            File.Delete(SECOND_FILE);
            Tester.Store(new BasicClusterTest("inOne"));
            Tester.Store(new BasicClusterTest("inBoth"));
            ObjectContainer second = Db4o.OpenFile(SECOND_FILE);
            second.Set(new BasicClusterTest("inBoth"));
            second.Set(new BasicClusterTest("inTwo"));
            second.Close();
        }
    
        public void Test(){
            ObjectContainer second = Db4o.OpenFile(SECOND_FILE);
            Cluster cluster = new Cluster(new ObjectContainer[]{
                Tester.ObjectContainer(),
                second
            });
            TQuery(cluster, "inOne", 1);
            TQuery(cluster, "inTwo", 1);
            TQuery(cluster, "inBoth", 2);
            second.Close();
        }
    
        private void TQuery(Cluster cluster, String name, int expected){
            Query q = cluster.Query();
            q.Constrain(this.GetType());
            q.Descend("_name").Constrain(name);
            System.Collections.IList list = q.Execute();
            Tester.Ensure(list.Count == expected);
            foreach( BasicClusterTest bct in list){
                Tester.Ensure(bct._name.Equals(name));
            }

        }

	}
}
