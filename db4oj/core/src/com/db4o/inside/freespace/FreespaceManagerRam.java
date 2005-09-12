/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;
import com.db4o.foundation.*;


public class FreespaceManagerRam extends FreespaceManager {
    
    private final YapFile     _file;
    
    private final TreeIntObject _finder   = new TreeIntObject(0);

    private Tree _freeByAddress;
    
    private Tree _freeBySize;
    
    public FreespaceManagerRam(YapFile file){
        _file = file;
    }
    
    public void free(int a_address, int a_length) {
        if(DTrace.enabled){
            DTrace.FREE.logLength(a_address, a_length);
        }
        
        if (a_length <= discardLimit()) {
            return;
        }
        
        a_length = _file.blocksFor(a_length);
        _finder.i_key = a_address;
        FreeSlotNode sizeNode;
        FreeSlotNode addressnode = (FreeSlotNode) Tree.findSmaller(_freeByAddress, _finder);
        if ((addressnode != null)
            && ((addressnode.i_key + addressnode.i_peer.i_key) == a_address)) {
            sizeNode = addressnode.i_peer;
            _freeBySize = _freeBySize.removeNode(sizeNode);
            sizeNode.i_key += a_length;
            FreeSlotNode secondAddressNode = (FreeSlotNode) Tree
                .findGreaterOrEqual(_freeByAddress, _finder);
            if ((secondAddressNode != null)
                && (a_address + a_length == secondAddressNode.i_key)) {
                sizeNode.i_key += secondAddressNode.i_peer.i_key;
                _freeBySize = _freeBySize
                    .removeNode(secondAddressNode.i_peer);
                _freeByAddress = _freeByAddress
                    .removeNode(secondAddressNode);
            }
            sizeNode.removeChildren();
            _freeBySize = Tree.add(_freeBySize, sizeNode);
        } else {
            addressnode = (FreeSlotNode) Tree.findGreaterOrEqual(
                _freeByAddress, _finder);
            if ((addressnode != null)
                && (a_address + a_length == addressnode.i_key)) {
                sizeNode = addressnode.i_peer;
                _freeByAddress = _freeByAddress.removeNode(addressnode);
                _freeBySize = _freeBySize.removeNode(sizeNode);
                sizeNode.i_key += a_length;
                addressnode.i_key = a_address;
                addressnode.removeChildren();
                sizeNode.removeChildren();
                _freeByAddress = Tree.add(_freeByAddress, addressnode);
                _freeBySize = Tree.add(_freeBySize, sizeNode);
            } else {
                addFreeSlotNodes(a_address, a_length);
            }
        }
        if (Deploy.debug) {
            _file.writeXBytes(a_address, a_length * blockSize());
        }
    }
    
    public int getSlot(int length) {
        int blocksNeeded = _file.blocksFor(length);
        _finder.i_key = blocksNeeded;
        _finder.i_object = null;
        _freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode) _freeBySize, _finder);

        if (_finder.i_object == null) {
            return 0;
        }
            
        FreeSlotNode node = (FreeSlotNode) _finder.i_object;
        int blocksFound = node.i_key;
        int address = node.i_peer.i_key;
        _freeByAddress = _freeByAddress.removeNode(node.i_peer);
        if (blocksFound > blocksNeeded) {
            addFreeSlotNodes(address + blocksNeeded, blocksFound - blocksNeeded);
        }
        return address;
    }

    public void read(int freeSlotsID) {
        if (freeSlotsID <= 0){
            return;
        }
        if(discardLimit() == Integer.MAX_VALUE){
            return;
        }
        YapWriter reader = _file.readWriterByID(trans(), freeSlotsID);
        if (reader == null) {
            return;
        }

        FreeSlotNode.sizeLimit = discardLimit();

        _freeBySize = new TreeReader(reader, new FreeSlotNode(0), true).read();

        final Tree[] addressTree = new Tree[1];
        if (_freeBySize != null) {
            _freeBySize.traverse(new Visitor4() {

                public void visit(Object a_object) {
                    FreeSlotNode node = ((FreeSlotNode) a_object).i_peer;
                    addressTree[0] = Tree.add(addressTree[0], node);
                }
            });
        }
        _freeByAddress = addressTree[0];

        free(freeSlotsID, YapConst.POINTER_LENGTH);
        free(reader.getAddress(), reader.getLength());
    }
    
    public int write(boolean shuttingDown){
        if(! shuttingDown){
            return 0;
        }
        int freeBySizeID = 0;
        int length = Tree.byteCount(_freeBySize);
        int[] slot = _file.newSlot(trans(), length);
        freeBySizeID = slot[0];
        YapWriter sdwriter = new YapWriter(trans(), length);
        sdwriter.useSlot(freeBySizeID, slot[1], length);
        Tree.write(sdwriter, _freeBySize);
        sdwriter.writeEncrypt();
        trans().writePointer(slot[0], slot[1], length);
        return freeBySizeID;
    }
    
    private void addFreeSlotNodes(int a_address, int a_length) {
        FreeSlotNode addressNode = new FreeSlotNode(a_address);
        addressNode.createPeer(a_length);
        _freeByAddress = Tree.add(_freeByAddress, addressNode);
        _freeBySize = Tree.add(_freeBySize, addressNode.i_peer);
    }
    
    private final Transaction trans(){
        return _file.i_systemTrans;
    }
    
    private final int discardLimit(){
        return _file.i_config.i_discardFreeSpace;
    }
    
    private final int blockSize(){
        return _file.blockSize();
    }

}
