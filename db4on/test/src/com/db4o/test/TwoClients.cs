/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o;
using com.db4o.query;
using com.db4o.ext;

namespace com.db4o.test {
    public class TwoClients : AllTestsConfAll {
	
        public void test(){
            if(Test.clientServer){
                Test.deleteAllInstances(new Atom());
                Test.commit();

                ExtObjectContainer client2 = null;
                try {
                    client2 =
                        Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
                
                    // client2 = Test.server().openClient().ext();
                } catch (Exception e) {
                    Console.WriteLine(e);
                    return ;
                }
                ExtObjectContainer client1 = Test.objectContainer();
                Atom a_1_1 = new Atom("One");
                Atom a_1_2 = new Atom("Two");
                Atom a_1_3 = new Atom("Three");
                client1.set(a_1_1);
                client1.set(a_1_2);
                client1.set(a_1_3);
                ensureAtomCount(client2,null, 0);
				
				Test.commitSync(client1, client2);
				
                ensureAtomCount(client2,null, 3);
                Atom a_2_1 = (Atom)client2.get(new Atom("One")).next();
                a_1_1.child = new Atom("OneChild");
                client1.set(a_1_1);
                ensureAtomCount(client2,null, 3);
                Test.commitSync(client1, client2);
                ensureAtomCount(client2,null, 4);
                client2.deactivate(a_2_1, int.MaxValue);
                client2.activate(a_2_1, int.MaxValue);
                Test.ensure(a_2_1.child.name.Equals("OneChild"));
                a_2_1.name = "Zulu";
                client2.set(a_2_1);
            
                Atom a_1_4 = new Atom("Zorro");
                client1.set(a_1_4);
                Atom a_1_5 = new Atom("Zzerk");
                client1.set(a_1_5);
            
                ensureAtomCount(client1, "Zulu", 0);
            
                Test.commitSync(client2, client1);
            
                ensureAtomCount(client1, "Zulu", 1);

            
                Query q = client1.query();
                q.constrain(typeof(Atom));
                    q.descend("name").constrain("Zulu");
                ObjectSet os = q.execute();
                Atom q_1_1 = (Atom)os.next();
            
                Test.ensure(a_1_1 == q_1_1);
                a_1_1.name = "Bozo";
                client1.set(a_1_1);
                a_1_1.child.name = "BozoChild";
                client1.set(a_1_1.child);
                a_1_4.name = "Bozo";
                client1.set(a_1_4);
                a_1_5.name = "Cue";
                client1.set(a_1_5);
            
                client2.refresh(a_2_1, int.MaxValue);
                Test.ensure(a_2_1.name.Equals("Zulu"));
                Test.ensure(a_2_1.child.name.Equals("OneChild"));
                ensureAtomCount(client2, "Bozo", 0);
            
				Test.commitSync(client1, client2);
				
                client2.refresh(a_2_1, int.MaxValue);
                Test.ensure(a_2_1.name.Equals("Bozo"));
                Test.ensure(a_2_1.child.name.Equals("BozoChild"));
                ensureAtomCount(client2, "Bozo", 2);
                ensureAtomCount(client2, "Cue", 1);
                ensureAtomCount(client2, "BozoChild", 1);
            
                client2.close();
            }
        }
	
        private void ensureAtomCount(ObjectContainer con, String name, int count){
		
			Query q = con.query();
			q.constrain(typeof(Atom));
			if(name != null){
				q.descend("name").constrain(name);
			}
			if(q.execute().size() == count){
				Test.assertionCount ++;
				return;
			}
            Test.error();
        }
    }
}
