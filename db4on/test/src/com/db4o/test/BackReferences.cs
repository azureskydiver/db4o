/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using com.db4o.query;

namespace com.db4o.test {


    public class BackReferences {

        public static void Main(String[] args) {
            ObjectContainer objectContainer = Db4o.OpenFile("BackReferences.yap");
            BR_Person person= new BR_Person();
            BR_Address address=new BR_Address();
            address.ID="Test1";
            person.AddAddress(address);
            objectContainer.Set(person);
            objectContainer.Commit();
            Query q = objectContainer.Query();
            q.Constrain(typeof(BR_Person));
            ObjectSet objectSet = q.Execute();
            while(objectSet.HasNext()){
                person = (BR_Person)objectSet.Next();
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
            objectContainer.Close();
        }

        public void Store(){
            BR_Person person= new BR_Person();
            BR_Address address=new BR_Address();
            address.ID="Test1";
            person.AddAddress(address);
            Tester.Store(person);
        }

        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(BR_Person));
            ObjectSet objectSet = q.Execute();
            while(objectSet.HasNext()){
                BR_Person person = (BR_Person)objectSet.Next();
                BR_Address address = (BR_Address)person.addresses["Test1"];
                Tester.Ensure(address.ID.Equals("Test1"));
                Tester.Ensure(address.Owner == person);
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
