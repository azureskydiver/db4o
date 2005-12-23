/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

	public class InputStream : StreamAdaptor {

		public InputStream(Stream stream) : base(stream) {
		}

		public int available() {
			return (int)(_stream.Length - _stream.Position);
		}

		public int read() {
			return _stream.ReadByte();
		}

        public int read(byte[] bytes){
            int read = _stream.Read(bytes, 0, bytes.Length);
            return (0 == read) ? -1 : read;
        }

		public int read(byte[] bytes, int offset, int length) {
            int read = _stream.Read(bytes, offset, length);
            return (0 == read) ? -1 : read;
		}
	}

}
