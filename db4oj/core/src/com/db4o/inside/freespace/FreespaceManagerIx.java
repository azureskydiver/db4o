/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;
import com.db4o.foundation.*;


public class FreespaceManagerIx extends FreespaceManager{
    
    private FreespaceIxAddress _addressIx;
    private FreespaceIxLength _lengthIx;
    
    private boolean _dirty;
    
    private Collection4 _xBytes;

    FreespaceManagerIx(YapFile file){
        super(file);
    }
    
    public void free(int address, int length) {
        
        if (address <= 0) {
            return;
        }
        
        if (length <= discardLimit()) {
            return;
        }
        
        if(DTrace.enabled){
            DTrace.FREE.logLength(address, length);
        }
        
        length = _file.blocksFor(length);
        
        _addressIx.find(address);
        
        if(_addressIx.preceding()){
            if(_addressIx.address() + _addressIx.length() == address){
                
                // System.out.println("Merging preceding a: " + _addressIx.address() + " l: " + _addressIx.length() + " to " + address);
                
                remove(_addressIx.address(), _addressIx.length());
                address =  _addressIx.address();
                length += _addressIx.length();
            }
        }
        
        if(_addressIx.subsequent()){
            if(address + length == _addressIx.address()){
                
                // System.out.println("Merging subsequent a: " + _addressIx.address() + " to a: " + address + " l: " + length);
                
                remove(_addressIx.address(), _addressIx.length());
                length += _addressIx.length();
            }
        }
        
        add(address, length);
        _dirty = true;
        
        if (Deploy.debug) {
            writeXBytes(address, length);
        }
    }
    
    private void add(int address, int length){
        
        // System.out.println("FreeSpaceManagerIX.add " + address + " " + length);
        
        _addressIx.add(address, length);
        _lengthIx.add(address, length);
    }
    
    private void remove(int address, int length){
        
        // System.out.println("FreeSpaceManagerIX.remove " + address + " " + length);
        
        _addressIx.remove(address, length);
        _lengthIx.remove(address, length);
    }
    
    public int getSlot(int length) {
        if(! started()){
            return 0;
        }
        int address = getSlot1(length);
        
        if(address != 0){
            
            _dirty = true;
            
            if(DTrace.enabled){
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
        
        _lengthIx.find(length);
        
        if(_lengthIx.match()){
            remove(_lengthIx.address(), _lengthIx.length());
            return _lengthIx.address();
        }
        
        if(_lengthIx.subsequent()){
            int lengthRemainder = _lengthIx.length() - length;
            int addressRemainder = _lengthIx.address() + length; 
            remove(_lengthIx.address(), _lengthIx.length());
            add(addressRemainder, lengthRemainder);
            return _lengthIx.address();
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
        return _addressIx != null; 
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
        _addressIx = new FreespaceIxAddress(_file, bootRecord._freespaceByAddress);
        _lengthIx = new FreespaceIxLength(_file, bootRecord._freespaceByLength);
    }
    
    public void commit(){
        if(started()  && _dirty){
            if(Deploy.debug){
                _xBytes = new Collection4();
            }
            _addressIx._index.commitFreeSpace(_lengthIx._index);
            if(Deploy.debug){
                Iterator4 i = _xBytes.iterator();
                while(i.hasNext()){
                    int[] addressLength = (int[])i.next();
                    _file.writeXBytes(addressLength[0], addressLength[1]);
                }
                _xBytes = null;
            }
        }
    }
    
    private void writeXBytes(int address, int length){
        if (Deploy.debug) {
            length = length * blockSize();
            if(_xBytes == null){
                _file.writeXBytes(address, length);
            }else{
                _xBytes.add(new int[] {address, length});
            }
        }
    }
    
    
    

}
