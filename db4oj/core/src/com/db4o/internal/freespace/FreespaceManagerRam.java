/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


public class FreespaceManagerRam extends AbstractFreespaceManager {
    
    private final TreeIntObject _finder   = new TreeIntObject(0);

    private Tree _freeByAddress;
    
    private Tree _freeBySize;
    
    public FreespaceManagerRam(LocalObjectContainer file){
        super(file);
    }
    
    public void traverseFreeSlots(final Visitor4 visitor) {
    	Tree.traverse(_freeByAddress, new Visitor4() {
			public void visit(Object obj) {
				FreeSlotNode node = (FreeSlotNode) obj;
				int address = node._key;
				int length = node._peer._key;
				visitor.visit(new Slot(address, length));
			}
		});
    }
    
    private void addFreeSlotNodes(int a_address, int a_length) {
        FreeSlotNode addressNode = new FreeSlotNode(a_address);
        addressNode.createPeer(a_length);
        _freeByAddress = Tree.add(_freeByAddress, addressNode);
        _freeBySize = Tree.add(_freeBySize, addressNode._peer);
    }

    public void beginCommit() {
        // do nothing
    }
    
    public void debug(){
        if(Debug.freespace){
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Dumping RAM based address index");
            _freeByAddress.traverse(new Visitor4() {
            
                public void visit(Object a_object) {
                    System.out.println(a_object);
                }
            
            });
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Dumping RAM based length index");
            _freeBySize.traverse(new Visitor4() {
                  public void visit(Object a_object) {
                      System.out.println(a_object);
                  }
              });
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        }
    }
    
    public void endCommit() {
        // do nothing
    }
    
    public void free(Slot slot) {
    	
    	int address = slot._address;
    	int length = slot._length;
        
        if (address <= 0) {
        	return;
        	
        	// TODO: FB change to this:
        	// throw new IllegalStateException();
        }
        
        if (length <= discardLimit()) {
            return;
        }
        
        if(DTrace.enabled){
            DTrace.FREE_RAM.logLength(address, length);
        }
        
        length = _file.blocksFor(length);
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
                _freeBySize = _freeBySize
                    .removeNode(secondAddressNode._peer);
                _freeByAddress = _freeByAddress
                    .removeNode(secondAddressNode);
            }
            sizeNode.removeChildren();
            _freeBySize = Tree.add(_freeBySize, sizeNode);
        } else {
            addressnode = (FreeSlotNode) Tree.findGreaterOrEqual(
                _freeByAddress, _finder);
            if ((addressnode != null)
                && (address + length == addressnode._key)) {
                sizeNode = addressnode._peer;
                _freeByAddress = _freeByAddress.removeNode(addressnode);
                _freeBySize = _freeBySize.removeNode(sizeNode);
                sizeNode._key += length;
                addressnode._key = address;
                addressnode.removeChildren();
                sizeNode.removeChildren();
                _freeByAddress = Tree.add(_freeByAddress, addressnode);
                _freeBySize = Tree.add(_freeBySize, sizeNode);
            } else {
                addFreeSlotNodes(address, length);
            }
        }
        if(! Debug.freespaceChecker){
        	_file.overwriteDeletedBytes(address, length * blockSize());
        }
    }
    
    public void freeSelf() {
        // Do nothing.
        // The RAM manager frees itself on reading.
    }
    
    public int totalFreespace() {
        final MutableInt mint = new MutableInt();
        Tree.traverse(_freeBySize, new Visitor4() {
            public void visit(Object obj) {
                FreeSlotNode node = (FreeSlotNode) obj;
                mint.add(node._key);
            }
        });
        return mint.value();
    }
    
    public int getSlot(int length) {
        int address = getSlot1(length);
        
        if(DTrace.enabled){
        	if(address != 0){
                DTrace.GET_FREESPACE_RAM.logLength(address, length);
            }
        }
        return address;
    }
    
    public int getSlot1(int length) {
        length = _file.blocksFor(length);
        _finder._key = length;
        _finder._object = null;
        _freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode) _freeBySize, _finder);

        if (_finder._object == null) {
            return 0;
        }
            
        FreeSlotNode node = (FreeSlotNode) _finder._object;
        int blocksFound = node._key;
        int address = node._peer._key;
        _freeByAddress = _freeByAddress.removeNode(node._peer);
        if (blocksFound > length) {
            addFreeSlotNodes(address + length, blocksFound - length);
        }
        return address;
    }
    

    public void migrate(final FreespaceManager newFM) {
        if(_freeByAddress != null){
            _freeByAddress.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    FreeSlotNode fsn = (FreeSlotNode)a_object;
                    int address = fsn._key;
                    int length = fsn._peer._key;
                    newFM.free(new Slot(address, length));
                }
            });
        }
    }
    
    public void onNew(LocalObjectContainer file) {
		// do nothing
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

        FreeSlotNode.sizeLimit = discardLimit();

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

    public int shutdown(){
        int freeBySizeID = 0;
        int length = TreeInt.byteCount((TreeInt)_freeBySize);
        
        Pointer4 ptr = _file.newSlot(trans(), length); 
        freeBySizeID = ptr._id;
        StatefulBuffer sdwriter = new StatefulBuffer(trans(), length);
        sdwriter.useSlot(freeBySizeID, ptr._address, length);
        TreeInt.write(sdwriter, (TreeInt)_freeBySize);
        sdwriter.writeEncrypt();
        trans().writePointer(ptr._id, ptr._address, length);
        return freeBySizeID;
    }

    public int entryCount() {
        return Tree.size(_freeByAddress);
    }



}
