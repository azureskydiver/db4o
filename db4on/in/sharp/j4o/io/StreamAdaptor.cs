/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace j4o.io 
{

	public abstract class StreamAdaptor 
	{

		protected Stream _stream;

		public StreamAdaptor(Stream stream) 
		{
			_stream = stream;
		}

		internal Stream UnderlyingStream 
		{
			get 
			{
				return _stream;
			}
		}

		public void close() 
		{
			_stream.Close();
		}

#if CF_1_0 || CF_2_0
		internal Stream buffered()
		{
			return _stream;
		}

		internal Stream buffered(int bufferSize)
		{
			return _stream;
		}
#else
		internal Stream buffered()
		{
			return new BufferedStream(_stream);
		}

		internal Stream buffered(int bufferSize)
		{
			return new BufferedStream(_stream, bufferSize);
		}
#endif
	}
}
