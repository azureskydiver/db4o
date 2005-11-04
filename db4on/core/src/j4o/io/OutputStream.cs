/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

	public class OutputStream : StreamAdaptor {

		public OutputStream(Stream stream) : base(stream) {
		}

		public void write(int b) {
			_stream.WriteByte((byte) b);
		}

		public void write(byte[] bytes) {
			_stream.Write(bytes, 0, bytes.Length);
		}

        public void write(byte[] bytes, int offset, int length) {
            _stream.Write(bytes, offset, length);
        }

		public void flush() {
			_stream.Flush();
		}
	}
}
