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
using com.db4o.foundation;
namespace com.db4o {

   internal class YapReferences : Runnable {
      internal Object _queue;
      private YapStream _stream;
      private SimpleTimer _timer;
      public bool _weak;
      
      internal YapReferences(YapStream yapstream) : base() {
         _stream = yapstream;
         _weak = !(yapstream is YapObjectCarrier) && Platform.hasWeakReferences() && yapstream.i_config.i_weakReferences;
         _queue = _weak ? Platform.createReferenceQueue() : null;
      }
      
      internal Object createYapRef(YapObject yapobject, Object obj) {
         if (!_weak) return obj;
         return Platform.createYapRef(_queue, yapobject, obj);
      }
      
      public void run() {
         if (_weak) Platform.pollReferenceQueue(_stream, _queue);
      }
      
      internal void startTimer() {
         if (_weak && _stream.i_config.i_weakReferenceCollectionInterval > 0 && _timer == null) _timer = new SimpleTimer(this, _stream.i_config.i_weakReferenceCollectionInterval, "db4o WeakReference collector");
      }
      
      internal void stopTimer() {
         if (_timer != null) {
            _timer.stop();
            _timer = null;
         }
      }
   }
}