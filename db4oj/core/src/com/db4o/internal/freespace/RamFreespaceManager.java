/* Copyright (C) 2004 - 2005  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


public class RamFreespaceManager extends AbstractFreespaceManager {
    
	private final TreeIntObject _finder   = new TreeIntObject(0);

    private Tree _freeByAddress;
    
    private Tree _freeBySize;
    
	private FreespaceListener _listener = NullFreespaceListener.INSTANCE;
    
	public RamFreespaceManager(Procedure4<Slot> slotFreedCallback, int discardLimit) {
		super(slotFreedCallback, discardLimit);
	}
	
    private void addFreeSlotNodes(int address, int length) {
        FreeSlotNode addressNode = new FreeSlotNode(address);
        addressNode.createPeer(length);
        _freeByAddress = Tree.add(_freeByAddress, addressNode);
        addToFreeBySize(addressNode._peer);
    }

	private void addToFreeBySize(FreeSlotNode node) {
		_freeBySize = Tree.add(_freeBySize, node);
		_listener.slotAdded(node._key);
	}
    
	public Slot allocateTransactionLogSlot(int length) {
		FreeSlotNode sizeNode = (FreeSlotNode) Tree.last(_freeBySize);
		if(sizeNode == null || sizeNode._key < length){
			return null;
		}

        // We can just be appending to the end of the file, using one
        // really big contigous slot that keeps growing. Let's limit.
        int limit = length + 100; 
        if(sizeNode._key > limit){
            return allocateSlot(limit);
        }
        
		removeFromBothTrees(sizeNode);
		return new Slot(sizeNode._peer._key, sizeNode._key);
	}
	
	public void freeTransactionLogSlot(Slot slot) {
		free(slot);
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
        if (address <= 0) {
        	throw new IllegalArgumentException();
        }
        
        int length = slot.length();
        if(DTrace.enabled){
            DTrace.FREESPACEMANAGER_RAM_FREE.logLength(address, length);
        }
        
        _finder._key = address;
        FreeSlotNode sizeNode;
        FreeSlotNode addressnode = (FreeSlotNode) Tree.findSmaller(_freeByAddress, _finder);
        if ((addressnode != null)
            && ((addressnode._key + addressnode._peer._key) == address)) {
            sizeNode = addressnode._peer;
            removeFromFreeBySize(sizeNode);
            sizeNode._key += length;
            FreeSlotNode secondAddressNode = (FreeSlotNode) Tree
                .findGreaterOrEqual(_freeByAddress, _finder);
            if ((secondAddressNode != null)
                && (address + length == secondAddressNode._key)) {
                sizeNode._key += secondAddressNode._peer._key;
                removeFromBothTrees(secondAddressNode._peer);
            }
            sizeNode.removeChildren();
            addToFreeBySize(sizeNode);
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
                addToFreeBySize(sizeNode);
            } else {
                if (canDiscard(length)) {
                    return;
                }
                addFreeSlotNodes(address, length);
            }
        }
        slotFreed(slot);
    }
    
	public void freeSelf() {
        // Do nothing.
        // The RAM manager frees itself on reading.
    }
    
    public Slot allocateSlot(int length) {
    	
        _finder._key = length;
        _finder._object = null;
        _freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode) _freeBySize, _finder);

        if (_finder._object == null) {
            return null;
        }
            
        FreeSlotNode node = (FreeSlotNode) _finder._object;
        _listener.slotRemoved(node._key);
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
        	DTrace.FREESPACEMANAGER_GET_SLOT.logLength(address, length);
        }
        
        return new Slot(address, length);
    }
    
    int marshalledLength() {
        return TreeInt.marshalledLength((TreeInt)_freeBySize);
    }

    public void read(LocalObjectContainer container, int freeSlotsID) {
        readById(container, freeSlotsID);
    }

    private void read(ByteArrayBuffer reader) {
        FreeSlotNode.sizeLimit = discardLimit();
        _freeBySize = new TreeReader(reader, new FreeSlotNode(0), true).read();
        final ByRef<Tree> addressTree = ByRef.newInstance();
        if (_freeBySize != null) {
            _freeBySize.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    FreeSlotNode node = ((FreeSlotNode) a_object)._peer;
                    addressTree.value = Tree.add(addressTree.value, node);
                }
            });
        }
        _freeByAddress = addressTree.value;
    }
    
    void read(LocalObjectContainer container, Slot slot){
        if(slot.isNull()){
            return;
        }
        ByteArrayBuffer buffer = container.readBufferBySlot(slot);
        if (buffer == null) {
            return;
        }
        read(buffer);
        if(! Debug4.freespace){
		    container.free(slot);
		}
    }
    
    private void readById(LocalObjectContainer container, int freeSlotsID){
        if (freeSlotsID <= 0){
            return;
        }
        if(discardLimit() == Integer.MAX_VALUE){
            return;
        }
        read(container, container.readPointerSlot(freeSlotsID));
        if(! Debug4.freespace){
          container.free(freeSlotsID, Const4.POINTER_LENGTH);
        }
    }

    private void removeFromBothTrees(FreeSlotNode sizeNode){
        removeFromFreeBySize(sizeNode);
        _freeByAddress = _freeByAddress.removeNode(sizeNode._peer);
    }

	private void removeFromFreeBySize(FreeSlotNode node) {
		_freeBySize = _freeBySize.removeNode(node);
		_listener.slotRemoved(node._key);
	}
    
    public int slotCount() {
        return Tree.size(_freeByAddress);
    }
    
    public void start(int slotAddress) {
        // this is done in read(), nothing to do here
    }
    
    public byte systemType() {
        return FM_RAM;
    }
    
    public String toString(){
        final StringBuffer sb = new StringBuffer();
        sb.append("RAM FreespaceManager\n");
        sb.append("Address Index\n");
        _freeByAddress.traverse(new ToStringVisitor(sb));
        sb.append("Length Index\n");
        _freeBySize.traverse(new ToStringVisitor(sb));
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

    public int write(LocalObjectContainer container){
        int pointerSlot = container.allocatePointerSlot();
		Slot slot = container.allocateSlot(marshalledLength());
		Pointer4 pointer = new Pointer4(pointerSlot, slot); 
        write(container, pointer);
        return pointer._id;
    }

    void write(LocalObjectContainer container, Pointer4 pointer) {
    	ByteArrayBuffer buffer = new ByteArrayBuffer(pointer.length());
        TreeInt.write(buffer, (TreeInt)_freeBySize);
        container.writeEncrypt(buffer, pointer.address(), 0);
        container.syncFiles();
        container.writePointer(pointer.id(), pointer._slot);
    }

    final static class ToStringVisitor implements Visitor4 {
		private final StringBuffer _sb;

		ToStringVisitor(StringBuffer sb) {
			_sb = sb;
		}

		public void visit(Object obj) {
		    _sb.append(obj);
		    _sb.append("\n");
		}
	}
    
	public void listener(FreespaceListener listener) {
		_listener = listener;
	}


}
