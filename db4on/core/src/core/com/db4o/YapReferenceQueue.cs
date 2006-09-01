/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using com.db4o.ext;
using com.db4o.foundation;

namespace com.db4o {

    internal class YapReferenceQueue{

        private List4 list;

        internal void Add(YapRef reference) {
            lock(this){
                list = new List4(list, reference);
            }
        }

        internal void Poll(ExtObjectContainer objectContainer) {
            List4 remove = null;
            lock(this){
                Iterator4 i = new Iterator4Impl(list);
                list = null;
                while(i.MoveNext()){
                    YapRef yapRef = (YapRef)i.Current();
                    if(yapRef.IsAlive){
                        list = new List4(list, yapRef);
                    }else{
                        remove = new List4(remove, yapRef.yapObject);
                    }
                }
            }
            Iterator4 j = new Iterator4Impl(remove);
            while(j.MoveNext() && (!objectContainer.IsClosed())){
                objectContainer.Purge(j.Current());
            }
        }
    }
}