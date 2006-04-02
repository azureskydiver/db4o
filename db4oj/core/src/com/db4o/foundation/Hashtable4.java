/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude 
 */
public class Hashtable4 implements DeepClone {
    
    private static final float FILL = 0.5F;

    private int i_tableSize;
    private int i_mask;
    private int i_maximumSize;
    private int i_size;
    private HashtableIntEntry[] i_table;
    
    protected Hashtable4() {}
    
    public Hashtable4(int a_size){
        a_size = newSize(a_size);  // legacy for .NET conversion
        i_tableSize = 1;
        while (i_tableSize < a_size){
            i_tableSize = i_tableSize << 1;
        }
        i_mask = i_tableSize - 1;
        i_maximumSize = (int)(i_tableSize * FILL);
        i_table = new HashtableIntEntry[i_tableSize];
    }
    
    private final int newSize(int a_size){
        return (int)(a_size/FILL);
    }
    
    public Object deepClone(Object obj) {
        return deepCloneInternal(new Hashtable4(),obj);
    }
    
    protected Hashtable4 deepCloneInternal(Hashtable4 ret,Object obj) {
        ret.i_mask=i_mask;
        ret.i_maximumSize=i_maximumSize;
        ret.i_size=i_size;
        ret.i_tableSize=i_tableSize;
        ret.i_table = new HashtableIntEntry[i_tableSize];
        for (int i = 0; i < i_tableSize; i++) {
            if(i_table[i] != null){
                ret.i_table[i] = (HashtableIntEntry)i_table[i].deepClone(obj);
            }
        }
        return ret;
    }
    
    public void forEachKey(Visitor4 visitor){
        for (int i = 0; i < i_table.length; i++) {
            HashtableIntEntry hie = i_table[i];
            while(hie != null){
                if(hie instanceof HashtableObjectEntry){
                    visitor.visit( ((HashtableObjectEntry)hie).i_objectKey);
                }else{
                    visitor.visit(new Integer(hie.i_key));    
                }
                hie = hie.i_next;
            }
        }
    }

    public void forEachValue(Visitor4 visitor){
        for (int i = 0; i < i_table.length; i++) {
            HashtableIntEntry hie =i_table[i];
            while(hie != null){
                visitor.visit(hie.i_object);
                hie = hie.i_next;
            }
        }
    }
    
    public void forEachKeyForIdentity(Visitor4 visitor, Object a_identity){
        for (int i = 0; i < i_table.length; i++) {
            HashtableIntEntry hie =i_table[i];
            while(hie != null){
                if(hie.i_object == a_identity){
                    if(hie instanceof HashtableObjectEntry){
                        visitor.visit( ((HashtableObjectEntry)hie).i_objectKey);
                    }else{
                        visitor.visit(new Integer(hie.i_key));    
                    }
                }
                hie = hie.i_next;
            }
        }
    }


    public Object get(int a_key) {
        HashtableIntEntry ihe = i_table[a_key & i_mask];
        while( ihe != null ) {
            if (ihe.i_key == a_key) {
                return ihe.i_object;
            }
            ihe = ihe.i_next;
        }
        return null;
    }
    
    public Object get(Object a_objectKey) {
    	if(a_objectKey == null){
    		return null;
    	}
        int a_key = a_objectKey.hashCode();
        HashtableObjectEntry ihe = (HashtableObjectEntry)i_table[a_key & i_mask];
        while (ihe != null) {
            if (ihe.i_key == a_key  && ihe.i_objectKey.equals(a_objectKey)) {
                return ihe.i_object;
            }
            ihe = (HashtableObjectEntry)ihe.i_next;
        }
        return null;
    }
    
