/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

/**
 * public for .NET conversion reasons
 */
public final class YapWriter extends YapReader {

    private int i_address;

    private int i_cascadeDelete; 

    private Tree i_embedded;
    private int i_id;

    // carries instantiation depth through the reading process
    private int i_instantionDepth;
    private int i_length;

    Transaction i_trans;

    // carries updatedepth depth through the update process
    // and carries instantiation information through the reading process 
    private int i_updateDepth = 1;

    YapWriter(Transaction a_trans, int a_initialBufferSize) {
        i_trans = a_trans;
        i_length = a_initialBufferSize;
        i_bytes = new byte[i_length];
    }

    YapWriter(Transaction a_trans, int a_address, int a_initialBufferSize) {
        this(a_trans, a_initialBufferSize);
        i_address = a_address;
    }

    YapWriter(YapWriter parent, YapWriter[] previousRead, int previousCount) {
        previousRead[previousCount++] = this;
        int parentID = parent.readInt();
        i_length = parent.readInt();
        i_id = parent.readInt();
        previousRead[parentID].addEmbedded(this);
        i_address = parent.readInt();
        i_trans = parent.getTransaction();
        i_bytes = new byte[i_length];
        System.arraycopy(parent.i_bytes, parent.i_offset, i_bytes, 0, i_length);
        parent.i_offset += i_length;
        if (previousCount < previousRead.length) {
            new YapWriter(parent, previousRead, previousCount);
        }
    }

    //	void debug(){
    //		if(Debug.current){
    //			System.out.println("Address: " + i_address + " ID:" + i_id);
    //			if(i_embedded != null){
    //				System.out.println("Children:");
    //				Iterator i = i_embedded.iterator();
    //				while(i.hasNext()){
    //					((YapBytes)(i.next())).debug();
    //				}
    //			}
    //		}
    //	}

    void addEmbedded(YapWriter a_bytes) {
        i_embedded = Tree.add(i_embedded, new TreeIntObject(a_bytes.getID(), a_bytes));
    }


