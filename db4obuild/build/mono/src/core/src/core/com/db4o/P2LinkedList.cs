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
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o.types;

namespace com.db4o {

    internal class P2LinkedList : P1Collection, Db4oList {
      
        public P1ListElement i_first;

        public P1ListElement i_last;

        internal P2LinkedList() : base() {
        }

        public int Add(Object obj){
            lock (streamLock()) {
                checkActive();
                if (obj == null){
                    throw new ArgumentNullException();
                }
                add4(obj);
                update();
                return size4() - 1;
            }
        }

        public void Clear(){
            lock (streamLock()) {
                checkActive();
                P2ListElementIterator i = iterator4();
                while (i.hasNext()) {
                    P1ListElement le = i.nextElement();
                    le.delete(i_deleteRemoved);
                }
                i_first = null;
                i_last = null;
                update();
            }
        }

        public bool Contains(Object obj){
            return IndexOf(obj) >= 0;
        }

        public void CopyTo(Array arr, int pos){
            lock (streamLock()) {
                checkActive();
                P2ListElementIterator i = iterator4();
                while (i.hasNext()) {
                    P1ListElement ple = i.nextElement();
                    arr.SetValue(ple.activatedObject(i_activationDepth), pos++);
                }
            }
        }

        public int Count{
            get{
                lock (streamLock()) {
                    checkActive();
                    return size4();
                }
            }
        }

        public IEnumerator GetEnumerator(){
            return getEnumerator1();
        }
      
        public int IndexOf(Object obj){
            lock (streamLock()) {
                checkActive();
                return indexOf4(obj);
            }
        }

        public void Insert(int pos, Object obj){
            lock (streamLock()) {
                checkActive();
                if (pos == 0) {
                    i_first = new P1ListElement(getTrans(), i_first, obj);
                    store(i_first);
                    checkLastAndUpdate(null, i_first);
                } else {
                    P2ListElementIterator i = iterator4();
                    P1ListElement previous = i.move(pos - 1);
                    if (previous == null){
                        throw new IndexOutOfRangeException();
                    }
                    P1ListElement newE = new P1ListElement(getTrans(), previous.i_next, obj);
                    store(newE);
                    previous.i_next = newE;
                    previous.update();
                    checkLastAndUpdate(previous, newE);
                }
            }
        }

        public bool IsFixedSize{
            get{
                return false;
            }
        }

        public bool IsReadOnly{
            get{
                return false;
            }
        }

        public bool IsSynchronized{
            get{
                return true;
            }
        }

        public void Remove(Object obj){
            lock (streamLock()) {
                checkActive();
                remove4(obj);
            }
        }

        public void RemoveAt(int pos){
            lock (streamLock()) {
                checkActive();
                remove4(pos);
            }
        }

        public Object SyncRoot{
            get{
                checkActive();
                return streamLock();
            }
        }

        public Object this[int index] {

            get{
                lock (streamLock()) {
                    checkActive();
                    P1ListElement ple = iterator4().move(index);
                    if (ple != null){
                        return ple.activatedObject(i_activationDepth);
                    }
                    return null;
                }
            }

            set{
                lock (streamLock()) {
                    checkActive();
                    bool needUpdate = false;
                    P1ListElement elem = null;
                    P1ListElement previous = null;
                    P1ListElement newElement = new P1ListElement(getTrans(), null, value);
                    if (index == 0) {
                        previous = i_first;
                        i_first = newElement;
                        needUpdate = true;
                    } else {
                        elem = iterator4().move(index - 1);
                        if (elem != null){
                            previous = elem.i_next; 
                        }else{ 
                            throw new IndexOutOfRangeException();
                        }
                    }
                    if (previous!= null) {
                        previous.checkActive();
                        newElement.i_next = previous.i_next;
                        if (elem != null) {
                            elem.i_next = newElement;
                            elem.update();
                        }
                        previous.delete(i_deleteRemoved);
                    } else {
                        i_last = newElement;
                        needUpdate = true;
                    }
                    if (needUpdate){
                        update();
                    }
                }
            }
        }