    public Object get(byte[] a_bytes) {
        int a_key = hash(a_bytes);
        HashtableObjectEntry ihe = (HashtableObjectEntry)i_table[a_key & i_mask];
        while (ihe != null) {
            if (ihe.i_key == a_key ){
                byte[] bytes = (byte[])ihe.i_objectKey;
                if(bytes.length == a_bytes.length){
                    boolean isEqual = true;
	                for (int i = 0; i < bytes.length; i++) {
	                    if(bytes[i] != a_bytes[i]){
	                        isEqual = false;
	                    }
	                }
	                if(isEqual){
	                    return ihe.i_object;
	                }
                }
            }
            ihe = (HashtableObjectEntry)ihe.i_next;
        }
        return null;
    }
    
    private int hash(byte[] bytes){
        int ret = 0;
        for (int i = 0; i < bytes.length; i++) {
            ret = ret * 31 + bytes[i]; 
        }
        return ret;
    }

    private void increaseSize() {
        i_tableSize = i_tableSize << 1;
        i_maximumSize = i_maximumSize << 1;
        i_mask = i_tableSize - 1;
        HashtableIntEntry[] temp = i_table;
        i_table = new HashtableIntEntry[i_tableSize];
        for (int i = 0; i < temp.length; i++){
            reposition(temp[i]);
        }
    }

    public void put(int a_key, Object a_object) {
        put1(new HashtableIntEntry(a_key,a_object));
    }
    
    public void put(Object a_key, Object a_object){
        put1(new HashtableObjectEntry(a_key,a_object));
    }
    
    public void put(byte[] a_bytes, Object a_object){
        int a_key = hash(a_bytes);
        put1(new HashtableObjectEntry(a_key, a_bytes,a_object));
    }
    
    private void put1(HashtableIntEntry a_entry){
        i_size++;
        if (i_size > i_maximumSize){
            increaseSize();
        }
        int index = a_entry.i_key & i_mask;
        a_entry.i_next = i_table[index];
        i_table[index] = a_entry;
    }
    
    public void remove(int a_key){
        HashtableIntEntry ihe = i_table[a_key & i_mask];
        HashtableIntEntry last = null; 
        while (ihe != null) {
            if (ihe.i_key == a_key ){
                if(last != null){
                    last.i_next = ihe.i_next;
                }else{
                    i_table[a_key & i_mask] = ihe.i_next;
                }
                i_size--;
                return;
            }
            last = ihe;
            ihe = ihe.i_next;
        }
    }
    
    public void remove(Object a_objectKey){
        int a_key = a_objectKey.hashCode();
        HashtableObjectEntry ihe = (HashtableObjectEntry)i_table[a_key & i_mask];
        HashtableIntEntry last = null; 
        while (ihe != null) {
            if (ihe.i_key == a_key && ihe.i_objectKey.equals(a_objectKey)){
                if(last != null){
                    last.i_next = ihe.i_next;
                }else{
                    i_table[a_key & i_mask] = ihe.i_next;
                }
                i_size--;
                return;
            }
            last = ihe;
            ihe = (HashtableObjectEntry)ihe.i_next;
        }
    }
    
    public Object remove(byte[] a_bytes){
        int a_key = hash(a_bytes);
        HashtableObjectEntry ihe = (HashtableObjectEntry)i_table[a_key & i_mask];
        HashtableObjectEntry last = null;
        while (ihe != null) {
            if (ihe.i_key == a_key ){
                byte[] bytes = (byte[])ihe.i_objectKey;
                if(bytes.length == a_bytes.length){
                    boolean isEqual = true;
                    for (int i = 0; i < bytes.length; i++) {
                        if(bytes[i] != a_bytes[i]){
                            isEqual = false;
                        }
                    }
                    if(isEqual){
                        if(last != null){
                            last.i_next = ihe.i_next;
                        }else{
                            i_table[a_key & i_mask] = ihe.i_next;
                        }
                        i_size--;
                        return ihe.i_object;
                    }
                }
            }
            last = ihe;
            ihe = (HashtableObjectEntry)ihe.i_next;
        }
        return null;
    }
    
    private void reposition(HashtableIntEntry a_entry) {
        if (a_entry != null) {
            reposition(a_entry.i_next);
            a_entry.i_next = i_table[a_entry.i_key & i_mask];
            i_table[a_entry.i_key & i_mask] = a_entry;
        }
    }
    
}
