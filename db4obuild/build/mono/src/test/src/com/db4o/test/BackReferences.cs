/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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