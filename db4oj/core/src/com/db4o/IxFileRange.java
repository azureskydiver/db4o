/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * A range of index entries in the database file. 
 */
class IxFileRange extends IxTree{
    
    int i_address;
    int i_entries;
    
    public IxFileRange(IxFieldTransaction a_ft, int a_address, int a_entries){
        super(a_ft);
        i_address = a_address;
        i_entries = a_entries;
        i_size = a_entries;
    }
    
    public Tree add(final Tree a_new){
        return i_fieldTransaction.i_index.fileRangeReader().add(this, a_new);
    }
    

    int compare(Tree a_to) {
        return i_fieldTransaction.i_index.fileRangeReader().compare(this, a_to);
    }
    
	int ownSize(){
	    return i_entries;
	}

    void write(YapDataType a_handler, YapWriter a_writer) {
        YapFile yf = (YapFile)a_writer.getStream();
        int length = i_entries * slotLength();
        yf.copy(i_address, a_writer.getAddress(), length);
        a_writer.setAddress(a_writer.getAddress() + length);
    }

    Tree addToCandidatesTree(Tree a_tree, QCandidates a_candidates, int[] a_lowerAndUpperMatch) {
        return i_fieldTransaction.i_index.fileRangeReader().addToCandidatesTree(a_candidates, a_tree, this, a_lowerAndUpperMatch);
    }
    
    public String toString(){
        return "";
//        YapFile yf = stream();
//        Transaction trans = trans();
//        YapReader reader = new YapReader(slotLength());
//        StringBuffer sb = new StringBuffer();
//        sb.append("IxFileRange");
//        for (int i = 0; i < i_entries; i++) {
//            int address = i_address + (i * slotLength());
//            reader.read(yf, address);
//            reader.i_offset = 0;
//            sb.append("\n  ");
//            Object obj = handler().indexObject(trans, handler().readIndexEntry(reader));
//            int parentID = reader.readInt();
//            sb.append("Parent: " + parentID);
//            sb.append("\n ");
//            sb.append(obj);
//        }
//        return sb.toString();
    }
}
