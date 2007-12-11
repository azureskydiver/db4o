/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ix.IxTree;
import com.db4o.internal.slots.Slot;


public class FreespaceManagerIx extends AbstractFreespaceManager{
    
    private int _slotAddress;
    
    private FreespaceIxAddress _addressIx;
    private FreespaceIxLength _lengthIx;
    
    private boolean _started;
    
    private Collection4 _xBytes;

    private final boolean _overwriteDeletedSlots;
    
    public FreespaceManagerIx(LocalObjectContainer file){
        super(file);
        _overwriteDeletedSlots=Debug.xbytes||file.config().freespaceFiller()!=null;
    }
    
    private void add(int address, int length){
        _addressIx.add(address, length);
        _lengthIx.add(address, length);
    }
    
	public Slot allocateTransactionLogSlot(int length) {
		return getSlot(length);
	}
	
	public void freeTransactionLogSlot(Slot slot) {
		free(slot);
	}
    
    public void beginCommit() {
        if(! started()){
            return;
        }
        slotEntryToZeroes(_file, _slotAddress);
    }
    
    public void endCommit() {
        if( ! started()){
            return;
        }
        if (_overwriteDeletedSlots) {
            _xBytes = new Collection4();
        }

        _addressIx._index.commitFreeSpace(_lengthIx._index);
        
        StatefulBuffer writer = new StatefulBuffer(_file.systemTransaction(), _slotAddress, slotLength());
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
            	Slot slot = (Slot) i.current();
                overwriteDeletedSlot(slot);
            }
        }
    }
    
    public int slotCount() {
        return _addressIx.entryCount();
    }
    
    public void free(Slot slot) {
    	
    	int address = slot.address();
    	int length = slot.length();
        
        if(! started()){
            return;
        }
        
        if (address <= 0) {
            return;
        }
        
        if (canDiscard(length)) {
            return;
        }
        
        if(DTrace.enabled){
            DTrace.FREE.logLength(address, length);
        }
        
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
            overwriteDeletedSlot(new Slot(freedAddress, freedLength));
        }
    }
    
    public void freeSelf() {
        if(! started()){
            return;
        }
        _addressIx._index._metaIndex.free(_file);
        _lengthIx._index._metaIndex.free(_file);
    }
    
    public Slot getSlot(int length) {

		if (!started()) {
			return null;
		}

		int address = 0;

		_lengthIx.find(length);

		if (_lengthIx.match()) {
			remove(_lengthIx.address(), _lengthIx.length());
			address = _lengthIx.address();
		} else if (_lengthIx.subsequent()) {
			int lengthRemainder = _lengthIx.length() - length;
			int addressRemainder = _lengthIx.address() + length;
			remove(_lengthIx.address(), _lengthIx.length());
			add(addressRemainder, lengthRemainder);
			address = _lengthIx.address();
		}

		if (address == 0) {
			return null;
		}

		if (DTrace.enabled) {
			DTrace.GET_FREESPACE.logLength(address, length);
		}
		return new Slot(address, length);
	}

    public void migrateTo(FreespaceManager fm) {
        if(! started()){
            return;
        }
        super.migrateTo(fm);
    }
    
	public void traverse(final Visitor4 visitor) {
        if(! started()){
            return;
        }
        final IntObjectVisitor dispatcher = new IntObjectVisitor(){
            public void visit(int length, Object address) {
            	visitor.visit(new Slot(((Integer)address).intValue(), length));
            }
        };
        Tree.traverse(_addressIx._indexTrans.getRoot(), new Visitor4() {
            public void visit(Object a_object) {
                IxTree ixTree = (IxTree)a_object;
                ixTree.visitAll(dispatcher);
            }
        });
	}
    
	public int onNew(LocalObjectContainer file) {
		return file.ensureFreespaceSlot();
	}
    
    public void read(int freespaceID) {
        // this is done in start(), nothing to do here
    }

    private void remove(int address, int length){
        _addressIx.remove(address, length);
        _lengthIx.remove(address, length);
    }
    
    public void start(int slotAddress) {
        
        if(started()){
            return;
        }
        
        _slotAddress = slotAddress;
        
        MetaIndex miAddress = new MetaIndex();
        MetaIndex miLength = new MetaIndex();
        
        BufferImpl reader = new BufferImpl(slotLength());
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

    public int write() {
        return 0;  // no special ID, FreespaceIX information is stored in fileheader variable part 
    }

    private void overwriteDeletedSlot(Slot slot){
        if (_overwriteDeletedSlots) {
            if(_xBytes == null){
                _file.overwriteDeletedBlockedSlot(slot);
            }else{
                _xBytes.add(slot);
            }
        }
    }
    
    public String toString() {
    	String str = "FreespaceManagerIx\n" + _lengthIx.toString(); 
    	return str;
    }

	public void commit() {
		// do nothing
	}


}