        protected bool add4(Object obj) {
            if (obj != null) {
                P1ListElement newElement = new P1ListElement(getTrans(), null, obj);
                store(newElement);
                if (i_first == null){
                    i_first = newElement;
                }else {
                    i_last.checkActive();
                    i_last.i_next = newElement;
                    i_last.update();
                }
                i_last = newElement;
                return true;
            }
            return false;
        }

        public override int adjustReadDepth(int i) {
            return 1;
        }
      
        protected void checkLastAndUpdate(P1ListElement a_previous, P1ListElement a_added) {
            if (i_last == a_previous){
                i_last = a_added;
            }
            update();
        }
      
        internal void checkRemoved(P1ListElement a_previous, P1ListElement a_removed) {
            bool needsUpdate = false;
            if (a_removed == i_first) {
                i_first = a_removed.i_next;
                needsUpdate = true;
            }
            if (a_removed == i_last) {
                i_last = a_previous;
                needsUpdate = true;
            }
            if (needsUpdate){
                update();
            }
        }
      
        protected bool contains4(Object obj) {
            return indexOf4(obj) >= 0;
        }
      
        public override Object createDefault(Transaction transaction) {
            checkActive();
            P2LinkedList ll = new P2LinkedList();
            ll.setTrans(transaction);
            P2ListElementIterator i = iterator4();
            while (i.MoveNext()){
                ll.add4(i.Current);
            }
            return ll;
        }

        protected override IEnumerator getEnumerator1(){
            lock (streamLock()) {
                checkActive();
                return iterator4();
            }
        }
     
        public override bool hasClassIndex() {
            return true;
        }
      
        protected int indexOf4(Object obj) {
            int idx = 0;
            if (getTrans() != null  && (! getTrans().i_stream.handlers().isSecondClass(obj))) {
                long id = getIDOf(obj);
                if (id > 0) {
                    P2ListElementIterator i = iterator4();
                    while (i.hasNext()) {
                        P1ListElement le= i.nextElement();
                        if (getIDOf(le.i_object) == id){
                            return idx;
                        }
                        idx++;
                    }
                }
            } else {
                P2ListElementIterator i = iterator4();
                while (i.hasNext()) {
                    P1ListElement le = i.nextElement();
                    if (le.i_object.Equals(obj)){
                        return idx;
                    }
                    idx++;
                }
            }
            return -1;
        }

        protected P2ListElementIterator iterator4() {
            return new P2ListElementIterator(this, i_first);
        }
      
        protected Object remove4(int idx) {
            Object ret = null;
            P1ListElement elem = null;
            P1ListElement previous = null;
            if (idx == 0){
                elem = i_first;
            }else {
                previous = iterator4().move(idx - 1);
                if (previous != null){
                    elem = previous.i_next;
                }
            }
            if (elem != null) {
                elem.checkActive();
                if (previous != null) {
                    previous.i_next = elem.i_next;
                    previous.update();
                }
                checkRemoved(previous, elem);
                ret = elem.activatedObject(i_activationDepth);
                elem.delete(i_deleteRemoved);
                return ret;
            }
            throw new IndexOutOfRangeException();
        }
      
        protected bool remove4(Object obj) {
            int idx = indexOf4(obj);
            if (idx >= 0) {
                remove4(idx);
                return true;
            }
            return false;
        }
      
        protected int size4() {
            int size = 0;
            P2ListElementIterator i = iterator4();
            while (i.hasNext()) {
                size++;
                i.nextElement();
            }
            return size;
        }
      
        public override Object storedTo(Transaction transaction) {
            if (getTrans() == null){
                setTrans(transaction);
            }else if (transaction != getTrans()){
                return createDefault(transaction);
            }
            return this;
        }
      
    }
}