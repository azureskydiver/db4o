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

   internal class YapSocketFake : YapSocket {
      internal YapServer i_server;
      private YapSocketFake i_affiliate;
      private ByteBuffer4 i_uploadBuffer;
      private ByteBuffer4 i_downloadBuffer;
      
      public YapSocketFake(YapServer yapserver) : base() {
         i_server = yapserver;
         i_uploadBuffer = new ByteBuffer4(((Config4Impl)yapserver.configure()).i_timeoutClientSocket);
         i_downloadBuffer = new ByteBuffer4(((Config4Impl)yapserver.configure()).i_timeoutClientSocket);
      }
      
      public YapSocketFake(YapServer yapserver, YapSocketFake yapsocketfake_0_) : this(yapserver) {
         i_affiliate = yapsocketfake_0_;
         yapsocketfake_0_.i_affiliate = this;
         i_downloadBuffer = yapsocketfake_0_.i_uploadBuffer;
         i_uploadBuffer = yapsocketfake_0_.i_downloadBuffer;
      }
      
      public override void close() {
         if (i_affiliate != null) {
            YapSocketFake yapsocketfake_1_1 = i_affiliate;
            i_affiliate = null;
            yapsocketfake_1_1.close();
         }
         i_affiliate = null;
      }
      
      public override void flush() {
      }
      
      public override String getHostName() {
         return null;
      }
      
      public bool isClosed() {
         return i_affiliate == null;
      }
      
      public override int read() {
         return i_downloadBuffer.read();
      }
      
      public override int read(byte[] xis, int i, int i_2_) {
         return i_downloadBuffer.read(xis, i, i_2_);
      }
      
      public override void setSoTimeout(int i) {
         i_uploadBuffer.setTimeout(i);
         i_downloadBuffer.setTimeout(i);
      }
      
      public override void write(byte[] xis) {
         i_uploadBuffer.write(xis);
      }
      
      public override void write(int i) {
         i_uploadBuffer.write(i);
      }
   }
}