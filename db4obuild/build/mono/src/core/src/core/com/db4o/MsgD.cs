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

   internal class MsgD : Msg {
      internal YapWriter payLoad;
      
      internal MsgD() : base() {
      }
      
      internal MsgD(String xstring) : base(xstring) {
      }
      
      internal override void fakePayLoad(Transaction transaction) {
      }
      
      internal override YapWriter getByteLoad() {
         return payLoad;
      }
      
      internal override YapWriter getPayLoad() {
         return payLoad;
      }
      
      internal MsgD getWriterForLength(Transaction transaction, int i) {
         MsgD msgd_0_1 = (MsgD)this.clone(transaction);
         msgd_0_1.payLoad = new YapWriter(transaction, i + 9);
         msgd_0_1.writeInt(i_msgID);
         msgd_0_1.writeInt(i);
         if (transaction.i_parentTransaction == null) msgd_0_1.payLoad.append((byte)115); else msgd_0_1.payLoad.append((byte)117);
         return msgd_0_1;
      }
      
      internal MsgD getWriter(Transaction transaction) {
         return getWriterForLength(transaction, 0);
      }
      
      internal MsgD getWriterFor2Ints(Transaction transaction, int i, int i_1_) {
         MsgD msgd_2_1 = getWriterForLength(transaction, 8);
         msgd_2_1.writeInt(i);
         msgd_2_1.writeInt(i_1_);
         return msgd_2_1;
      }
      
      internal MsgD getWriterFor3Ints(Transaction transaction, int i, int i_3_, int i_4_) {
         MsgD msgd_5_1 = getWriterForLength(transaction, 12);
         msgd_5_1.writeInt(i);
         msgd_5_1.writeInt(i_3_);
         msgd_5_1.writeInt(i_4_);
         return msgd_5_1;
      }
      
      internal MsgD getWriterFor4Ints(Transaction transaction, int i, int i_6_, int i_7_, int i_8_) {
         MsgD msgd_9_1 = getWriterForLength(transaction, 16);
         msgd_9_1.writeInt(i);
         msgd_9_1.writeInt(i_6_);
         msgd_9_1.writeInt(i_7_);
         msgd_9_1.writeInt(i_8_);
         return msgd_9_1;
      }
      
      internal MsgD getWriterForIntArray(Transaction transaction, int[] xis, int i) {
         MsgD msgd_10_1 = getWriterForLength(transaction, 4 * (i + 1));
         msgd_10_1.writeInt(i);
         for (int i_11_1 = 0; i_11_1 < i; i_11_1++) msgd_10_1.writeInt(xis[i_11_1]);
         return msgd_10_1;
      }
      
      internal MsgD getWriterForInt(Transaction transaction, int i) {
         MsgD msgd_12_1 = getWriterForLength(transaction, 4);
         msgd_12_1.writeInt(i);
         return msgd_12_1;
      }
      
      internal MsgD getWriterForIntString(Transaction transaction, int i, String xstring) {
         MsgD msgd_13_1 = getWriterForLength(transaction, YapConst.stringIO.Length(xstring) + 8);
         msgd_13_1.writeInt(i);
         msgd_13_1.writeString(xstring);
         return msgd_13_1;
      }
      
      internal MsgD getWriterForLong(Transaction transaction, long l) {
         MsgD msgd_14_1 = getWriterForLength(transaction, 8);
         msgd_14_1.writeLong(l);
         return msgd_14_1;
      }
      
      internal MsgD getWriterForString(Transaction transaction, String xstring) {
         MsgD msgd_15_1 = getWriterForLength(transaction, YapConst.stringIO.Length(xstring) + 4);
         msgd_15_1.writeString(xstring);
         return msgd_15_1;
      }
      
      internal virtual MsgD getWriter(YapWriter yapwriter) {
         MsgD msgd_16_1 = getWriterForLength(yapwriter.getTransaction(), yapwriter.getLength());
         msgd_16_1.payLoad.append(yapwriter._buffer);
         return msgd_16_1;
      }
      
      internal byte[] readBytes() {
         return payLoad.readBytes(readInt());
      }
      
      internal int readInt() {
         return payLoad.readInt();
      }
      
      internal long readLong() {
         return YLong.readLong(payLoad);
      }
      
      internal override Msg readPayLoad(Transaction transaction, YapSocket yapsocket, YapWriter yapwriter) {
         int i1 = yapwriter.readInt();
         if (yapwriter.readByte() == 115 && transaction.i_parentTransaction != null) transaction = transaction.i_parentTransaction;
         MsgD msgd_17_1 = (MsgD)this.clone(transaction);
         msgd_17_1.payLoad = new YapWriter(transaction, i1);
         msgd_17_1.payLoad.read(yapsocket);
         return msgd_17_1;
      }
      
      internal String readString() {
         int i1 = readInt();
         return YapConst.stringIO.read(payLoad, i1);
      }
      
      internal void writeBytes(byte[] xis) {
         writeInt(xis.Length);
         payLoad.append(xis);
      }
      
      internal void writeInt(int i) {
         payLoad.writeInt(i);
      }
      
      internal void writeLong(long l) {
         YLong.writeLong(l, payLoad);
      }
      
      internal void writeString(String xstring) {
         payLoad.writeInt(j4o.lang.JavaSystem.getLengthOf(xstring));
         YapConst.stringIO.write(payLoad, xstring);
      }
   }
}