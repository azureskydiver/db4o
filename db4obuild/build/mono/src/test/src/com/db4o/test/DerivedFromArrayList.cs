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

    public class DerivedFromArrayList : ArrayList {
        
        public void storeOne(){
            Add("One");
            Add("Two");
        }

        public void test(){
            Query q = Test.query();
            q.constrain(typeof(DerivedFromArrayList));
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()){
                DerivedFromArrayList dal = (DerivedFromArrayList)objectSet.next();
                dal.Add("Three");
                Test.store(dal);
            }
            Test.reOpen();
            q = Test.query();
            q.constrain(typeof(DerivedFromArrayList));
            objectSet = q.execute();
            while(objectSet.hasNext()){
                DerivedFromArrayList dal = (DerivedFromArrayList)objectSet.next();
                Test.ensure(dal.Count > 2);
            }
        }
    }
}
