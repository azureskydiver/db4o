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

   public class QE {
      
      public QE() : base() {
      }
      static internal QE DEFAULT = new QE();
      
      internal virtual QE add(QE qe_0_) {
         return qe_0_;
      }
      
      internal virtual bool identity() {
         return false;
      }
      
      internal virtual bool evaluate(QConObject qconobject, QCandidate qcandidate, Object obj) {
         if (obj == null) return qconobject.getComparator(qcandidate) is Null;
         return qconobject.getComparator(qcandidate).isEqual(obj);
      }
      
      public override bool Equals(Object obj) {
         return j4o.lang.Class.getClassForObject(obj) == j4o.lang.Class.getClassForObject(this);
      }
      
      internal virtual bool not(bool xbool) {
         return xbool;
      }
      
      internal virtual void indexBitMap(bool[] bools) {
         bools[1] = true;
      }
      
      internal virtual bool supportsIndex() {
         return true;
      }
   }
}