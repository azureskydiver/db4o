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
namespace com.db4o {

   internal class YapString : YapIndependantType {
      
      internal YapString() : base() {
      }
      private YapStringIO i_stringIo;
      private static Class i_class = j4o.lang.Class.getClassForObject("");
      private YapReader i_compareTo;
      
      public override void appendEmbedded3(YapWriter yapwriter) {
         YapWriter yapwriter_0_1 = yapwriter.readEmbeddedObject();
         if (yapwriter_0_1 != null) yapwriter.addEmbedded(yapwriter_0_1);
      }
      
      public override bool canHold(Class var_class) {
         return var_class == i_class;
      }
      
      public override void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool) {
      }
      
      static internal String cipher(long l) {
         String xstring1 = Convert.ToString(l);
         String string_1_1 = "";
         for (int i1 = 0; i1 < j4o.lang.JavaSystem.getLengthOf(xstring1); i1++) string_1_1 += j4o.lang.JavaSystem.getCharAt(xstring1, i1);
         return string_1_1;
      }
      
      public override bool Equals(YapDataType yapdatatype) {
         return this == yapdatatype;
      }
      
      public override int getID() {
         return 9;
      }
      
      internal byte getIdentifier() {
         return (byte)83;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override YapClass getYapClass(YapStream yapstream) {
         return yapstream.i_handlers.i_yapClasses[getID() - 1];
      }
      
      static internal String invert(String xstring) {
         StringBuffer stringbuffer1 = new StringBuffer();
         try {
            {
               for (int i1 = j4o.lang.JavaSystem.getLengthOf(xstring) - 1; i1 >= 0; i1--) stringbuffer1.append(j4o.lang.JavaSystem.getCharAt(xstring, i1));
            }
         }  catch (Exception exception) {
            {
               j4o.lang.JavaSystem.printStackTrace(exception);
            }
         }
         return stringbuffer1.ToString();
      }
      
      public override Object indexObject(Transaction transaction, Object obj) {
         if (obj != null) {
            int[] xis1 = (int[])obj;
            return transaction.i_stream.readObjectReaderByAddress(xis1[0], xis1[1]);
         }
         return null;
      }
      
      static internal String licenseEncrypt(String xstring) {
         xstring = xstring.ToLower();
         String string_2_1 = "";
         for (int i1 = 0; i1 < j4o.lang.JavaSystem.getLengthOf(xstring); i1++) {
            char c1 = (char)(j4o.lang.JavaSystem.getCharAt(xstring, i1) + (char)i1 + (char)1);
            string_2_1 += c1;
         }
         return string_2_1;
      }
      
      public override Object read(YapWriter yapwriter) {
         i_lastIo = yapwriter.readEmbeddedObject();
         return read1(i_lastIo);
      }
      
      internal Object read1(YapReader yapreader) {
         if (yapreader == null) return null;
         String xstring1 = readShort(yapreader);
         return xstring1;
      }
      
      public override YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders) {
         return null;
      }
      
      public override void readCandidates(YapReader yapreader, QCandidates qcandidates) {
      }
      
      public override Object readIndexEntry(YapReader yapreader) {
         return new int[]{
            yapreader.readInt(),
yapreader.readInt()         };
      }
      
      public override Object readQuery(Transaction transaction, YapReader yapreader, bool xbool) {
         YapReader yapreader_3_1 = yapreader.readEmbeddedObject(transaction);
         if (xbool && yapreader_3_1 != null) return yapreader_3_1.ToString(transaction);
         return yapreader_3_1;
      }
      
      internal String readShort(YapReader yapreader) {
         int i1 = yapreader.readInt();
         if (i1 > 70000000) throw new CorruptionException();
         if (i1 > 0) return i_stringIo.read(yapreader, i1);
         return "";
      }
      
      internal void setStringIo(YapStringIO yapstringio) {
         i_stringIo = yapstringio;
      }
      
      public override bool supportsIndex() {
         return true;
      }
      