    int appendTo(final YapWriter a_bytes, int a_id) {
        a_id++;
        a_bytes.writeInt(i_length);
        a_bytes.writeInt(i_id);
        a_bytes.writeInt(i_address);
        a_bytes.append(i_bytes);
        final int[] newID = { a_id };
        final int myID = a_id;
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(YapWriter a_embedded) {
                a_bytes.writeInt(myID);
                newID[0] = a_embedded.appendTo(a_bytes, newID[0]);
            }
        });
        return newID[0];
    }

    int cascadeDeletes() {
        return i_cascadeDelete;
    }

    void debugCheckBytes() {
        if (Deploy.debug) {
            if (i_offset != i_length) {
                // Db4o.log("!!! YapBytes.debugCheckBytes not all bytes used");
                // This is normal for writing The FreeSlotArray, becauce one
                // slot is possibly reserved by it's own pointer.
            }
        }
    }

    int embeddedCount() {
        final int[] count = { 0 };
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(YapWriter a_bytes) {
                count[0] += 1 + a_bytes.embeddedCount();
            }
        });
        return count[0];
    }

    int embeddedLength() {
        final int[] length = { 0 };
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(YapWriter a_bytes) {
                length[0] += a_bytes.getLength() + a_bytes.embeddedLength();
            }
        });
        return length[0];
    }

    void forEachEmbedded(final VisitorYapBytes a_visitor) {
        if (i_embedded != null) {
            i_embedded.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    a_visitor.visit((YapWriter) ((TreeIntObject)a_object).i_object);
                }
            });
        }
    }

    int getAddress() {
        return i_address;
    }
    

    int getID() {
        return i_id;
    }

    int getInstantiationDepth() {
        return i_instantionDepth;
    }

    int getLength() {
        return i_length;
    }

    YapStream getStream() {
        return i_trans.i_stream;
    }

    Transaction getTransaction() {
        return i_trans;
    }

    int getUpdateDepth() {
        return i_updateDepth;
    }
    
    byte[] getWrittenBytes(){
        byte[] bytes = new byte[i_offset];
        System.arraycopy(i_bytes, 0, bytes, 0, i_offset);
        return bytes;
    }


    void read() {
        i_trans.i_stream.readBytes(i_bytes, i_address, i_length);
    }

    final void read(YapSocket sock) throws IOException {
        int offset = 0;
        int length = i_length;
        while (length > 0) {
            int read = sock.read(i_bytes, offset, length);
            offset += read;
            length -= read;
        }
    }

    final YapWriter readEmbeddedObject() {
        int id = readInt();
        int length = readInt();
        Tree tio = TreeInt.find(i_embedded, id);
        if (tio != null) {
            return (YapWriter) ((TreeIntObject)tio).i_object;
        }
        YapWriter bytes = i_trans.i_stream.readObjectWriterByAddress(i_trans, id, length);
        if (bytes != null) {
            bytes.setID(id);
        }
        return bytes;
    }

    final YapWriter readYapBytes() {
        int length = readInt();
        if (length == 0) {
            return null;
        }
        YapWriter yb = new YapWriter(i_trans, length);
        System.arraycopy(i_bytes, i_offset, yb.i_bytes, 0, length);
        i_offset += length;
        return yb;
    }

    void removeFirstBytes(int aLength) {
        i_length -= aLength;
        byte[] temp = new byte[i_length];
        System.arraycopy(i_bytes, aLength, temp, 0, i_length);
        i_bytes = temp;
        i_offset -= aLength;
        if (i_offset < 0) {
            i_offset = 0;
        }
    }

    void setAddress(int a_address) {
        i_address = a_address;
    }

    void setCascadeDeletes(int depth) {
        i_cascadeDelete = depth;
    }

    void setID(int a_id) {
        i_id = a_id;
    }

    void setInstantiationDepth(int a_depth) {
        i_instantionDepth = a_depth;
    }

    void setTransaction(Transaction aTrans) {
        i_trans = aTrans;
    }

    void setUpdateDepth(int a_depth) {
        i_updateDepth = a_depth;
    }

    void trim4(int a_offset, int a_length) {
        byte[] temp = new byte[a_length];
        System.arraycopy(i_bytes, a_offset, temp, 0, a_length);
        i_bytes = temp;
        i_length = a_length;
    }

    void useSlot(int a_adress) {
        i_address = a_adress;
        i_offset = 0;
    }

    void useSlot(int a_adress, int a_length) {
        i_address = a_adress;
        i_offset = 0;
        if (a_length > i_bytes.length) {
            i_bytes = new byte[a_length];
        }
        i_length = a_length;
    }

    void useSlot(int a_id, int a_adress, int a_length) {
        i_id = a_id;
        useSlot(a_adress, a_length);
    }

    void write() {
        if (Deploy.debug) {
            debugCheckBytes();
        }
        i_trans.i_file.writeBytes(this);
    }

    void writeEmbedded() {
        final YapWriter finalThis = this;
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(YapWriter a_bytes) {
                a_bytes.writeEmbedded();
                i_trans.i_stream.writeEmbedded(finalThis, a_bytes);
            }
        });
        
        // TODO: It may be possible to remove the following to 
        // allow indexes to be created from the bytes passed
        // from the client without having to reread. Currently
        // the bytes don't seem to be found and there is a
        // problem with encryption during the write process.

        i_embedded = null; // no reuse !!!
    }

    void writeEmbeddedNull() {
        writeInt(0);
        writeInt(0);
    }

    void writeEncrypt() {
        if (Deploy.debug) {
            debugCheckBytes();
        }
        i_trans.i_stream.i_handlers.encrypt(this);
        i_trans.i_file.writeBytes(this);
        i_trans.i_stream.i_handlers.decrypt(this);
    }


    // turning writing around since our Collection world is the wrong
    // way around
    // TODO: optimize
    final void writeQueryResult(QResult a_qr) {
        int size = a_qr.size();
        writeInt(size);
        i_offset += (size - 1) * YapConst.YAPID_LENGTH;
        int dec = YapConst.YAPID_LENGTH * 2;
        for (int i = 0; i < size; i++) {
            writeInt(a_qr.nextInt());
            i_offset -= dec;
        }
    }

    void writeShortString(String a_string) {
        i_trans.i_stream.i_handlers.i_stringHandler.writeShort(a_string, this);
    }
}
