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
using com.db4o.types;
namespace com.db4o {

   internal class BlobImpl : Blob, Cloneable, Db4oTypeImpl {
      
      internal BlobImpl() : base() {
      }
      static internal int COPYBUFFER_LENGTH = 4096;
      public String fileName;
      public String i_ext;
      [Transient] private File i_file;
      [Transient] private MsgBlob i_getStatusFrom;
      public int i_length;
      [Transient] private double i_status = -1.0;
      [Transient] private YapStream i_stream;
      [Transient] private Transaction i_trans;
      
      public int adjustReadDepth(int i) {
         return 1;
      }
      
      private String checkExt(File file) {
         String xstring1 = file.getName();
         int i1 = xstring1.LastIndexOf(".");
         if (i1 > 0) {
            i_ext = xstring1.Substring(i1);
            return xstring1.Substring(0, i1);
         }
         i_ext = "";
         return xstring1;
      }
      
      private void copy(File file, File file_0_) {
         file_0_.delete();
         BufferedInputStream bufferedinputstream1 = new BufferedInputStream(new FileInputStream(file));
         BufferedOutputStream bufferedoutputstream1 = new BufferedOutputStream(new FileOutputStream(file_0_));
         byte[] xis1 = new byte[4096];
         int i1 = -1;
         while ((i1 = bufferedinputstream1.read(xis1)) >= 0) bufferedoutputstream1.write(xis1, 0, i1);
         bufferedoutputstream1.flush();
         bufferedoutputstream1.close();
         bufferedinputstream1.close();
      }
      
      public Object createDefault(Transaction transaction) {
         Object obj1 = null;
         BlobImpl blobimpl_1_1;
         try {
            {
               blobimpl_1_1 = (BlobImpl)j4o.lang.JavaSystem.clone(this);
               blobimpl_1_1.setTrans(transaction);
            }
         }  catch (CloneNotSupportedException clonenotsupportedexception) {
            {
               return null;
            }
         }
         return blobimpl_1_1;
      }
      
      internal FileInputStream getClientInputStream() {
         return new FileInputStream(i_file);
      }
      
      internal FileOutputStream getClientOutputStream() {
         return new FileOutputStream(i_file);
      }
      
      public String getFileName() {
         return fileName;
      }
      
      internal int getLength() {
         return i_length;
      }
      
      public double getStatus() {
         if (i_status == -5.0 && i_getStatusFrom != null) return i_getStatusFrom.getStatus();
         if (i_status == -1.0 && i_length > 0) i_status = -2.0;
         return i_status;
      }
      
      internal void getStatusFrom(MsgBlob msgblob) {
         i_getStatusFrom = msgblob;
      }
      
      public bool hasClassIndex() {
         return false;
      }
      
      public void readFrom(File file) {
         if (!file.exists()) throw new IOException(Messages.get(41, file.getAbsolutePath()));
         i_length = (int)j4o.lang.JavaSystem.getLengthOf(file);
         checkExt(file);
         if (i_stream.isClient()) {
            i_file = file;
            Object obj1 = null;
            MsgBlob msgblob1;
            lock (i_stream.Lock()) {
               i_stream.set(this);
               int i1 = (int)i_stream.getID(this);
               msgblob1 = (MsgBlob)Msg.WRITE_BLOB.getWriterForInt(i_trans, i1);
               msgblob1.i_blob = this;
               i_status = -3.0;
            }
            ((YapClient)i_stream).processBlobMessage(msgblob1);
         } else readLocal(file);
      }
      
      public void readLocal(File file) {
         bool xbool1 = false;
         if (fileName == null) {
            File file_2_1 = new File(serverPath(), file.getName());
            if (!file_2_1.exists()) {
               copy(file, file_2_1);
               xbool1 = true;
               fileName = file_2_1.getName();
            }
         }
         if (!xbool1) copy(file, serverFile(checkExt(file), true));
         lock (i_stream.i_lock) {
            i_stream.setInternal(i_trans, this, false);
         }
         i_status = -4.0;
      }
      
      public void preDeactivate() {
      }
      
      internal File serverFile(String xstring, bool xbool) {
         lock (i_stream.i_lock) {
            i_stream.activate1(i_trans, this, 2);
         }
         String string_3_1 = serverPath();
         i_stream.i_config.ensureDirExists(string_3_1);
         do {
            if (xbool) {
               if (fileName != null) break;
               if (xstring != null) fileName = xstring; else fileName = "b_" + j4o.lang.JavaSystem.currentTimeMillis();
               String string_4_1 = fileName + i_ext;
               int i1 = 0;
               while (new File(string_3_1, string_4_1).exists()) {
                  string_4_1 = fileName + "_" + i1++ + i_ext;
                  if (i1 == 99) {
                     i_status = -99.0;
                     throw new IOException(Messages.get(40));
                  }
               }
               fileName = string_4_1;
               lock (i_stream.i_lock) {
                  i_stream.setInternal(i_trans, this, false);
                  break;
               }
            }
            if (fileName == null) throw new IOException(Messages.get(38));
         }          while (false);
         String string_5_1 = string_3_1 + File.separator + fileName;
         if (!xbool && !new File(string_5_1).exists()) throw new IOException(Messages.get(39));
         return new File(string_5_1);
      }
      
      private String serverPath() {
         String xstring1 = i_stream.i_config.i_blobPath;
         if (xstring1 == null) xstring1 = "blobs";
         i_stream.i_config.ensureDirExists(xstring1);
         return xstring1;
      }
      
      internal void setStatus(double d) {
         i_status = d;
      }
      
      public void setTrans(Transaction transaction) {
         i_trans = transaction;
         i_stream = transaction.i_stream;
      }
      
      public void writeLocal(File file) {
         copy(serverFile(null, false), file);
         i_status = -4.0;
      }
      
      public void writeTo(File file) {
         if (getStatus() == -1.0) throw new IOException(Messages.get(43));
         if (i_stream.isClient()) {
            i_file = file;
            MsgBlob msgblob1 = (MsgBlob)Msg.READ_BLOB.getWriterForInt(i_trans, (int)i_stream.getID(this));
            msgblob1.i_blob = this;
            i_status = -3.0;
            ((YapClient)i_stream).processBlobMessage(msgblob1);
         } else writeLocal(file);
      }
      
      public Object storedTo(Transaction transaction) {
         return this;
      }
      
      public void setYapObject(YapObject yapobject) {
      }
   }
}