/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using j4o.io;
namespace com.db4o {

   public class YapWriter : YapReader {
      private int i_address;
      private int _addressOffset;
      private int i_cascadeDelete;
      private Tree i_embedded;
      private int i_id;
      private int i_instantionDepth;
      private int i_length;
      internal Transaction i_trans;
      private int i_updateDepth = 1;
      
      internal YapWriter(Transaction transaction, int i) : base() {
         i_trans = transaction;
         i_length = i;
         _buffer = new byte[i_length];
      }
      
      internal YapWriter(Transaction transaction, int i, int i_0_) : this(transaction, i_0_) {
         i_address = i;
      }
      
      internal YapWriter(YapWriter yapwriter_1_, YapWriter[] yapwriters, int i) : base() {
         yapwriters[i++] = this;
         int i_2_1 = yapwriter_1_.readInt();
         i_length = yapwriter_1_.readInt();
         i_id = yapwriter_1_.readInt();
         yapwriters[i_2_1].addEmbedded(this);
         i_address = yapwriter_1_.readInt();
         i_trans = yapwriter_1_.getTransaction();
         _buffer = new byte[i_length];
         j4o.lang.JavaSystem.arraycopy(yapwriter_1_._buffer, yapwriter_1_._offset, _buffer, 0, i_length);
         yapwriter_1_._offset += i_length;
         if (i < yapwriters.Length) new YapWriter(yapwriter_1_, yapwriters, i);
      }
      
      internal void addEmbedded(YapWriter yapwriter_3_) {
         i_embedded = Tree.add(i_embedded, new TreeIntObject(yapwriter_3_.getID(), yapwriter_3_));
      }
      
      internal int appendTo(YapWriter yapwriter_4_, int i) {
         i++;
         yapwriter_4_.writeInt(i_length);
         yapwriter_4_.writeInt(i_id);
         yapwriter_4_.writeInt(i_address);
         yapwriter_4_.append(_buffer);
         int[] xis1 = {
            i         };
         int i_5_1 = i;
         forEachEmbedded(new YapWriter__1(this, yapwriter_4_, i_5_1, xis1));
         return xis1[0];
      }
      
      internal int cascadeDeletes() {
         return i_cascadeDelete;
      }
      
      internal void debugCheckBytes() {
      }
      
      internal int embeddedCount() {
         int[] xis1 = {
            0         };
         forEachEmbedded(new YapWriter__2(this, xis1));
         return xis1[0];
      }
      
      internal int embeddedLength() {
         int[] xis1 = {
            0         };
         forEachEmbedded(new YapWriter__3(this, xis1));
         return xis1[0];
      }
      
      internal void forEachEmbedded(VisitorYapBytes visitoryapbytes) {
         if (i_embedded != null) i_embedded.traverse(new YapWriter__4(this, visitoryapbytes));
      }
      
      internal int getAddress() {
         return i_address;
      }
      
      internal int addressOffset() {
         return _addressOffset;
      }
      
      internal int getID() {
         return i_id;
      }
      
      internal int getInstantiationDepth() {
         return i_instantionDepth;
      }
      
      internal override int getLength() {
         return i_length;
      }
      
      internal YapStream getStream() {
         return i_trans.i_stream;
      }
      
      internal Transaction getTransaction() {
         return i_trans;
      }
      
      internal int getUpdateDepth() {
         return i_updateDepth;
      }
      
      internal byte[] getWrittenBytes() {
         byte[] xis1 = new byte[_offset];
         j4o.lang.JavaSystem.arraycopy(_buffer, 0, xis1, 0, _offset);
         return xis1;
      }
      
      internal void read() {
         i_trans.i_stream.readBytes(_buffer, i_address, _addressOffset, i_length);
      }
      
      internal void read(YapSocket yapsocket) {
         int i1 = 0;
         int i_7_1;
         for (int i_6_1 = i_length; i_6_1 > 0; i_6_1 -= i_7_1) {
            i_7_1 = yapsocket.read(_buffer, i1, i_6_1);
            i1 += i_7_1;
         }
      }
      