      public override void writeIndexEntry(YapWriter yapwriter, Object obj) {
         if (obj == null) {
            yapwriter.writeInt(0);
            yapwriter.writeInt(0);
         } else {
            int[] xis1 = (int[])obj;
            yapwriter.writeInt(xis1[0]);
            yapwriter.writeInt(xis1[1]);
         }
      }
      
      public override void writeNew(Object obj, YapWriter yapwriter) {
         if (obj == null) yapwriter.writeEmbeddedNull(); else {
            String xstring1 = (String)obj;
            int i1 = i_stringIo.Length(xstring1);
            YapWriter yapwriter_4_1 = new YapWriter(yapwriter.getTransaction(), i1);
            yapwriter_4_1.writeInt(j4o.lang.JavaSystem.getLengthOf(xstring1));
            i_stringIo.write(yapwriter_4_1, xstring1);
            yapwriter_4_1.setID(yapwriter._offset);
            i_lastIo = yapwriter_4_1;
            yapwriter.getStream().writeEmbedded(yapwriter, yapwriter_4_1);
            yapwriter.incrementOffset(4);
            yapwriter.writeInt(i1);
         }
      }
      
      internal void writeShort(String xstring, YapReader yapreader) {
         if (xstring == null) yapreader.writeInt(0); else {
            yapreader.writeInt(j4o.lang.JavaSystem.getLengthOf(xstring));
            i_stringIo.write(yapreader, xstring);
         }
      }
      
      static internal String fromIntArray(int[] xis) {
         StringBuffer stringbuffer1 = new StringBuffer();
         try {
            {
               for (int i1 = 0; i1 < xis.Length; i1++) stringbuffer1.append((char)xis[i1]);
            }
         }  catch (Exception exception) {
            {
               j4o.lang.JavaSystem.printStackTrace(exception);
            }
         }
         return stringbuffer1.ToString();
      }
      
      public override int getType() {
         return 1;
      }
      
      private YapReader val(Object obj) {
         if (obj is YapReader) return (YapReader)obj;
         if (obj is String) {
            String xstring1 = (String)obj;
            YapReader yapreader1 = new YapReader(i_stringIo.Length(xstring1));
            writeShort(xstring1, yapreader1);
            return yapreader1;
         }
         return null;
      }
      
      public override void prepareLastIoComparison(Transaction transaction, Object obj) {
         if (obj == null) i_compareTo = null; else i_compareTo = i_lastIo;
      }
      
      public override YapComparable prepareComparison(Object obj) {
         if (obj == null) {
            i_compareTo = null;
            return Null.INSTANCE;
         }
         i_compareTo = val(obj);
         return this;
      }
      
      public override int compareTo(Object obj) {
         if (i_compareTo == null) {
            if (obj == null) return 0;
            return 1;
         }
         return compare(i_compareTo, val(obj));
      }
      
      public override bool isEqual(Object obj) {
         if (i_compareTo == null) return obj == null;
         return i_compareTo.containsTheSame(val(obj));
      }
      
      public override bool isGreater(Object obj) {
         if (i_compareTo == null) return obj != null;
         return compare(i_compareTo, val(obj)) > 0;
      }
      
      public override bool isSmaller(Object obj) {
         if (i_compareTo == null) return false;
         return compare(i_compareTo, val(obj)) < 0;
      }
      
      internal int compare(YapReader yapreader, YapReader yapreader_5_) {
         if (yapreader == null) {
            if (yapreader_5_ == null) return 0;
            return 1;
         }
         if (yapreader_5_ == null) return -1;
         return compare(yapreader._buffer, yapreader_5_._buffer);
      }
      
      static internal int compare(byte[] xis, byte[] is_6_) {
         int i1 = xis.Length < is_6_.Length ? xis.Length : is_6_.Length;
         int i_7_1 = 4;
         for (int i_8_1 = i_7_1; i_8_1 < i1; i_8_1++) {
            if (xis[i_8_1] != is_6_[i_8_1]) return is_6_[i_8_1] - xis[i_8_1];
         }
         return is_6_.Length - xis.Length;
      }
   }
}