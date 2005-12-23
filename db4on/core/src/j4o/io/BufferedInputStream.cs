/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using com.db4o;

namespace j4o.io {

	public class BufferedInputStream : InputStream {

		public BufferedInputStream(InputStream stream) : base(Compat.buffer(stream.UnderlyingStream)) {
		}

		public BufferedInputStream(InputStream stream, int bufferSize) : base(Compat.buffer(stream.UnderlyingStream, bufferSize)) {
		}

	}

}
