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
using com.db4o.ext;
namespace com.db4o {

   internal class YapObject : YapMeta, ObjectInfo {
      private YapClass i_yapClass;
      internal Object i_object;
      internal VirtualAttributes i_virtualAttributes;
      private YapObject id_preceding;
      private YapObject id_subsequent;
      private int id_size;
      private YapObject hc_preceding;
      private YapObject hc_subsequent;
      private int hc_size;
      private int hc_code;
      
      internal YapObject(int i) : base() {
         i_id = i;
      }
      
      internal YapObject(YapClass yapclass, int i) : base() {
         i_yapClass = yapclass;
         i_id = i;
      }
      
      internal void activate(Transaction transaction, Object obj, int i, bool xbool) {
         activate1(transaction, obj, i, xbool);
         transaction.i_stream.activate3CheckStill(transaction);
      }
      
      internal void activate1(Transaction transaction, Object obj, int i, bool xbool) {
         if (obj is Db4oTypeImpl) i = ((Db4oTypeImpl)obj).adjustReadDepth(i);
         if (i > 0) {
            YapStream yapstream1 = transaction.i_stream;
            if (xbool) {
               if (yapstream1.i_config.i_messageLevel > 2) yapstream1.message("" + this.getID() + " refresh " + i_yapClass.getName());
            } else {
               if (this.isActive() && obj != null) {
                  if (i > 1) {
                     if (i_yapClass.i_config != null) i = i_yapClass.i_config.adjustActivationDepth(i);
                     i_yapClass.activateFields(transaction, obj, i - 1);
                  }
                  return;
               }
               if (yapstream1.i_config.i_messageLevel > 2) yapstream1.message("" + this.getID() + " activate " + i_yapClass.getName());
            }
            read(transaction, null, obj, i, 0);
         }
      }
      
      internal void addToIDTree(YapStream yapstream) {
         if (!(i_yapClass is YapClassPrimitive)) yapstream.idTreeAdd(this);
      }
      
      internal bool continueSet(Transaction transaction, int i) {
         if (this.bitIsTrue(4)) {
            if (!i_yapClass.stateOKAndAncestors()) return false;
            YapStream yapstream1 = transaction.i_stream;
            this.bitFalse(4);
            Object obj1 = getObject();
            int i_0_1 = this.getID();
            int i_1_1 = ownLength();
            int i_2_1 = -1;
            if (!yapstream1.isClient()) i_2_1 = ((YapFile)yapstream1).getSlot(i_1_1);
            transaction.setPointer(i_0_1, i_2_1, i_1_1);
            YapWriter yapwriter1 = new YapWriter(transaction, i_1_1);
            yapwriter1.useSlot(i_0_1, i_2_1, i_1_1);
            yapwriter1.setUpdateDepth(i);
            yapwriter1.writeInt(i_yapClass.getID());
            i_yapClass.marshallNew(this, yapwriter1, obj1);
            yapstream1.writeNew(i_yapClass, yapwriter1);
            i_yapClass.dispatchEvent(yapstream1, obj1, 4);
            i_object = yapstream1.i_references.createYapRef(this, obj1);
            this.setStateClean();
            this.endProcessing();
         }
         return true;
      }
      
      internal void deactivate(Transaction transaction, int i) {
         if (i > 0) {
            Object obj1 = getObject();
            if (obj1 != null) {
               if (obj1 is Db4oTypeImpl) ((Db4oTypeImpl)obj1).preDeactivate();
               YapStream yapstream1 = transaction.i_stream;
               if (yapstream1.i_config.i_messageLevel > 2) yapstream1.message("" + this.getID() + " deactivate " + i_yapClass.getName());
               this.setStateDeactivated();
               i_yapClass.deactivate(transaction, obj1, i);
            }
         }
      }
      
      internal override byte getIdentifier() {
         return (byte)79;
      }
      
      internal Class getJavaClass() {
         return i_yapClass.getJavaClass();
      }
      
      public Object getObject() {
         if (Platform.hasWeakReferences()) return Platform.getYapRefObject(i_object);
         return i_object;
      }
      
      private Transaction getTrans() {
         if (i_yapClass != null) {
            YapStream yapstream1 = i_yapClass.getStream();
            if (yapstream1 != null) return yapstream1.getTransaction();
         }
         return null;
      }
      
      public Db4oUUID getUUID() {
         VirtualAttributes virtualattributes1 = virtualAttributes(getTrans());
         if (virtualattributes1 != null && virtualattributes1.i_database != null) return new Db4oUUID(virtualattributes1.i_uuid, virtualattributes1.i_database.i_signature);
         return null;
      }
      
      internal YapClass getYapClass() {
         return i_yapClass;
      }
      
      internal override int ownLength() {
         return i_yapClass.objectLength();
      }
      
