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
using j4o.lang;
using j4o.util;

namespace com.db4o {

    internal class P2HashMapIterator : IDictionaryEnumerator  {

        private P1HashElement i_previous;

        private P1HashElement i_current;

        private P2HashMap i_map;

        private int i_nextIndex;

        private bool i_firstMoved;
      
        internal P2HashMapIterator(P2HashMap p2hashmap) : base() {
            i_map = p2hashmap;
            i_nextIndex = -1;
            getNextCurrent();
        }

        public virtual Object Current{
            get{
                return Entry;
            }
        }

        public DictionaryEntry Entry{
            get{
                lock (i_map.streamLock()) {
                    i_map.checkActive();
                    checkFirstMoved();
                    Object key = i_current.activatedKey(i_map.i_activationDepth);
                    return new DictionaryEntry(key,  i_map.get4(key));
                }
            }
        }

        public Object Key{
            get{
                lock (i_map.streamLock()) {
                    i_map.checkActive();
                    checkFirstMoved();
                    return i_current.activatedKey(i_map.i_activationDepth);
                }
            }
        }

        public bool MoveNext(){
            lock (i_map.streamLock()) {
                i_map.checkActive();
                if(! i_firstMoved){
                    i_firstMoved = true;
                }else{
                    if(i_current != null){
                        getNextCurrent();
                    }
                }
                return i_current != null;
            }
        }

        public void Reset(){
            lock (i_map.streamLock()) {
                i_map.checkActive();
                i_previous = null;
                i_current = null;
                i_firstMoved = false;
                i_nextIndex = -1;
                getNextCurrent();
            }
        }

        public Object Value{
            get{
                lock (i_map.streamLock()) {
                    i_map.checkActive();
                    checkFirstMoved();
                    return i_map.get4(i_current.activatedKey(i_map.i_activationDepth));
                }
            }
        }

        private void checkFirstMoved(){
            if(i_current == null || ! i_firstMoved){
                throw new InvalidOperationException("Enumerator is positioned before first or after last.");
            }
        }

        private int currentIndex() {
            if (i_current == null) return -1;
            return i_current.i_hashCode & i_map.i_mask;
        }
      
        private void getNextCurrent() {
            i_previous = i_current;
            i_current = (P1HashElement)nextElement();
            if (i_current != null) i_current.checkActive();
        }
      
        internal bool hasNext() {
            return i_current != null;
        }
      
        internal Object next() {
            Object ret = null;
            if (i_current != null){
                ret = i_current.activatedKey(i_map.i_activationDepth);
            }
            getNextCurrent();
            return ret;
        }
      
        private P1ListElement nextElement() {
            if (i_current != null && i_current.i_next != null) return i_current.i_next;
            if (i_nextIndex <= currentIndex()) searchNext();
            if (i_nextIndex >= 0) return i_map.i_table[i_nextIndex];
            return null;
        }

        private void searchNext() {
            if (i_nextIndex > -2) {
                while (++i_nextIndex < i_map.i_tableSize) {
                    if (i_map.i_table[i_nextIndex] != null) return;
                }
                i_nextIndex = -2;
            }
        }
    }
}