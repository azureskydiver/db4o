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
    
        public void store(){
            File.Delete(SECOND_FILE);
            Tester.store(new BasicClusterTest("inOne"));
            Tester.store(new BasicClusterTest("inBoth"));
            ObjectContainer second = Db4o.openFile(SECOND_FILE);
            second.set(new BasicClusterTest("inBoth"));
            second.set(new BasicClusterTest("inTwo"));
            second.close();
        }
    
        public void test(){
            ObjectContainer second = Db4o.openFile(SECOND_FILE);
            Cluster cluster = new Cluster(new ObjectContainer[]{
                Tester.objectContainer(),
                second
            });
            tQuery(cluster, "inOne", 1);
            tQuery(cluster, "inTwo", 1);
            tQuery(cluster, "inBoth", 2);
            second.close();
        }
    
        private void tQuery(Cluster cluster, String name, int expected){
            Query q = cluster.query();
            q.constrain(this.GetType());
            q.descend("_name").constrain(name);
            System.Collections.IList list = q.execute();
            Tester.ensure(list.Count == expected);
            foreach( BasicClusterTest bct in list){
                Tester.ensure(bct._name.Equals(name));
            }

        }

	}
}
