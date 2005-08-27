
/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
        
        public RandomAccessFile getFD() {
        	return this;
        }

        public void write(byte[] bytes) {
            this.write(bytes, 0, bytes.Length);
        }

        public void write(byte[] bytes, int offset, int length) {
            fileStream.Write(bytes, offset, length);
        }
    }
}
