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
	internal sealed class Session
	{
		internal readonly string i_fileName;

		internal com.db4o.YapStream i_stream;

		private int i_openCount;

		internal Session(string a_fileName)
		{
			i_fileName = a_fileName;
		}

		internal static void checkHackedVersion()
		{
		}

		/// <summary>returns true, if session is to be closed completely</summary>
		internal bool closeInstance()
		{
			i_openCount--;
			return i_openCount < 0;
		}

		public override bool Equals(object a_object)
		{
			return i_fileName.Equals(((com.db4o.Session)a_object).i_fileName);
		}

		internal string fileName()
		{
			return i_fileName;
		}

		internal com.db4o.YapStream subSequentOpen()
		{
			if (i_stream.isClosed())
			{
				return null;
			}
			i_openCount++;
			return i_stream;
		}
	}
}
