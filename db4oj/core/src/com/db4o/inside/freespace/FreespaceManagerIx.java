/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;
import com.db4o.inside.ix.*;


public class FreespaceManagerIx extends FreespaceManager{
    
    Index4 _addressIndex;
    Index4 _lengthIndex;
    
    IndexTransaction _addressXT;
    IndexTransaction _lengthXT;

    FreespaceManagerIx(YapFile file){
        super(file);
    }
    
    public void free(int address, int length) {
        
        if(DTrace.enabled){
            DTrace.FREE.logLength(address, length);
        }
        
        if (length <= discardLimit()) {
            return;
        }
        
        length = _file.blocksFor(length);
        
        IxTraverser traverser = new IxTraverser();
        traverser.findBoundsExactMatch(new Integer(address), (IxTree)_addressXT.getRoot());
        
        FreespaceVisitor visitor = preceding(traverser);
        if(visitor != null){
            if(visitor._key + visitor._value == address){
                removeIndexEntry(visitor._key, visitor._value);
                address = visitor._key;
                length += visitor._value;
            }
        }
        
        visitor = subsequent(traverser);
        if(visitor != null){
            if(address + length == visitor._key){
                removeIndexEntry(visitor._key, visitor._value);
                length += visitor._value;
            }
        }
        
        addIndexEntry(address, length);
        
        if (Deploy.debug) {
            _file.writeXBytes(address, length * blockSize());
        }
    }
    
    private void addIndexEntry(int address, int length){
        _addressIndex._handler.prepareComparison(new Integer(address));
        _addressXT.add(length, new Integer(address));
        _lengthIndex._handler.prepareComparison(new Integer(length));
        _lengthXT.add(address, new Integer(length));
    }
    
    private void removeIndexEntry(int address, int length){
        _addressIndex._handler.prepareComparison(new Integer(address));
        _addressXT.remove(length, new Integer(address));
        _lengthIndex._handler.prepareComparison(new Integer(length));
        _lengthXT.remove(address, new Integer(length));
    }
    
    
    
    private FreespaceVisitor preceding(IxTraverser traverser){
        FreespaceVisitor visitor = new FreespaceVisitor();
        traverser.visitPreceding(visitor);
        if(visitor.visited()){
            return visitor;
        }
        return null;
    }
    
    private FreespaceVisitor subsequent(IxTraverser traverser){
        FreespaceVisitor visitor = new FreespaceVisitor();
        traverser.visitSubsequent(visitor);
        if(visitor.visited()){
            return visitor;
        }
        return null;
    }
    
    private FreespaceVisitor match(IxTraverser traverser){
        FreespaceVisitor visitor = new FreespaceVisitor();
        traverser.visitMatch(visitor);
        if(visitor.visited()){
            return visitor;
        }
        return null;
    }


    
    public int getSlot(int length) {
        if(! started()){
            return 0;
        }
        int address = getSlot1(length);
        if(DTrace.enabled){
            if(address != 0){
                DTrace.GET_FREESPACE.logLength(address, length);
            }
        }
        return address;
    }
    

    private int getSlot1(int length) {
        if(! started()){
            return 0;
        }
        
        length = _file.blocksFor(length);
        
        IxTraverser traverser = new IxTraverser();
        traverser.findBoundsExactMatch(new Integer(length), (IxTree)_lengthXT.getRoot());
        
        FreespaceVisitor visitor = match(traverser);
        if(visitor != null){
            removeIndexEntry(visitor._key, visitor._value);
            return visitor._key;
        }
        
        visitor = subsequent(traverser);
        if(visitor != null){
            removeIndexEntry(visitor._key, visitor._value);
            return visitor._key;
        }
        return 0;
    }

    public void read(int freespaceID) {
        // this is done in start(), nothing to do here
    }

    public int write(boolean shuttingDown) {
        return 0;  // no special ID, Freespace information is stored in PBootRecord 
    }

    public byte systemType() {
        return FM_IX;
    }
    
    private boolean started(){
        return _addressIndex != null; 
    }
    

    public void start() {
        if(started()){
            return;
        }
        PBootRecord bootRecord = _file.bootRecord();
        if(bootRecord._freespaceByAddress == null){
            bootRecord._freespaceByAddress = new MetaIndex();
            bootRecord._freespaceByLength = new MetaIndex();
            bootRecord.setDirty();
            bootRecord.store(Integer.MAX_VALUE);
        }
        Transaction trans = _file.getSystemTransaction();
        _addressIndex = new Index4(trans,new YInt(_file), bootRecord._freespaceByAddress); 
        _lengthIndex = new Index4(trans,new YInt(_file), bootRecord._freespaceByLength);
        _addressXT = _addressIndex.globalIndexTransaction();
        _lengthXT = _lengthIndex.globalIndexTransaction();
    }

}
