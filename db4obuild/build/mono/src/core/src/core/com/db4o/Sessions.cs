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
	internal class Sessions : com.db4o.Collection4
	{
		internal virtual void forEach(com.db4o.Visitor4 visitor)
		{
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.Iterator4 i = iterator();
				while (i.hasNext())
				{
					visitor.visit(i.next());
				}
			}
		}

		internal virtual com.db4o.ObjectContainer open(string databaseFileName)
		{
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.ObjectContainer oc = null;
				com.db4o.Session newSession = new com.db4o.Session(databaseFileName);
				com.db4o.Session.checkHackedVersion();
				com.db4o.Session oldSession = (com.db4o.Session)get(newSession);
				if (oldSession != null)
				{
					oc = oldSession.subSequentOpen();
					if (oc == null)
					{
						remove(oldSession);
					}
					return oc;
				}
				try
				{
					oc = new com.db4o.YapRandomAccessFile(newSession);
				}
				catch (com.db4o.ExpirationException e)
				{
					throw e;
				}
				catch (com.db4o.LongJumpOutException e)
				{
					throw e;
				}
				catch (com.db4o.ext.DatabaseFileLockedException e)
				{
					throw e;
				}
				catch (com.db4o.ext.ObjectNotStorableException e)
				{
					throw e;
				}
				catch (com.db4o.UserException eu)
				{
					com.db4o.Db4o.throwRuntimeException(eu.errCode, eu.errMsg);
				}
				catch (System.Exception t)
				{
					com.db4o.Db4o.logErr(com.db4o.Db4o.i_config, 4, databaseFileName, t);
					return null;
				}
				if (oc != null)
				{
					newSession.i_stream = (com.db4o.YapStream)oc;
					add(newSession);
					com.db4o.Platform.postOpen(oc);
					com.db4o.Db4o.logMsg(com.db4o.Db4o.i_config, 5, databaseFileName);
				}
				return oc;
			}
		}

		internal override object remove(object obj)
		{
			lock (com.db4o.Db4o.Lock)
			{
				return base.remove(obj);
			}
		}
	}
}