      internal Object read(Transaction transaction, YapWriter yapwriter, Object obj, int i, int i_3_) {
         if (this.beginProcessing()) {
            YapStream yapstream1 = transaction.i_stream;
            if (yapwriter == null) yapwriter = yapstream1.readWriterByID(transaction, this.getID());
            if (yapwriter != null) {
               i_yapClass = readYapClass(yapwriter);
               if (i_yapClass == null) return null;
               yapwriter.setInstantiationDepth(i);
               yapwriter.setUpdateDepth(i_3_);
               if (i_3_ == -1) obj = i_yapClass.instantiateTransient(this, obj, yapwriter); else obj = i_yapClass.instantiate(this, obj, yapwriter, i_3_ == 1);
            }
            this.endProcessing();
         }
         return obj;
      }
      
      internal Object readPrefetch(YapStream yapstream, Transaction transaction, YapWriter yapwriter) {
         Object obj1 = null;
         if (this.beginProcessing()) {
            i_yapClass = readYapClass(yapwriter);
            if (i_yapClass == null) return null;
            yapwriter.setInstantiationDepth(i_yapClass.configOrAncestorConfig() == null ? 1 : 0);
            obj1 = i_yapClass.instantiate(this, getObject(), yapwriter, true);
            this.endProcessing();
         }
         return obj1;
      }
      
      internal override void readThis(Transaction transaction, YapReader yapreader) {
      }
      
      private YapClass readYapClass(YapWriter yapwriter) {
         return yapwriter.getStream().getYapClass(yapwriter.readInt());
      }
      
      internal override void setID(YapStream yapstream, int i) {
         i_id = i;
      }
      
      internal void setObjectWeak(YapStream yapstream, Object obj) {
         if (yapstream.i_references._weak) {
            if (i_object != null) Platform.killYapRef(i_object);
            i_object = Platform.createYapRef(yapstream.i_references._queue, this, obj);
         } else i_object = obj;
      }
      
      internal void setObject(Object obj) {
         i_object = obj;
      }
      
      internal void setStateOnRead(YapWriter yapwriter) {
      }
      
      internal bool store(Transaction transaction, YapClass yapclass, Object obj, int i) {
         i_object = obj;
         this.writeObjectBegin();
         YapStream yapstream1 = transaction.i_stream;
         i_yapClass = yapclass;
         if (i_yapClass.getID() != 11) {
            setID(yapstream1, yapstream1.newUserObject());
            this.beginProcessing();
            this.bitTrue(4);
            if (!(i_yapClass is YapClassPrimitive)) return true;
            continueSet(transaction, i);
         }
         return false;
      }
      
      internal VirtualAttributes virtualAttributes(Transaction transaction) {
         if (i_virtualAttributes == null && i_yapClass.hasVirtualAttributes() && transaction != null) {
            i_virtualAttributes = new VirtualAttributes();
            i_yapClass.readVirtualAttributes(transaction, this);
         }
         return i_virtualAttributes;
      }
      
      internal override void writeThis(YapWriter yapwriter) {
      }
      
      internal void writeUpdate(Transaction transaction, int i) {
         continueSet(transaction, i);
         if (this.beginProcessing()) {
            Object obj1 = getObject();
            if (i_yapClass.dispatchEvent(transaction.i_stream, obj1, 9)) {
               if (!this.isActive() || obj1 == null) this.endProcessing(); else {
                  if (transaction.i_stream.i_config.i_messageLevel > 1) transaction.i_stream.message("" + this.getID() + " update " + i_yapClass.getName());
                  this.setStateClean();
                  transaction.writeUpdateDeleteMembers(this.getID(), i_yapClass, YapHandlers.arrayType(obj1), 0);
                  i_yapClass.marshallUpdate(transaction, this.getID(), i, this, obj1);
               }
            } else this.endProcessing();
         }
      }
      
      internal YapObject hc_add(YapObject yapobject_4_) {
         Object obj1 = yapobject_4_.getObject();
         if (obj1 != null) {
            yapobject_4_.hc_preceding = null;
            yapobject_4_.hc_subsequent = null;
            yapobject_4_.hc_size = 1;
            yapobject_4_.hc_code = hc_getCode(obj1);
            return hc_add1(yapobject_4_);
         }
         return this;
      }
      
      private YapObject hc_add1(YapObject yapobject_5_) {
         int i1 = hc_compare(yapobject_5_);
         if (i1 < 0) {
            if (hc_preceding == null) {
               hc_preceding = yapobject_5_;
               hc_size++;
            } else {
               hc_preceding = hc_preceding.hc_add1(yapobject_5_);
               if (hc_subsequent == null) return hc_rotateRight();
               return hc_balance();
            }
         } else if (hc_subsequent == null) {
            hc_subsequent = yapobject_5_;
            hc_size++;
         } else {
            hc_subsequent = hc_subsequent.hc_add1(yapobject_5_);
            if (hc_preceding == null) return hc_rotateLeft();
            return hc_balance();
         }
         return this;
      }
      
