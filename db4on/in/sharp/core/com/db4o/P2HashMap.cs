/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o.inside;
using com.db4o.types;
using com.db4o.foundation;

namespace com.db4o {

    internal class P2HashMap : P1Collection, Db4oMap, TransactionListener {
        
        protected static float FILL = 0.6F;

        [Transient]
        protected int i_changes;
        
        [Transient]
        protected bool i_dontStoreOnDeactivate;

        public P1HashElement[] i_entries;
        public int i_mask;
        public int i_maximumSize;
        public int i_size;
        public int i_type;  // 0 == default hash, 1 == ID hash
        
        [Transient]
        internal P1HashElement[] i_table;
        
        public int i_tableSize;
      
        internal P2HashMap() : base() {
        }
      
        internal P2HashMap(int a_size) : base() {
            a_size = (int)(a_size /FILL);
            i_tableSize = 1;
            while (i_tableSize < a_size) {
                i_tableSize = i_tableSize << 1;
            }
            i_mask = i_tableSize - 1;
            i_maximumSize = (int)(i_tableSize * FILL);
            i_table = new P1HashElement[i_tableSize];
        }

        public void Add(Object a_key, Object a_value) {
            lock (this.streamLock()) {
                checkActive();
                put4(a_key, a_value);
            }
        }

        public void Clear() {
            lock (this.streamLock()) {
                checkActive();
                if (i_size != 0) {
                    for (int i = 0; i < i_table.Length; i++) {
                        deleteAllElements(i_table[i]);
                        i_table[i] = null;
                    }
                    for (int i = 0; i < i_entries.Length; i++){
                        i_entries[i] = null;
                    }
                    i_size = 0;
                    modified();
                }
            }
        }

        public bool Contains(Object obj) {
            lock (this.streamLock()) {
                checkActive();
                return get4(obj) != null;
            }
        }

        public void CopyTo(Array arr, int pos){
            lock (this.streamLock()) {
                this.checkActive();
                P2HashMapIterator i = new P2HashMapIterator(this);
                while(i.hasNext()){
                    Object key = i.next();
                    arr.SetValue(new DictionaryEntry(key, get4(key)), pos++);
                }
            }
        }

        public int Count{
            get{
                lock (this.streamLock()) {
                    checkActive();
                    return i_size;
                }
            }
        }

        public IDictionaryEnumerator GetEnumerator(){
            return (IDictionaryEnumerator)getEnumerator1();
        }

