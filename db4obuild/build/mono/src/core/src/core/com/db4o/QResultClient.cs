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

   internal class QResultClient : QResult {
      private Object[] i_prefetched = new Object[10];
      private int i_remainingObjects;
      private int i_prefetchCount = 10;
      
      internal QResultClient(Transaction transaction) : base(transaction) {
      }
      
      public override bool hasNext() {
         lock (this.streamLock()) {
            if (i_remainingObjects > 0) return true;
            return base.hasNext();
         }
      }
      
      public override Object next() {
         lock (this.streamLock()) {
            YapClient yapclient1 = (YapClient)i_trans.i_stream;
            yapclient1.checkClosed();
            if (i_remainingObjects < 1 && base.hasNext()) i_remainingObjects = yapclient1.prefetchObjects(this, i_prefetched, i_prefetchCount);
            i_remainingObjects--;
            if (i_remainingObjects < 0) return null;
            if (i_prefetched[i_remainingObjects] == null) return next();
            return this.activate(i_prefetched[i_remainingObjects]);
         }
      }
      
      public override void reset() {
         lock (this.streamLock()) {
            i_remainingObjects = 0;
            base.reset();
         }
      }
   }
}