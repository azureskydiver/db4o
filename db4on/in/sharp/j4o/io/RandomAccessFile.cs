/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

    public class RandomAccessFile {

        private FileStream fileStream;

#if NET || NET_2_0
        [System.Runtime.InteropServices.DllImport("kernel32.dll", SetLastError=true)] 
        static extern int FlushFileBuffers(IntPtr fileHandle); 
#endif

        public RandomAccessFile(String file, String fileMode) {
            fileStream = new FileStream(file, FileMode.OpenOrCreate,
                fileMode.Equals("rw") ? FileAccess.ReadWrite : FileAccess.Read);
            lockFileStream(this.fileStream);
        }

    	private void lockFileStream(FileStream stream)
    	{
#if !CF_1_0 && !CF_2_0
			stream.Lock(0, 1);
#endif
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

#if NET || NET_2_0
			FlushFileBuffers(fileStream.Handle);
#endif

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
