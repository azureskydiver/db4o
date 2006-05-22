/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o;
using com.db4o.query;
using com.db4o.ext;

namespace com.db4o.test {
    public class TwoClients : AllTestsConfAll {
	
        public void Test(){
            if(Tester.clientServer){
                Tester.DeleteAllInstances(new Atom());
                Tester.Commit();

                ExtObjectContainer client2 = null;
                try {
                    client2 =
                        Db4o.OpenClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).Ext();
                
                    // client2 = Tester.Server().OpenClient().Ext();
                } catch (Exception e) {
                    Console.WriteLine(e);
                    return ;
                }
                ExtObjectContainer client1 = Tester.ObjectContainer();
                Atom a_1_1 = new Atom("One");
                Atom a_1_2 = new Atom("Two");
                Atom a_1_3 = new Atom("Three");
                client1.Set(a_1_1);
                client1.Set(a_1_2);
                client1.Set(a_1_3);
                EnsureAtomCount(client2,null, 0);
				
				Tester.CommitSync(client1, client2);
				
                EnsureAtomCount(client2,null, 3);
                Atom a_2_1 = (Atom)client2.Get(new Atom("One")).Next();
                a_1_1.child = new Atom("OneChild");
                client1.Set(a_1_1);
                EnsureAtomCount(client2,null, 3);
                Tester.CommitSync(client1, client2);
                EnsureAtomCount(client2,null, 4);
                client2.Deactivate(a_2_1, int.MaxValue);
                client2.Activate(a_2_1, int.MaxValue);
                Tester.Ensure(a_2_1.child.name.Equals("OneChild"));
                a_2_1.name = "Zulu";
                client2.Set(a_2_1);
            
                Atom a_1_4 = new Atom("Zorro");
                client1.Set(a_1_4);
                Atom a_1_5 = new Atom("Zzerk");
                client1.Set(a_1_5);
            
                EnsureAtomCount(client1, "Zulu", 0);
            
                Tester.CommitSync(client2, client1);
            
                EnsureAtomCount(client1, "Zulu", 1);

            
                Query q = client1.Query();
                q.Constrain(typeof(Atom));
                    q.Descend("name").Constrain("Zulu");
                ObjectSet os = q.Execute();
                Atom q_1_1 = (Atom)os.Next();
            
                Tester.Ensure(a_1_1 == q_1_1);
                a_1_1.name = "Bozo";
                client1.Set(a_1_1);
                a_1_1.child.name = "BozoChild";
                client1.Set(a_1_1.child);
                a_1_4.name = "Bozo";
                client1.Set(a_1_4);
                a_1_5.name = "Cue";
                client1.Set(a_1_5);
            
                client2.Refresh(a_2_1, int.MaxValue);
                Tester.Ensure(a_2_1.name.Equals("Zulu"));
                Tester.Ensure(a_2_1.child.name.Equals("OneChild"));
                EnsureAtomCount(client2, "Bozo", 0);
            
				Tester.CommitSync(client1, client2);
				
                client2.Refresh(a_2_1, int.MaxValue);
                Tester.Ensure(a_2_1.name.Equals("Bozo"));
                Tester.Ensure(a_2_1.child.name.Equals("BozoChild"));
                EnsureAtomCount(client2, "Bozo", 2);
                EnsureAtomCount(client2, "Cue", 1);
                EnsureAtomCount(client2, "BozoChild", 1);
            
                client2.Close();
            }
        }
	
        private void EnsureAtomCount(ObjectContainer con, String name, int count){
		
			Query q = con.Query();
			q.Constrain(typeof(Atom));
			if(name != null){
				q.Descend("name").Constrain(name);
			}
			if(q.Execute().Size() == count){
				Tester.assertionCount ++;
				return;
			}
            Tester.Error();
        }
    }
}
