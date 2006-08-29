/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.classindex;

import com.db4o.*;
import com.db4o.foundation.Debug4;
import com.db4o.inside.Exceptions4;
import com.db4o.inside.slots.Slot;

/**
 * representation to collect and hold all IDs of one class
 */
 public class ClassIndex extends YapMeta implements ReadWriteable {
     
     
    private final YapClass _yapClass;
     
	/**
	 * contains TreeInt with object IDs 
	 */
	private Tree i_root;
    
    ClassIndex(YapClass yapClass){
        _yapClass = yapClass;
    }
	
	public void add(int a_id){
		i_root = Tree.add(i_root, new TreeInt(a_id));
	}

    public final int byteCount() {
    	return YapConst.INT_LENGTH * (Tree.size(i_root) + 1);
    }

    public final void clear() {
        i_root = null;
    }
    
    void ensureActive(Transaction trans){
        if (!isActive()) {
            setStateDirty();
            read(trans);
        }
    }

    int entryCount(Transaction ta){
        if(isActive() || isNew()){
            return Tree.size(i_root);
        }
        Slot slot = ta.getSlotInformation(getID());
        int length = YapConst.INT_LENGTH;
        if(Deploy.debug){
            length += YapConst.LEADING_LENGTH;
        }
        YapReader reader = new YapReader(length);
        reader.readEncrypt(ta.stream(), slot._address);
        if (reader == null) {
            return 0;
        }
        if (Deploy.debug) {
            reader.readBegin(getIdentifier());
        }
        return reader.readInt();
    }
    
    public final byte getIdentifier() {
        return YapConst.YAPINDEX;
    }
    
    TreeInt getRoot(){
        return (TreeInt)i_root;
    }
    
    public final int ownLength() {
        return YapConst.OBJECT_LENGTH + byteCount();
    }

    public final Object read(YapReader a_reader) {
    	throw Exceptions4.virtualException();
    }

    public final void readThis(Transaction a_trans, YapReader a_reader) {
    	i_root = new TreeReader(a_reader, new TreeInt(0)).read();
    }

	public void remove(int a_id){
		i_root = Tree.removeLike(i_root, new TreeInt(a_id));
	}

    void setDirty(YapStream a_stream) {
    	// TODO: get rid of the setDirty call
        a_stream.setDirtyInSystemTransaction(this);
    }

    public void write(YapReader a_writer) {
        writeThis(null, a_writer);
    }

    public final void writeThis(Transaction trans, final YapReader a_writer) {
    	Tree.write(a_writer, i_root);
    }
    
    public String toString(){
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
        return _yapClass + " index";  
    }
}