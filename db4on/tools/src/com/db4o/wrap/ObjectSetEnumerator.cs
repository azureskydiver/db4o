/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.ext;

namespace com.db4o.wrap
{

    /// <summary>
    /// ObjectSet wrapper to allow using an ObjectSet as an IEnumerator
    /// </summary>
    public class ObjectSetEnumerator : IEnumerator{

        public int activationDepth = 5;
        private ExtObjectContainer i_objectContainer;
        private int i_current = -1;
        private long[] i_ids;

        public ObjectSetEnumerator(ObjectContainer objectContainer, ObjectSet objectSet){
            i_objectContainer = objectContainer.ext();
            i_ids = objectSet.ext().getIDs();
        }

        private Object activatedObject(int a_index) {
            Object obj = i_objectContainer.getByID(i_ids[a_index]);
            i_objectContainer.activate(obj, activationDepth);
            return obj;
        }

        public Object Current {
            get{
                return activatedObject(i_current);
            }
        }

        public bool MoveNext(){
            i_current++;
            return i_current >= 0 && i_current < i_ids.Length;
        }

        public void Reset(){
            i_current = -1;
        }
    }
}
