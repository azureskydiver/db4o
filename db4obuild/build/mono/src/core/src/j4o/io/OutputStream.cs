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

namespace j4o.io {

	public class OutputStream : StreamAdaptor {

		public OutputStream(Stream stream) : base(stream) {
		}

		public void write(int b) {
			_stream.WriteByte((byte) b);
		}

        public void write(byte[] bytes, int offset, int length){
            _stream.Write(bytes, offset, length);
        }

		public void flush() {
			_stream.Flush();
		}
	}
}
