/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io {

	public abstract class StreamAdaptor {

		protected Stream _stream;

		public StreamAdaptor(Stream stream) {
			_stream = stream;
		}

		internal Stream UnderlyingStream {
			get {
				return _stream;
			}
		}

		public void close() {
			_stream.Close();
		}
	}
}