        private int hashOf(Object key) {
            if(i_type == 1) {
                int id = (int)getIDOf(key);
                if(id == 0) {
                    Exceptions4.throwRuntimeException(62);
                }
                return id;
            }
            return key.GetHashCode();
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

        public ICollection Keys{
            get{
                lock (this.streamLock()) {
                    checkActive();
                    return new P2HashMapKeySet(this);
                }
            }
        }

        public void Remove(Object obj){
            lock (this.streamLock()) {
                this.checkActive();
                remove4(obj);
            }
        }

        public Object SyncRoot{
            get{
                this.checkActive();
                return streamLock();
            }
        }

        public Object this[object a_key] {
            get{
                lock (streamLock()) {
                    checkActive();
                    return get4(a_key);
                }
            }

            set{
                lock (this.streamLock()) {
                    checkActive();
                    put4(a_key, value);
                }
            }
        }

        public ICollection Values{
            get{
                throw new NotSupportedException();
            }
        }
      
        public override int activationDepth() {
            return 2;
        }
      
        public override int adjustReadDepth(int i) {
            return 2;
        }
        
        internal override void checkActive() {
            base.checkActive();
            if (i_table == null) {
                i_table = new P1HashElement[i_tableSize];
                if (i_entries != null) {
                    for (int i = 0; i < i_entries.Length; i++) {
                        if(i_entries[i] != null){
                            i_entries[i].checkActive();
                            i_table[i_entries[i].i_position] = i_entries[i];
                        }
                    }
                }
                i_changes = 0;

                // FIXME: reducing the table in size can be a problem during defragment in 
                //        C/S mode on P2HashMaps that were partially stored uncommitted.

//                if ((i_size + 1) * 10 < i_tableSize) {
//                    i_tableSize = i_size + 1;
//                    increaseSize();
//                    modified();
//                }

            }
        }
      
        public bool containsValue(Object obj) {
            throw new NotSupportedException();
        }
      
        public override Object createDefault(Transaction transaction) {
            checkActive();
            P2HashMap m4 = new P2HashMap(i_size);
            m4.setTrans(transaction);
            P2HashMapIterator i = new P2HashMapIterator(this);
            while (i.hasNext()) {
                Object obj1 = i.next();
                m4.put4(obj1, get4(obj1));
            }
            return m4;
        }
      
        protected void deleteAllElements(P1HashElement a_entry) {
            if (a_entry != null) {
                a_entry.checkActive();
                deleteAllElements((P1HashElement)a_entry.i_next);
                a_entry.delete(i_deleteRemoved);
            }
        }
      
        protected bool Equals(P1HashElement phe, int i, Object obj) {
            return phe.i_hashCode == i && phe.activatedKey(elementActivationDepth()).Equals(obj);
        }
      
        public Object get(Object obj) {
            lock (this.streamLock()) {
                checkActive();
                return get4(obj);
            }
        }
      
        internal Object get4(Object obj) {
            int hash = hashOf(obj);
            for (P1HashElement phe = i_table[hash & i_mask]; phe != null; phe = (P1HashElement)phe.i_next) {
                phe.checkActive();
                if (Equals(phe, hash, obj)){
                    return phe.activatedObject(elementActivationDepth());
                }
            }
            return null;
        }

        protected override IEnumerator getEnumerator1(){
            lock (this.streamLock()) {
                this.checkActive();
                return new P2HashMapIterator(this);
            }
        }
      
        protected void increaseSize() {
            i_tableSize = i_tableSize << 1;
            i_maximumSize = (int)(i_tableSize * FILL);
            i_mask = i_tableSize - 1;
            P1HashElement[] temp = i_table;
            i_table = new P1HashElement[i_tableSize];
            for (int i = 0; i < temp.Length; i++){
                reposition(temp[i]);
            }
        }

        internal void modified() {
            if (getTrans() != null) {
                if (i_changes == 0){
                    getTrans().addTransactionListener(this);
                }
                i_changes++;
            }
        }
      
        public void postRollback() {
            i_dontStoreOnDeactivate = true;
            deactivate();
            i_dontStoreOnDeactivate = false;
        }
      
        public void preCommit() {
            if (i_changes > 0) {
                Collection4 col = new Collection4();
                for (int i = 0; i < i_table.Length; i++) {
                    if (i_table[i] != null) {
                        i_table[i].checkActive();
                        if (i_table[i].i_position != i) {
                            i_table[i].i_position = i;
                            i_table[i].update();
                        }
                        col.add(i_table[i]);
                    }
                }
                if (i_entries == null || i_entries.Length != col.size()){
                    i_entries = new P1HashElement[col.size()];
                }
                int j = 0;
                Iterator4 it = col.fastIterator();
                while (it.hasNext()){
                    i_entries[j++] = (P1HashElement)it.next();
                }
                store(2);
            }
            i_changes = 0;
        }
      
        public override void preDeactivate() {
            if (!i_dontStoreOnDeactivate){
                preCommit();
            }
            i_table = null;
        }
      
        protected Object put4(Object a_key, Object a_value) {
            int hash = hashOf(a_key);
            P1HashElement entry = new P1HashElement(this.getTrans(), null, a_key, hash, a_value);
            i_size++;
            if (i_size > i_maximumSize){
                increaseSize();
            }
            modified();
            int index = entry.i_hashCode & i_mask;
            P1HashElement phe = i_table[index];
            P1HashElement last = null;
            while(phe != null) {
                phe.checkActive();
                if (Equals(phe, entry.i_hashCode, a_key)) {
                    i_size--;
                    Object ret = phe.activatedObject(elementActivationDepth());
                    entry.i_next = phe.i_next;
                    this.store(entry);
                    if (last != null) {
                        last.i_next = entry;
                        last.update();
                    } else {
                        i_table[index] = entry;
                    }
                    phe.delete(i_deleteRemoved);
                    return ret;
                }
                last = phe;
                phe = (P1HashElement)phe.i_next;
            }
            entry.i_next = i_table[index];
            i_table[index] = entry;
            this.store(entry);
            return null;
        }
      
		/*
        public Object remove(Object obj) {
            lock (this.streamLock()) {
                checkActive();
                return remove4(obj);
            }
        }
		*/
      
        internal Object remove4(Object a_key) {
            int hash = hashOf(a_key);
            P1HashElement phe = i_table[hash & i_mask];
            P1HashElement last = null;
            for (; phe != null; phe = (P1HashElement)phe.i_next) {
                phe.checkActive();
                if (Equals(phe, hash, a_key)) {
                    if (last != null) {
                        last.i_next = phe.i_next;
                        last.update();
                    } else i_table[hash & i_mask] = (P1HashElement)phe.i_next;
                    modified();
                    i_size--;
                    Object obj = phe.activatedObject(elementActivationDepth());
                    phe.delete(i_deleteRemoved);
                    return obj;
                }
                last = phe;
            }
            return null;
        }

        public void replicateFrom(Object obj) {
            checkActive();
            if(i_entries != null){
                for (int i = 0; i < i_entries.Length; i++) {
                    if(i_entries[i] != null){
                        i_entries[i].delete(false);
                    }
                    i_entries[i] = null;
                }
            }
            if(i_table != null){
                for (int i = 0; i < i_table.Length; i++) {
                    i_table[i] = null;
                }
            }
            i_size = 0;
        
            P2HashMap m4 = (P2HashMap)obj;
            m4.checkActive();
            P2HashMapIterator it = new P2HashMapIterator(m4);
            while (it.hasNext()) {
                Object key = it.next();
                put4(key, m4.get4(key));
            }
        
            modified();
        }

      
        protected void reposition(P1HashElement a_entry) {
            if (a_entry != null) {
                reposition((P1HashElement)a_entry.i_next);
                a_entry.checkActive();
                Object oldNext = a_entry.i_next;
                a_entry.i_next = i_table[a_entry.i_hashCode & i_mask];
                if (a_entry.i_next != oldNext){
                    a_entry.update();
                }
                i_table[a_entry.i_hashCode & i_mask] = a_entry;
            }
        }

        public override Object storedTo(Transaction transaction) {
            if (this.getTrans() == null) {
                this.setTrans(transaction);
                modified();
            } else if (transaction != this.getTrans()){
             	return replicate(getTrans(), transaction);
            }
            return this;
        }
    }
}