      internal YapWriter readEmbeddedObject() {
         int i1 = this.readInt();
         int i_8_1 = this.readInt();
         TreeInt treeint1 = TreeInt.find(i_embedded, i1);
         if (treeint1 != null) return (YapWriter)((TreeIntObject)treeint1).i_object;
         YapWriter yapwriter_9_1 = i_trans.i_stream.readObjectWriterByAddress(i_trans, i1, i_8_1);
         if (yapwriter_9_1 != null) yapwriter_9_1.setID(i1);
         return yapwriter_9_1;
      }
      
      internal YapWriter readYapBytes() {
         int i1 = this.readInt();
         if (i1 == 0) return null;
         YapWriter yapwriter_10_1 = new YapWriter(i_trans, i1);
         j4o.lang.JavaSystem.arraycopy(_buffer, _offset, yapwriter_10_1._buffer, 0, i1);
         _offset += i1;
         return yapwriter_10_1;
      }
      
      internal void removeFirstBytes(int i) {
         i_length -= i;
         byte[] xis1 = new byte[i_length];
         j4o.lang.JavaSystem.arraycopy(_buffer, i, xis1, 0, i_length);
         _buffer = xis1;
         _offset -= i;
         if (_offset < 0) _offset = 0;
      }
      
      internal void address(int i) {
         i_address = i;
      }
      
      internal void setCascadeDeletes(int i) {
         i_cascadeDelete = i;
      }
      
      internal void setID(int i) {
         i_id = i;
      }
      
      internal void setInstantiationDepth(int i) {
         i_instantionDepth = i;
      }
      
      internal void setTransaction(Transaction transaction) {
         i_trans = transaction;
      }
      
      internal void setUpdateDepth(int i) {
         i_updateDepth = i;
      }
      
      internal void trim4(int i, int i_11_) {
         byte[] xis1 = new byte[i_11_];
         j4o.lang.JavaSystem.arraycopy(_buffer, i, xis1, 0, i_11_);
         _buffer = xis1;
         i_length = i_11_;
      }
      
      internal void useSlot(int i) {
         i_address = i;
         _offset = 0;
      }
      
      internal void useSlot(int i, int i_12_) {
         i_address = i;
         _offset = 0;
         if (i_12_ > _buffer.Length) _buffer = new byte[i_12_];
         i_length = i_12_;
      }
      
      internal void useSlot(int i, int i_13_, int i_14_) {
         i_id = i;
         useSlot(i_13_, i_14_);
      }
      
      internal void write() {
         i_trans.i_file.writeBytes(this);
      }
      
      internal void writeEmbedded() {
         YapWriter yapwriter_15_1 = this;
         forEachEmbedded(new YapWriter__5(this, yapwriter_15_1));
         i_embedded = null;
      }
      
      internal void writeEmbeddedNull() {
         this.writeInt(0);
         this.writeInt(0);
      }
      
      internal void writeEncrypt() {
         i_trans.i_stream.i_handlers.encrypt(this);
         i_trans.i_file.writeBytes(this);
         i_trans.i_stream.i_handlers.decrypt(this);
      }
      
      internal void writeQueryResult(QResult qresult) {
         int i1 = qresult.size();
         this.writeInt(i1);
         _offset += (i1 - 1) * 4;
         int i_16_1 = 8;
         for (int i_17_1 = 0; i_17_1 < i1; i_17_1++) {
            this.writeInt(qresult.nextInt());
            _offset -= i_16_1;
         }
      }
      
      internal void writeShortString(String xstring) {
         i_trans.i_stream.i_handlers.i_stringHandler.writeShort(xstring, this);
      }
      
      public void moveForward(int i) {
         _addressOffset += i;
      }
      
      public void writeForward() {
         write();
         _addressOffset += i_length;
         _offset = 0;
      }
   }
}