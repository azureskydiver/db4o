/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


public class RamFreespaceManager extends AbstractFreespaceManager {
    
    private final TreeIntObject _finder   = new TreeIntObject(0);

    private Tree _freeByAddress;
    
    private Tree _freeBySize;
    
    public RamFreespaceManager(LocalObjectContainer file){
        super(file);
    }
    
    private void addFreeSlotNodes(int address, int length) {
        FreeSlotNode addressNode = new FreeSlotNode(address);
        addressNode.createPeer(length);
        _freeByAddress = Tree.add(_freeByAddress, addressNode);
        _freeBySize = Tree.add(_freeBySize, addressNode._peer);
    }
    
	public Slot allocateTransactionLogSlot(int length) {
		FreeSlotNode sizeNode = (FreeSlotNode) Tree.last(_freeBySize);
		if(sizeNode == null || sizeNode._key < length){
			return null;
		}
		removeFromBothTrees(sizeNode);
		return new Slot(sizeNode._peer._key, sizeNode._key);
	}

    public void beginCommit() {
        // do nothing
    }
    
	public void commit() {
		// do nothing
	}
    
    public void endCommit() {
        // do nothing
    }
    
    public void free(final Slot slot) {
    	
    	int address = slot.address();
    	int length = slot.length();
        
        if (address <= 0) {
        	throw new IllegalArgumentException();
        }
        
        if(DTrace.enabled){
            DTrace.FREE_RAM.logLength(address, length);
        }
        
        _finder._key = address;
        FreeSlotNode sizeNode;
        FreeSlotNode addressnode = (FreeSlotNode) Tree.findSmaller(_freeByAddress, _finder);
        if ((addressnode != null)
            && ((addressnode._key + addressnode._peer._key) == address)) {
            sizeNode = addressnode._peer;
            _freeBySize = _freeBySize.removeNode(sizeNode);
            sizeNode._key += length;
            FreeSlotNode secondAddressNode = (FreeSlotNode) Tree
                .findGreaterOrEqual(_freeByAddress, _finder);
            if ((secondAddressNode != null)
                && (address + length == secondAddressNode._key)) {
                sizeNode._key += secondAddressNode._peer._key;
                removeFromBothTrees(secondAddressNode._peer);
            }
            sizeNode.removeChildren();
            _freeBySize = Tree.add(_freeBySize, sizeNode);
        } else {
            addressnode = (FreeSlotNode) Tree.findGreaterOrEqual(
                _freeByAddress, _finder);
            if ((addressnode != null)
                && (address + length == addressnode._key)) {
                sizeNode = addressnode._peer;
                removeFromBothTrees(sizeNode);
                sizeNode._key += length;
                addressnode._key = address;
                addressnode.removeChildren();
                sizeNode.removeChildren();
                _freeByAddress = Tree.add(_freeByAddress, addressnode);
                _freeBySize = Tree.add(_freeBySize, sizeNode);
            } else {
                if (canDiscard(length)) {
                    return;
                }
                addFreeSlotNodes(address, length);
            }
        }
        _file.overwriteDeletedBlockedSlot(slot);
    }
    
    public void freeSelf() {
        // Do nothing.
        // The RAM manager frees itself on reading.
    }
    
    public Slot getSlot(int length) {
    	
        _finder._key = length;
        _finder._object = null;
        _freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode) _freeBySize, _finder);

        if (_finder._object == null) {
            return null;
        }
            
        FreeSlotNode node = (FreeSlotNode) _finder._object;
        int blocksFound = node._key;
        int address = node._peer._key;
        _freeByAddress = _freeByAddress.removeNode(node._peer);
        int remainingBlocks = blocksFound - length;
    	if(canDiscard(remainingBlocks)){
    		length = blocksFound;
    	}else{
    		addFreeSlotNodes(address + length, remainingBlocks);	
    	}
        
        if(DTrace.enabled){
        	DTrace.GET_FREESPACE.logLength(address, length);
        }
        
        return new Slot(address, length);
    }
    
    public String toString(){
    	final StringBuffer sb = new StringBuffer();
    	sb.append("RAM FreespaceManager\n");
    	sb.append("Address Index\n");
        _freeByAddress.traverse(new Visitor4() {
            public void visit(Object obj) {
            	sb.append(obj);
            	sb.append("\n");
            }
        
        });
        sb.append("Length Index\n");
        _freeBySize.traverse(new Visitor4() {
              public void visit(Object obj) {
                  sb.append(obj);
                  sb.append("\n");
              }
          });
        return sb.toString();
    }
    
    public void traverse(final Visitor4 visitor) {
		if (_freeByAddress == null) {
			return;
		}
		_freeByAddress.traverse(new Visitor4() {
			public void visit(Object a_object) {
				FreeSlotNode fsn = (FreeSlotNode) a_object;
				int address = fsn._key;
				int length = fsn._peer._key;
				visitor.visit(new Slot(address, length));
			}
		});
	}

    public int onNew(LocalObjectContainer file) {
		// do nothing
    	return 0;
	}
    
    public void read(int freeSlotsID) {
        if (freeSlotsID <= 0){
            return;
        }
        if(discardLimit() == Integer.MAX_VALUE){
            return;
        }
        StatefulBuffer reader = _file.readWriterByID(trans(), freeSlotsID);
        if (reader == null) {
            return;
        }

        FreeSlotNode.sizeLimit = blockedDiscardLimit();

        _freeBySize = new TreeReader(reader, new FreeSlotNode(0), true).read();

        final Tree.ByRef addressTree = new Tree.ByRef();
        if (_freeBySize != null) {
            _freeBySize.traverse(new Visitor4() {

                public void visit(Object a_object) {
                    FreeSlotNode node = ((FreeSlotNode) a_object)._peer;
                    addressTree.value = Tree.add(addressTree.value, node);
                }
            });
        }
        _freeByAddress = addressTree.value;
        
        if(! Debug.freespace){
          _file.free(freeSlotsID, Const4.POINTER_LENGTH);
          _file.free(reader.getAddress(), reader.getLength());
        }
    }
    
    public void start(int slotAddress) {
        // this is done in read(), nothing to do here
    }
    
    public byte systemType() {
        return FM_RAM;
    }
    
    private final LocalTransaction trans(){
        return (LocalTransaction)_file.systemTransaction();
    }

    public int write(){
        int freeBySizeID = 0;
        int length = TreeInt.marshalledLength((TreeInt)_freeBySize);
        
        Pointer4 pointer = _file.newSlot(trans(), length); 
        freeBySizeID = pointer._id;
        StatefulBuffer sdwriter = new StatefulBuffer(trans(), length);
        sdwriter.useSlot(freeBySizeID, pointer._slot);
        TreeInt.write(sdwriter, (TreeInt)_freeBySize);
        sdwriter.writeEncrypt();
        trans().writePointer(pointer._id, pointer._slot);
        return freeBySizeID;
    }

    public int slotCount() {
        return Tree.size(_freeByAddress);
    }
    
    private void removeFromBothTrees(FreeSlotNode sizeNode){
        _freeBySize = _freeBySize.removeNode(sizeNode);
        _freeByAddress = _freeByAddress.removeNode(sizeNode._peer);
    }

}
