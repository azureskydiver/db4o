/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import java.io.IOException;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ix.*;


public class FreespaceManagerIx extends FreespaceManager{
    
    private int _slotAddress;
    
    private FreespaceIxAddress _addressIx;
    private FreespaceIxLength _lengthIx;
    
    private boolean _started;
    
    private Collection4 _xBytes;

    private final boolean _overwriteDeletedSlots;
    
    FreespaceManagerIx(LocalObjectContainer file){
        super(file);
        _overwriteDeletedSlots=Debug.xbytes||file.config().freespaceFiller()!=null;
    }
    
    private void add(int address, int length){
        _addressIx.add(address, length);
        _lengthIx.add(address, length);
    }
    
    public void beginCommit() {
        if(! started()){
            return;
        }
        slotEntryToZeroes(_file, _slotAddress);
    }
    
    public void debug(){
        if(Debug.freespace){
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Dumping file based address index");
            _addressIx.debug();
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Dumping file based length index");
            _lengthIx.debug();
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        }
    }
    
    public void endCommit() {
        if( ! started()){
            return;
        }
        if (_overwriteDeletedSlots) {
            _xBytes = new Collection4();
        }

        _addressIx._index.commitFreeSpace(_lengthIx._index);
        
        StatefulBuffer writer = new StatefulBuffer(_file.getSystemTransaction(), _slotAddress, slotLength());
        _addressIx._index._metaIndex.write(writer);
        _lengthIx._index._metaIndex.write(writer);
        if (_overwriteDeletedSlots) {
            writer.setID(Const4.IGNORE_ID);  // no XBytes check
        }
        if(_file.configImpl().flushFileBuffers()){
            _file.syncFiles();
        }
        writer.writeEncrypt();
        
        if(_overwriteDeletedSlots){
            Iterator4 i = _xBytes.iterator();
            _xBytes = null;
            while(i.moveNext()){
                int[] addressLength = (int[])i.current();
                overwriteDeletedSlots(addressLength[0], addressLength[1]);
            }
        }
    }
    
    public int entryCount() {
        return _addressIx.entryCount();
    }
    
    public void free(int address, int length) {
        
        if(! started()){
            return;
        }
        
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
        
        int freedAddress = address;
        int freedLength = length;
        
        _addressIx.find(address);
        
        if(_addressIx.preceding()){
            if(_addressIx.address() + _addressIx.length() == address){
                remove(_addressIx.address(), _addressIx.length());
                address =  _addressIx.address();
                length += _addressIx.length();
                _addressIx.find(freedAddress);
            }
        }
        
        if(_addressIx.subsequent()){
            if(freedAddress + freedLength == _addressIx.address()){
                remove(_addressIx.address(), _addressIx.length());
                length += _addressIx.length();
            }
        }
        
        add(address, length);
        
        if (_overwriteDeletedSlots) {
            overwriteDeletedSlots(freedAddress, freedLength);
        }
    }
    
    public void freeSelf() {
        if(! started()){
            return;
        }
        _addressIx._index._metaIndex.free(_file);
        _lengthIx._index._metaIndex.free(_file);
    }
    
    public int freeSize() {
        return _addressIx.freeSize();
    }

    public int getSlot(int length) {
        if(! started()){
            return 0;
        }
        int address = getSlot1(length);
        
        if(address != 0){
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

    public void migrate(final FreespaceManager newFM) {
        if(! started()){
            return;
        }
        final IntObjectVisitor addToNewFM = new IntObjectVisitor(){
            public void visit(int length, Object address) {
                newFM.free(((Integer)address).intValue(), length);
            }
        };
        Tree.traverse(_addressIx._indexTrans.getRoot(), new Visitor4() {
            public void visit(Object a_object) {
                IxTree ixTree = (IxTree)a_object;
                ixTree.visitAll(addToNewFM);
            }
        });
    }
    
	public void onNew(LocalObjectContainer file) {
		file.ensureFreespaceSlot();
	}
    
    public void read(int freespaceID) {
        // this is done in start(), nothing to do here
    }

    private void remove(int address, int length){
        _addressIx.remove(address, length);
        _lengthIx.remove(address, length);
    }
    
    public void start(int slotAddress) throws IOException {
        
        if(started()){
            return;
        }
        
        _slotAddress = slotAddress;
        
        MetaIndex miAddress = new MetaIndex();
        MetaIndex miLength = new MetaIndex();
        
        Buffer reader = new Buffer(slotLength());
        reader.read(_file, slotAddress, 0);
        miAddress.read(reader);
        miLength.read(reader);
        
        _addressIx = new FreespaceIxAddress(_file, miAddress);
        _lengthIx = new FreespaceIxLength(_file, miLength);
        
        _started = true;
    }
    

    private boolean started(){
        return _started; 
    }
    
    public byte systemType() {
        return FM_IX;
    }

    public int shutdown() {
        return 0;  // no special ID, FreespaceIX information is stored in fileheader variable part 
    }

    private void overwriteDeletedSlots(int address, int length){
        if (_overwriteDeletedSlots) {
            if(_xBytes == null){
                length = length * blockSize();
                _file.overwriteDeletedBytes(address, length);
            }else{
                _xBytes.add(new int[] {address, length});
            }
        }
    }

}
