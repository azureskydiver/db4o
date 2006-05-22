/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;
using com.db4o.test.types;

namespace com.db4o.test {
    public class Book {
        public Person[] authors;
        public String title;
    
        public Book(){}
    
        public Book(String title, Person[] authors){
            this.title = title;
            this.authors = authors;
        }

        public void Store(){
            Person aaron = new Person("Aaron", "OneOK");
            Person bill = new Person("Bill", "TwoOK");
            Person chris = new Person("Chris", "ThreeOK");
            Person dave = new Person("Dave", "FourOK");
            Person neil = new Person("Neil", "Notwanted");
            Person nat = new Person("Nat", "Neverwanted");
            Tester.Store(new Book("Persistence possibilities", new Person[] { aaron, bill,chris}));
            Tester.Store(new Book("Persistence using S.O.D.A.", new Person[]{aaron}));
            Tester.Store(new Book("Persistence using JDO", new Person[]{bill, dave}));
            Tester.Store(new Book("Don't want to find Phil", new Person[]{aaron, bill, neil}));
            Tester.Store(new Book("Persistence by Jeff", new Person[]{nat}));
        }

        public void Test(){
            Query qBooks = Tester.Query();
            qBooks.Constrain(typeof(Book));
            qBooks.Descend("title").Constrain("Persistence").Like();
            Query qAuthors = qBooks.Descend("authors");
            Query qFirstName = qAuthors.Descend("firstName");
            Query qLastName = qAuthors.Descend("lastName");
            Constraint cAaron =
                qFirstName.Constrain("Aaron").And(
                qLastName.Constrain("OneOK")
                );
            Constraint cBill =
                qFirstName.Constrain("Bill").And(
                qLastName.Constrain("TwoOK")
                );
            cAaron.Or(cBill);
            ObjectSet results = qAuthors.Execute();
            Tester.Ensure(results.Size() == 4);
            while(results.HasNext()){
                Person person = (Person)results.Next();
                Tester.Ensure(person.lastName.EndsWith("OK"));
            }
        }

    
        public override String ToString(){
            String ret = title;
            if(authors != null){
                for (int i = 0; i < authors.Length; i++) {
                    ret += "\n  " + authors[i].ToString(); 
                }
            }
            return ret;
        }
    }
}
