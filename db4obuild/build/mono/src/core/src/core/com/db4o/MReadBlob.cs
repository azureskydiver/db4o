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
	internal class MReadBlob : com.db4o.MsgBlob
	{
		internal override void processClient(com.db4o.YapSocket sock)
		{
			com.db4o.Msg message = com.db4o.Msg.readMessage(getTransaction(), sock);
			if (message.Equals(com.db4o.Msg.LENGTH))
			{
				try
				{
					i_currentByte = 0;
					i_length = message.getPayLoad().readInt();
					i_blob.getStatusFrom(this);
					i_blob.setStatus(com.db4o.ext.Status.PROCESSING);
					copy(sock, this.i_blob.getClientOutputStream(), i_length, true);
					message = com.db4o.Msg.readMessage(getTransaction(), sock);
					if (message.Equals(com.db4o.Msg.OK))
					{
						this.i_blob.setStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this.i_blob.setStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch (System.Exception e)
				{
				}
			}
			else
			{
				if (message.Equals(com.db4o.Msg.ERROR))
				{
					this.i_blob.setStatus(com.db4o.ext.Status.ERROR);
				}
			}
		}

		internal override bool processMessageAtServer(com.db4o.YapSocket sock)
		{
			com.db4o.YapStream stream = getStream();
			try
			{
				com.db4o.BlobImpl blobImpl = this.serverGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.setTrans(getTransaction());
					j4o.io.File file = blobImpl.serverFile(null, false);
					int length = (int)file.length();
					com.db4o.Msg.LENGTH.getWriterForInt(getTransaction(), length).write(stream, sock);
					j4o.io.FileInputStream fin = new j4o.io.FileInputStream(file);
					copy(fin, sock, false);
					sock.flush();
					com.db4o.Msg.OK.write(stream, sock);
				}
			}
			catch (System.Exception e)
			{
				com.db4o.Msg.ERROR.write(stream, sock);
			}
			return true;
		}
	}
}
