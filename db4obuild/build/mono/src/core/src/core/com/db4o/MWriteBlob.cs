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

   internal class MWriteBlob : MsgBlob {
      
      internal MWriteBlob() : base() {
      }
      
      internal override void processClient(YapSocket yapsocket) {
         Msg msg1 = Msg.readMessage(this.getTransaction(), yapsocket);
         if (msg1.Equals(Msg.OK)) {
            try {
               {
                  i_currentByte = 0;
                  i_length = i_blob.getLength();
                  i_blob.getStatusFrom(this);
                  i_blob.setStatus(-5.0);
                  j4o.io.FileInputStream fileinputstream1 = i_blob.getClientInputStream();
                  this.copy(fileinputstream1, yapsocket, true);
                  yapsocket.flush();
                  YapStream yapstream1 = this.getStream();
                  msg1 = Msg.readMessage(this.getTransaction(), yapsocket);
                  if (msg1.Equals(Msg.OK)) {
                     yapstream1.deactivate(i_blob, 2147483647);
                     yapstream1.activate(i_blob, 2147483647);
                     i_blob.setStatus(-4.0);
                  } else i_blob.setStatus(-99.0);
               }
            }  catch (Exception exception) {
               {
                  j4o.lang.JavaSystem.printStackTrace(exception);
               }
            }
         }
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         try {
            {
               YapStream yapstream1 = this.getStream();
               BlobImpl blobimpl1 = this.serverGetBlobImpl();
               if (blobimpl1 != null) {
                  blobimpl1.setTrans(this.getTransaction());
                  File file1 = blobimpl1.serverFile(null, true);
                  Msg.OK.write(yapstream1, yapsocket);
                  FileOutputStream fileoutputstream1 = new FileOutputStream(file1);
                  this.copy(yapsocket, fileoutputstream1, blobimpl1.getLength(), false);
                  Msg.OK.write(yapstream1, yapsocket);
               }
            }
         }  catch (Exception exception) {
            {
            }
         }
         return true;
      }
   }
}