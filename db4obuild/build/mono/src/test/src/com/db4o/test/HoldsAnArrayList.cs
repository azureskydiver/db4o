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

    public class HoldsAnArrayList {

        public ArrayList something = new ArrayList();

        public HoldsAnArrayList (){
            something.AddRange(new char[] {'1', '2'});
        }

        public void configure(){

            // Both of the following configuration settings work.
            // They can be used alternatively.

            Db4o.configure().updateDepth(3);

            // Db4o.configure().objectClass(typeof(HoldsAnArrayList)).cascadeOnUpdate(true);
        }

        public void store(){
            Test.store(new HoldsAnArrayList());
        }

        public void test(){
            Query q = Test.query();
            q.constrain(typeof(HoldsAnArrayList));
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()){
                HoldsAnArrayList obj = (HoldsAnArrayList)objectSet.next();
                obj.something.Add('3');
                Test.store(obj);

                Test.reOpen();
                q = Test.query();
                q.constrain(typeof(HoldsAnArrayList));
                objectSet = q.execute();
                while(objectSet.hasNext()){
                    HoldsAnArrayList haal = (HoldsAnArrayList)objectSet.next();
                    Test.ensure(haal.something.Count > 2);
                }

            }
        }
    }
}