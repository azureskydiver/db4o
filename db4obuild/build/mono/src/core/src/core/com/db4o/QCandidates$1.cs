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

   internal class QCandidates__1 : Visitor4 {
      private bool val__major;
      private int[] val__placement;
      private QCandidates stathis0;
      
      internal QCandidates__1(QCandidates qcandidates, bool xbool, int[] xis) : base() {
         stathis0 = qcandidates;
         val__major = xbool;
         val__placement = xis;
      }
      
      public void visit(Object obj) {
         ((QCandidate)obj).hintOrder(0, val__major);
         ((QCandidate)obj).hintOrder(val__placement[0]++, !val__major);
      }
   }
}