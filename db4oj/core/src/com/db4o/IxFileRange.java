/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * A range of index entries in the database file. 
 */
class IxFileRange extends IxTree{
    
    final int _address;
    int _addressOffset;
    int _entries;
    private int[] _lowerAndUpperMatches;
    
    public IxFileRange(IxFieldTransaction a_ft, int a_address, int addressOffset, int a_entries){
        super(a_ft);
        _address = a_address;
        _addressOffset = addressOffset;
        _entries = a_entries;
        i_size = a_entries;
    }
    
    public Tree add(final Tree a_new){
        return reader().add(this, a_new);
    }

    int compare(Tree a_to) {
        _lowerAndUpperMatches = new int[2];
        return reader().compare(this, _lowerAndUpperMatches);
    }
    
    int[] lowerAndUpperMatch(){
        return _lowerAndUpperMatches;
    }
    
    private final IxFileRangeReader reader(){
        return i_fieldTransaction.i_index.fileRangeReader();
    }
    
    public void incrementAddress(int length) {
        _addressOffset += length;
    }
    
	int ownSize(){
	    return _entries;
	}
    
    public String toString(){
//        return "";
        YapFile yf = stream();
        Transaction transaction = trans();
        YapReader fileReader = new YapReader(slotLength());
        StringBuffer sb = new StringBuffer();
        sb.append("IxFileRange");
        for (int i = 0; i < _entries; i++) {
            int address = _address + (i * slotLength());
            fileReader.read(yf, address, _addressOffset);
            fileReader._offset = 0;
            sb.append("\n  ");
            Object obj = handler().comparableObject(transaction, handler().readIndexEntry(fileReader));
            int parentID = fileReader.readInt();
            sb.append("Parent: " + parentID);
            sb.append("\n ");
            sb.append(obj);
        }
        return sb.toString();
    }

    public void visit(Visitor4 visitor, int[] lowerUpper){
        IxFileRangeReader frr = reader();
        if (lowerUpper == null) {
            lowerUpper = new int[] { 0, _entries - 1};
        }
        int count = lowerUpper[1] - lowerUpper[0] + 1;
        if (count > 0) {
            YapReader fileReader = new YapReader(count * frr._slotLength);
            fileReader.read(stream(), _address, _addressOffset + (lowerUpper[0] * frr._slotLength));
            for (int i = lowerUpper[0]; i <= lowerUpper[1]; i++) {
                fileReader.incrementOffset(frr._linkLegth);
                visitor.visit(new Integer(fileReader.readInt()));
            }
        }

    }

    void write(YapDataType a_handler, YapWriter a_writer) {
        YapFile yf = (YapFile)a_writer.getStream();
        int length = _entries * slotLength();
        yf.copy(_address, _addressOffset, a_writer.getAddress(), a_writer.addressOffset(), length);
        a_writer.moveForward(length);
    }
}
