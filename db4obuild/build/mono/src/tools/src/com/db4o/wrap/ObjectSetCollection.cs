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
using com.db4o.ext;

namespace com.db4o.wrap {

    /// <summary>
    /// ObjectSet wrapper to allow using an ObjectSet as ICollection and IList
    /// </summary>
    public class ObjectSetCollection: IList {

        public int activationDepth = 5;
        private ExtObjectContainer i_objectContainer;
        private ObjectSet i_objectSet;
        private long[] i_ids;

        public ObjectSetCollection(ObjectContainer objectContainer, ObjectSet objectSet) {
            i_objectContainer = objectContainer.ext();
            i_objectSet = objectSet;
            i_ids = objectSet.ext().getIDs();
        }

        private Object activatedObject(int a_index) {
            Object obj = i_objectContainer.getByID(i_ids[a_index]);
            i_objectContainer.activate(obj, activationDepth);
            return obj;
        }

        public int Add(Object obj){
            throw new NotSupportedException();
        }

        public void Clear(){
            throw new NotSupportedException();
        }

        public bool Contains(Object obj){
            return IndexOf(obj) >= 0;
        }

        public void CopyTo(Array arr, int pos){
            for (int i = 0; i < i_ids.Length; i++) {
                arr.SetValue(activatedObject(i), pos++);
            }
        }

        public int Count{
            get{
                return i_ids.Length;
            }
        }

        public IEnumerator GetEnumerator(){
            return new ObjectSetEnumerator(i_objectContainer, i_objectSet);
        }

        public int IndexOf(Object obj){
            long id = i_objectContainer.getID(obj);
            if (id > 0) {
                for (int i = 0; i < i_ids.Length; i++) {
                    if (i_ids[i] == id) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public void Insert(int pos, Object obj){
            throw new NotSupportedException();
        }

        public bool IsFixedSize{
            get{
                return true;
            }
        }

        public bool IsReadOnly{
            get{
                return true;
            }
        }

        public bool IsSynchronized{
            get{
                return false;
            }
        }
        public Object this[int index] {
            get{
                if (index < 0 || index >= Count) {
                    throw new ArgumentOutOfRangeException("Index " + index + " exceeds size " + Count);
                }
                return activatedObject(index);
            }
            set{
                throw new NotSupportedException();
            }
        }

        public void Remove(Object obj){
            throw new NotSupportedException();
        }

        public void RemoveAt(int pos){
            throw new NotSupportedException();
        }

        public Object SyncRoot{
            get{
                return null;
            }
        }
    }
}
