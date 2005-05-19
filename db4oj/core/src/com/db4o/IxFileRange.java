/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * A range of index entries in the database file. 
 */
class IxFileRange extends IxTree{
    
    final int _address;
    int _addressOffset;
    int _entries;
    
    public IxFileRange(IxFieldTransaction a_ft, int a_address, int addressOffset, int a_entries){
        super(a_ft);
        _address = a_address;
        _addressOffset = addressOffset;
        _entries = a_entries;
        i_size = a_entries;
    }
    
    public Tree add(final Tree a_new){
        return i_fieldTransaction.i_index.fileRangeReader().add(this, a_new);
    }
    

    int compare(Tree a_to) {
        return i_fieldTransaction.i_index.fileRangeReader().compare(this, a_to);
    }
    
	int ownSize(){
	    return _entries;
	}

    void write(YapDataType a_handler, YapWriter a_writer) {
        YapFile yf = (YapFile)a_writer.getStream();
        int length = _entries * slotLength();
        yf.copy(_address, _addressOffset, a_writer.getAddress(), a_writer.addressOffset(), length);
        a_writer.moveForward(length);
    }

    Tree addToCandidatesTree(Tree a_tree, QCandidates a_candidates, int[] a_lowerAndUpperMatch) {
        return i_fieldTransaction.i_index.fileRangeReader().addToCandidatesTree(a_candidates, a_tree, this, a_lowerAndUpperMatch);
    }
    
    public String toString(){
//        return "";
        YapFile yf = stream();
        Transaction transaction = trans();
        YapReader reader = new YapReader(slotLength());
        StringBuffer sb = new StringBuffer();
        sb.append("IxFileRange");
        for (int i = 0; i < _entries; i++) {
            int address = _address + (i * slotLength());
            reader.read(yf, address, _addressOffset);
            reader._offset = 0;
            sb.append("\n  ");
            Object obj = handler().comparableObject(transaction, handler().readIndexEntry(reader));
            int parentID = reader.readInt();
            sb.append("Parent: " + parentID);
            sb.append("\n ");
            sb.append(obj);
        }
        return sb.toString();
    }

    public void incrementAddress(int length) {
        _addressOffset += length;
    }
}
