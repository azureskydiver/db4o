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
using System.IO;
using com.db4o;

namespace j4o.io {

    public class RandomAccessFile {

        private FileStream fileStream;

        public RandomAccessFile(String file, String fileMode) {
            fileStream = new FileStream(file, FileMode.OpenOrCreate,
                fileMode.Equals("rw") ? FileAccess.ReadWrite : FileAccess.Read);
            Compat.lockFileStream(this.fileStream);
        }

        public void close() {
            fileStream.Close();
        }

        public long length() {
            return fileStream.Length;
        }

        public int read(byte[] bytes, int offset, int length) {
            return fileStream.Read(bytes, offset, length);
        }

        public void read(byte[] bytes) {
            fileStream.Read(bytes, 0, bytes.Length);
        }

        public void seek(long pos) {
            fileStream.Seek(pos, SeekOrigin.Begin);
        }

        public void sync() {
            fileStream.Flush();
        }

        public void write(byte[] bytes) {
            this.write(bytes, 0, bytes.Length);
        }

        public void write(byte[] bytes, int offset, int length) {
            fileStream.Write(bytes, offset, length);
        }
    }
}
