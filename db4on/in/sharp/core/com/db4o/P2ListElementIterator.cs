/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;

namespace com.db4o {

    internal class P2ListElementIterator : IEnumerator{

        private P2LinkedList i_list;

        private P1ListElement i_preprevious;

        private P1ListElement i_previous;

        private P1ListElement i_next;

        private bool i_firstMoved;
      
        internal P2ListElementIterator(P2LinkedList p2linkedlist, P1ListElement p1listelement) : base() {
            i_list = p2linkedlist;
            i_next = p1listelement;
            checkNextActive();
        }

        public Object Current {
            get{
                if(i_next == null || ! i_firstMoved){
                    throw new InvalidOperationException("Enumerator is positioned before first or after last.");
                }
                lock (i_next.streamLock()) {
                    return i_next.activatedObject(i_list.elementActivationDepth());
                }
            }
        }

        public bool MoveNext(){
            if (i_next != null) {
                lock (i_next.streamLock()) {
                    if(! i_firstMoved){
                        i_firstMoved = true;
                        return i_next != null;
                    }
                    i_preprevious = i_previous;
                    i_previous = i_next;
                    Object obj1 = i_next.activatedObject(i_list.elementActivationDepth());
                    i_next = i_next.i_next;
                    checkNextActive();
                    return i_next != null;
                }
            }
            return false;
        }

        public void Reset(){
            i_preprevious = null;
            i_previous = null;
            i_firstMoved = false;
            i_next = i_list.i_first;
            checkNextActive();
        }
      
        protected void checkNextActive() {
            if (i_next != null) i_next.checkActive();
        }
      
        public bool hasNext(){
            return i_next != null;
        }

        internal P1ListElement move(int i) {
            if (i < 0){
                return null;
            }
            for (int i_0_1 = 0; i_0_1 < i; i_0_1++) {
                if (hasNext()){
                    nextElement();
                } else{
                    return null;
                }
            }
            if (hasNext()){
                return nextElement();
            }
            return null;
        }

        internal P1ListElement nextElement() {
            i_preprevious = i_previous;
            i_previous = i_next;
            i_next = i_next.i_next;
            checkNextActive();
            return i_previous;
        }
      
    }
}