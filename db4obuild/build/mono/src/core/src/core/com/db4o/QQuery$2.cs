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

   internal class QQuery__2 : Visitor4 {
      private Collection4 val__fieldPath;
      private QResult val__result;
      private QQuery stathis0;
      
      internal QQuery__2(QQuery qquery, Collection4 collection4, QResult qresult) : base() {
         stathis0 = qquery;
         val__fieldPath = collection4;
         val__result = qresult;
      }
      
      public void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         if (qcandidate1.include()) {
            TreeInt treeint1 = new TreeInt(qcandidate1.i_key);
            TreeInt[] treeints1 = new TreeInt[1];
            Iterator4 iterator41 = val__fieldPath.iterator();
            while (iterator41.hasNext()) {
               treeints1[0] = null;
               String xstring1 = (String)iterator41.next();
               if (treeint1 != null) treeint1.traverse(new QQuery__3(this, treeints1, xstring1));
               treeint1 = treeints1[0];
            }
            if (treeint1 != null) treeint1.traverse(new QQuery__4(this));
         }
      }
      
      static internal QQuery access__000(QQuery__2 var_2) {
         return var_2.stathis0;
      }
      
      static internal QResult access__100(QQuery__2 var_2) {
         return var_2.val__result;
      }
   }
}