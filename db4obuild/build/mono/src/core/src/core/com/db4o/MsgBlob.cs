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

   abstract internal class MsgBlob : MsgD {
      
      internal MsgBlob() : base() {
      }
      internal BlobImpl i_blob;
      internal int i_currentByte;
      internal int i_length;
      
      internal double getStatus() {
         if (i_length != 0) return (double)i_currentByte / (double)i_length;
         return -99.0;
      }
      
      abstract internal void processClient(YapSocket yapsocket);
      
      internal BlobImpl serverGetBlobImpl() {
         Object obj1 = null;
         int i1 = payLoad.readInt();
         YapStream yapstream1 = this.getStream();
         BlobImpl blobimpl1;
         lock (yapstream1.i_lock) {
            blobimpl1 = (BlobImpl)yapstream1.getByID1(this.getTransaction(), (long)i1);
            yapstream1.activate1(this.getTransaction(), blobimpl1, 3);
         }
         return blobimpl1;
      }
      
      protected void copy(YapSocket yapsocket, OutputStream outputstream, int i, bool xbool) {
         BufferedOutputStream bufferedoutputstream1 = new BufferedOutputStream(outputstream);
         byte[] xis1 = new byte[4096];
         int i_0_1 = 0;
         while (i_0_1 < i) {
            int i_1_1 = i - i_0_1;
            int i_2_1 = i_1_1 < xis1.Length ? i_1_1 : xis1.Length;
            int i_3_1 = yapsocket.read(xis1, 0, i_2_1);
            bufferedoutputstream1.write(xis1, 0, i_3_1);
            i_0_1 += i_3_1;
            if (xbool) i_currentByte += i_3_1;
         }
         bufferedoutputstream1.flush();
         bufferedoutputstream1.close();
      }
      
      protected void copy(InputStream inputstream, YapSocket yapsocket, bool xbool) {
         BufferedInputStream bufferedinputstream1 = new BufferedInputStream(inputstream);
         byte[] xis1 = new byte[4096];
         int i1 = -1;
         while ((i1 = inputstream.read(xis1)) >= 0) {
            yapsocket.write(xis1, 0, i1);
            if (xbool) i_currentByte += i1;
         }
         bufferedinputstream1.close();
      }
   }
}