      private YapObject hc_balance() {
         int i1 = hc_subsequent.hc_size - hc_preceding.hc_size;
         if (i1 < -2) return hc_rotateRight();
         if (i1 > 2) return hc_rotateLeft();
         hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
         return this;
      }
      
      private void hc_calculateSize() {
         if (hc_preceding == null) {
            if (hc_subsequent == null) hc_size = 1; else hc_size = hc_subsequent.hc_size + 1;
         } else if (hc_subsequent == null) hc_size = hc_preceding.hc_size + 1; else hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
      }
      
      private int hc_compare(YapObject yapobject_6_) {
         int i1 = yapobject_6_.hc_code - hc_code;
         if (i1 == 0) i1 = yapobject_6_.i_id - i_id;
         return i1;
      }
      
      internal YapObject hc_find(Object obj) {
         return hc_find(hc_getCode(obj), obj);
      }
      
      private YapObject hc_find(int i, Object obj) {
         int i_7_1 = i - hc_code;
         if (i_7_1 < 0) {
            if (hc_preceding != null) return hc_preceding.hc_find(i, obj);
         } else if (i_7_1 > 0) {
            if (hc_subsequent != null) return hc_subsequent.hc_find(i, obj);
         } else {
            if (obj == getObject()) return this;
            if (hc_preceding != null) {
               YapObject yapobject_8_1 = hc_preceding.hc_find(i, obj);
               if (yapobject_8_1 != null) return yapobject_8_1;
            }
            if (hc_subsequent != null) return hc_subsequent.hc_find(i, obj);
         }
         return null;
      }
      
      private int hc_getCode(Object obj) {
         int i1 = j4o.lang.JavaSystem.identityHashCode(obj);
         if (i1 < 0) i1 ^= -1;
         return i1;
      }
      
      private YapObject hc_rotateLeft() {
         YapObject yapobject_9_1 = hc_subsequent;
         hc_subsequent = yapobject_9_1.hc_preceding;
         hc_calculateSize();
         yapobject_9_1.hc_preceding = this;
         if (yapobject_9_1.hc_subsequent == null) yapobject_9_1.hc_size = 1 + hc_size; else yapobject_9_1.hc_size = 1 + hc_size + yapobject_9_1.hc_subsequent.hc_size;
         return yapobject_9_1;
      }
      
      private YapObject hc_rotateRight() {
         YapObject yapobject_10_1 = hc_preceding;
         hc_preceding = yapobject_10_1.hc_subsequent;
         hc_calculateSize();
         yapobject_10_1.hc_subsequent = this;
         if (yapobject_10_1.hc_preceding == null) yapobject_10_1.hc_size = 1 + hc_size; else yapobject_10_1.hc_size = 1 + hc_size + yapobject_10_1.hc_preceding.hc_size;
         return yapobject_10_1;
      }
      
      private YapObject hc_rotateSmallestUp() {
         if (hc_preceding != null) {
            hc_preceding = hc_preceding.hc_rotateSmallestUp();
            return hc_rotateRight();
         }
         return this;
      }
      
      internal YapObject hc_remove(YapObject yapobject_11_) {
         if (this == yapobject_11_) return hc_remove();
         int i1 = hc_compare(yapobject_11_);
         if (i1 <= 0 && hc_preceding != null) hc_preceding = hc_preceding.hc_remove(yapobject_11_);
         if (i1 >= 0 && hc_subsequent != null) hc_subsequent = hc_subsequent.hc_remove(yapobject_11_);
         hc_calculateSize();
         return this;
      }
      
      private YapObject hc_remove() {
         if (hc_subsequent != null && hc_preceding != null) {
            hc_subsequent = hc_subsequent.hc_rotateSmallestUp();
            hc_subsequent.hc_preceding = hc_preceding;
            hc_subsequent.hc_calculateSize();
            return hc_subsequent;
         }
         if (hc_subsequent != null) return hc_subsequent;
         return hc_preceding;
      }
      
      internal YapObject id_add(YapObject yapobject_12_) {
         yapobject_12_.id_preceding = null;
         yapobject_12_.id_subsequent = null;
         yapobject_12_.id_size = 1;
         return id_add1(yapobject_12_);
      }
      
