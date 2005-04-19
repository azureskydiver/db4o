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
	internal sealed class MQueryExecute : com.db4o.MsgObject
	{
		internal override bool processMessageAtServer(com.db4o.YapSocket sock)
		{
			com.db4o.Transaction trans = getTransaction();
			com.db4o.YapStream stream = getStream();
			com.db4o.QResult qr = new com.db4o.QResult(trans);
			this.unmarshall();
			com.db4o.QQuery query = (com.db4o.QQuery)stream.unmarshall(payLoad);
			query.unmarshall(getTransaction());
			lock (stream.i_lock)
			{
				try
				{
					query.executeLocal(qr);
				}
				catch (System.Exception e)
				{
					qr = new com.db4o.QResult(getTransaction());
				}
			}
			writeQueryResult(getTransaction(), qr, sock);
			return true;
		}
	}
}
