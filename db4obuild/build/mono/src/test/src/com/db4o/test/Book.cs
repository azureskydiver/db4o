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

        public void store(){
            Person aaron = new Person("Aaron", "OneOK");
            Person bill = new Person("Bill", "TwoOK");
            Person chris = new Person("Chris", "ThreeOK");
            Person dave = new Person("Dave", "FourOK");
            Person neil = new Person("Neil", "Notwanted");
            Person nat = new Person("Nat", "Neverwanted");
            Test.store(new Book("Persistence possibilities", new Person[] { aaron, bill,chris}));
            Test.store(new Book("Persistence using S.O.D.A.", new Person[]{aaron}));
            Test.store(new Book("Persistence using JDO", new Person[]{bill, dave}));
            Test.store(new Book("Don't want to find Phil", new Person[]{aaron, bill, neil}));
            Test.store(new Book("Persistence by Jeff", new Person[]{nat}));
        }

        public void test(){
            Query qBooks = Test.query();
            qBooks.constrain(typeof(Book));
            qBooks.descend("title").constrain("Persistence").like();
            Query qAuthors = qBooks.descend("authors");
            Query qFirstName = qAuthors.descend("firstName");
            Query qLastName = qAuthors.descend("lastName");
            Constraint cAaron =
                qFirstName.constrain("Aaron").and(
                qLastName.constrain("OneOK")
                );
            Constraint cBill =
                qFirstName.constrain("Bill").and(
                qLastName.constrain("TwoOK")
                );
            cAaron.or(cBill);
            ObjectSet results = qAuthors.execute();
            Test.ensure(results.size() == 4);
            while(results.hasNext()){
                Person person = (Person)results.next();
                Test.ensure(person.lastName.EndsWith("OK"));
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
