/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using com.db4o.query;

namespace com.db4o.test {


    public class BackReferences {

        public static void Main(String[] args) {
            ObjectContainer objectContainer = Db4o.openFile("BackReferences.yap");
            BR_Person person= new BR_Person();
            BR_Address address=new BR_Address();
            address.ID="Test1";
            person.AddAddress(address);
            objectContainer.set(person);
            objectContainer.commit();
            Query q = objectContainer.query();
            q.constrain(typeof(BR_Person));
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()){
                person = (BR_Person)objectSet.next();
                address = (BR_Address)person.addresses["Test1"];
                if(address.ID.Equals("Test1")){
                    Console.WriteLine("OK Test1");
                }else{
                    throw new Exception();
                }
                if(address.Owner == person){
                    Console.WriteLine("OK BackReference");
                }else{
                    throw new Exception();
                }
            }
            objectContainer.close();
        }

        public void store(){
            BR_Person person= new BR_Person();
            BR_Address address=new BR_Address();
            address.ID="Test1";
            person.AddAddress(address);
            Test.store(person);
        }

        public void test(){
            Query q = Test.query();
            q.constrain(typeof(BR_Person));
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()){
                BR_Person person = (BR_Person)objectSet.next();
                BR_Address address = (BR_Address)person.addresses["Test1"];
                Test.ensure(address.ID.Equals("Test1"));
                Test.ensure(address.Owner == person);
            }
        }
    }

    public class BR_Person{

        public Hashtable addresses;

        public BR_Person(){
            addresses = new Hashtable();
        }

        public void AddAddress(BR_Address address){
            address.Owner = this;
            addresses.Add(address.ID, address);
        }
    }

    public class BR_Address{
        public BR_Person Owner;
        public string ID;
    }

}