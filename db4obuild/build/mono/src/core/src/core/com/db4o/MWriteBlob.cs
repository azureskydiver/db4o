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
	internal class MWriteBlob : com.db4o.MsgBlob
	{
		internal override void processClient(com.db4o.YapSocket sock)
		{
			com.db4o.Msg message = com.db4o.Msg.readMessage(getTransaction(), sock);
			if (message.Equals(com.db4o.Msg.OK))
			{
				try
				{
					i_currentByte = 0;
					i_length = this.i_blob.getLength();
					i_blob.getStatusFrom(this);
					i_blob.setStatus(com.db4o.ext.Status.PROCESSING);
					j4o.io.FileInputStream inBlob = this.i_blob.getClientInputStream();
					copy(inBlob, sock, true);
					sock.flush();
					com.db4o.YapStream stream = getStream();
					message = com.db4o.Msg.readMessage(getTransaction(), sock);
					if (message.Equals(com.db4o.Msg.OK))
					{
						stream.deactivate(i_blob, int.MaxValue);
						stream.activate(i_blob, int.MaxValue);
						this.i_blob.setStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this.i_blob.setStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch (System.Exception e)
				{
					j4o.lang.JavaSystem.printStackTrace(e);
				}
			}
		}

		internal override bool processMessageAtServer(com.db4o.YapSocket sock)
		{
			try
			{
				com.db4o.YapStream stream = getStream();
				com.db4o.BlobImpl blobImpl = this.serverGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.setTrans(getTransaction());
					j4o.io.File file = blobImpl.serverFile(null, true);
					com.db4o.Msg.OK.write(stream, sock);
					j4o.io.FileOutputStream fout = new j4o.io.FileOutputStream(file);
					copy(sock, fout, blobImpl.getLength(), false);
					com.db4o.Msg.OK.write(stream, sock);
				}
			}
			catch (System.Exception e)
			{
			}
			return true;
		}
	}
}
