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
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4o.test {
    public class NoInstanceStored {

        public String name;

        public void test(){
            Query q = Test.query();
            q.constrain(typeof(NoInstanceStored));
            q.descend("name").constrain("hi");
            ObjectSet objectSet = q.execute();
            Test.ensure(objectSet.size() == 0);
        }

        public static void Main(String[] args) {
            ObjectContainer objectContainer = Db4o.openFile("nis.yap");
            objectContainer.set(new Atom("jj"));
            objectContainer.close();
            objectContainer = Db4o.openFile("nis.yap");
            Query q = objectContainer.query();
            q.constrain(typeof(NoInstanceStored));
            q.descend("name").constrain("hi");
            ObjectSet objectSet = q.execute();
            Console.WriteLine(objectSet.size());
            objectContainer.close();
        }
    }
}
