/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
using System;
using com.db4o.ext;

namespace com.db4o {

    internal class YapReferenceQueue{

        private List4 list;

        internal void add(YapRef reference) {
            lock(this){
                list = new List4(list, reference);
            }
        }

        internal void poll(ExtObjectContainer objectContainer) {
            List4 remove = null;
            lock(this){
                Iterator4 i = new Iterator4(list);
                list = null;
                while(i.hasNext()){
                    YapRef yapRef = (YapRef)i.next();
                    if(yapRef.IsAlive){
                        list = new List4(list, yapRef);
                    }else{
                        remove = new List4(remove, yapRef.yapObject);
                    }
                }
            }
            Iterator4 j = new Iterator4(remove);
            while(j.hasNext() && (!objectContainer.isClosed())){
                objectContainer.purge(j.next());
            }
        }
    }
}