      private YapObject id_add1(YapObject yapobject_13_) {
         int i1 = yapobject_13_.i_id - i_id;
         if (i1 < 0) {
            if (id_preceding == null) {
               id_preceding = yapobject_13_;
               id_size++;
            } else {
               id_preceding = id_preceding.id_add1(yapobject_13_);
               if (id_subsequent == null) return id_rotateRight();
               return id_balance();
            }
         } else if (id_subsequent == null) {
            id_subsequent = yapobject_13_;
            id_size++;
         } else {
            id_subsequent = id_subsequent.id_add1(yapobject_13_);
            if (id_preceding == null) return id_rotateLeft();
            return id_balance();
         }
         return this;
      }
      
      private YapObject id_balance() {
         int i1 = id_subsequent.id_size - id_preceding.id_size;
         if (i1 < -2) return id_rotateRight();
         if (i1 > 2) return id_rotateLeft();
         id_size = id_preceding.id_size + id_subsequent.id_size + 1;
         return this;
      }
      
      private void id_calculateSize() {
         if (id_preceding == null) {
            if (id_subsequent == null) id_size = 1; else id_size = id_subsequent.id_size + 1;
         } else if (id_subsequent == null) id_size = id_preceding.id_size + 1; else id_size = id_preceding.id_size + id_subsequent.id_size + 1;
      }
      
      internal YapObject id_find(int i) {
         int i_14_1 = i - i_id;
         if (i_14_1 > 0) {
            if (id_subsequent != null) return id_subsequent.id_find(i);
         } else if (i_14_1 < 0) {
            if (id_preceding != null) return id_preceding.id_find(i);
         } else return this;
         return null;
      }
      
      private YapObject id_rotateLeft() {
         YapObject yapobject_15_1 = id_subsequent;
         id_subsequent = yapobject_15_1.id_preceding;
         id_calculateSize();
         yapobject_15_1.id_preceding = this;
         if (yapobject_15_1.id_subsequent == null) yapobject_15_1.id_size = id_size + 1; else yapobject_15_1.id_size = id_size + 1 + yapobject_15_1.id_subsequent.id_size;
         return yapobject_15_1;
      }
      
      private YapObject id_rotateRight() {
         YapObject yapobject_16_1 = id_preceding;
         id_preceding = yapobject_16_1.id_subsequent;
         id_calculateSize();
         yapobject_16_1.id_subsequent = this;
         if (yapobject_16_1.id_preceding == null) yapobject_16_1.id_size = id_size + 1; else yapobject_16_1.id_size = id_size + 1 + yapobject_16_1.id_preceding.id_size;
         return yapobject_16_1;
      }
      
      private YapObject id_rotateSmallestUp() {
         if (id_preceding != null) {
            id_preceding = id_preceding.id_rotateSmallestUp();
            return id_rotateRight();
         }
         return this;
      }
      
      internal YapObject id_remove(int i) {
         int i_17_1 = i - i_id;
         if (i_17_1 < 0) {
            if (id_preceding != null) id_preceding = id_preceding.id_remove(i);
         } else if (i_17_1 > 0) {
            if (id_subsequent != null) id_subsequent = id_subsequent.id_remove(i);
         } else return id_remove();
         id_calculateSize();
         return this;
      }
      
      private YapObject id_remove() {
         if (id_subsequent != null && id_preceding != null) {
            id_subsequent = id_subsequent.id_rotateSmallestUp();
            id_subsequent.id_preceding = id_preceding;
            id_subsequent.id_calculateSize();
            return id_subsequent;
         }
         if (id_subsequent != null) return id_subsequent;
         return id_preceding;
      }
      
      public override String ToString() {
         try {
            {
               int i1 = this.getID();
               String xstring1 = "YapObject\nID=" + i1;
               if (i_yapClass != null) {
                  YapStream yapstream1 = i_yapClass.getStream();
                  if (yapstream1 != null && i1 > 0) {
                     YapWriter yapwriter1 = yapstream1.readWriterByID(yapstream1.getTransaction(), i1);
                     if (yapwriter1 != null) xstring1 += "\nAddress=" + yapwriter1.getAddress();
                     YapClass yapclass1 = readYapClass(yapwriter1);
                     if (yapclass1 != i_yapClass) xstring1 += "\nYapClass corruption"; else xstring1 += yapclass1.ToString(yapwriter1, this, 0, 5);
                  }
               }
               Object obj1 = getObject();
               if (obj1 == null) xstring1 += "\nfor [null]"; else {
                  String string_18_1 = "";
                  try {
                     {
                        string_18_1 = obj1.ToString();
                     }
                  }  catch (Exception exception) {
                     {
                     }
                  }
                  Class var_class1 = j4o.lang.Class.getClassForObject(obj1);
                  xstring1 += "\n" + var_class1.getName() + "\n" + (String)string_18_1;
               }
               return xstring1;
            }
         }  catch (Exception exception) {
            {
               j4o.lang.JavaSystem.printStackTrace(exception);
               return "Exception in YapObject analyzer";
            }
         }
      }
   }
}