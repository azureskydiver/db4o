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

   internal class QCandidates__2 : Visitor4 {
      private int[] val__placement;
      private bool val__major;
      private QCandidates stathis0;
      
      internal QCandidates__2(QCandidates qcandidates, int[] xis, bool xbool) : base() {
         stathis0 = qcandidates;
         val__placement = xis;
         val__major = xbool;
      }
      
      public void visit(Object obj) {
         QOrder qorder1 = (QOrder)obj;
         QCandidate qcandidate1 = qorder1.i_candidate.getRoot();
         qcandidate1.hintOrder(val__placement[0]++, val__major);
      }
   }
}