/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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