/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <summary>Fakes a socket connection for an embedded client.</summary>
	/// <remarks>Fakes a socket connection for an embedded client.</remarks>
	internal class YapSocketFake : com.db4o.YapSocket
	{
		internal readonly com.db4o.YapServer i_server;

		private com.db4o.YapSocketFake i_affiliate;

		private com.db4o.ByteBuffer4 i_uploadBuffer;

		private com.db4o.ByteBuffer4 i_downloadBuffer;

		public YapSocketFake(com.db4o.YapServer a_server)
		{
			i_server = a_server;
			i_uploadBuffer = new com.db4o.ByteBuffer4(((com.db4o.Config4Impl)a_server.configure
				()).i_timeoutClientSocket);
			i_downloadBuffer = new com.db4o.ByteBuffer4(((com.db4o.Config4Impl)a_server.configure
				()).i_timeoutClientSocket);
		}

		public YapSocketFake(com.db4o.YapServer a_server, com.db4o.YapSocketFake affiliate
			) : this(a_server)
		{
			i_affiliate = affiliate;
			affiliate.i_affiliate = this;
			i_downloadBuffer = affiliate.i_uploadBuffer;
			i_uploadBuffer = affiliate.i_downloadBuffer;
		}

		public override void close()
		{
			if (i_affiliate != null)
			{
				com.db4o.YapSocketFake temp = i_affiliate;
				i_affiliate = null;
				temp.close();
			}
			i_affiliate = null;
		}

		public override void flush()
		{
		}

		public override string getHostName()
		{
			return null;
		}

		public virtual bool isClosed()
		{
			return i_affiliate == null;
		}

		public override int read()
		{
			return i_downloadBuffer.read();
		}

		public override int read(byte[] a_bytes, int a_offset, int a_length)
		{
			return i_downloadBuffer.read(a_bytes, a_offset, a_length);
		}

		public override void setSoTimeout(int a_timeout)
		{
			i_uploadBuffer.setTimeout(a_timeout);
			i_downloadBuffer.setTimeout(a_timeout);
		}

		public override void write(byte[] bytes)
		{
			i_uploadBuffer.write(bytes);
		}

		public override void write(int i)
		{
			i_uploadBuffer.write(i);
		}
	}
}
