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
	internal sealed class Message
	{
		internal readonly j4o.io.PrintStream stream;

		internal Message(com.db4o.YapStream a_stream, string msg)
		{
			stream = a_stream.i_config.outStream();
			print(msg, true);
		}

		internal Message(string a_StringParam, int a_intParam, j4o.io.PrintStream a_stream
			, bool header)
		{
			stream = a_stream;
			print(com.db4o.Messages.get(a_intParam, a_StringParam), header);
		}

		internal Message(string a_StringParam, int a_intParam, j4o.io.PrintStream a_stream
			) : this(a_StringParam, a_intParam, a_stream, true)
		{
		}

		private void print(string msg, bool header)
		{
			if (stream != null)
			{
				if (header)
				{
					stream.println("[" + com.db4o.Db4o.version() + "   " + com.db4o.YDate.now() + "] "
						);
				}
				stream.println(" " + msg);
			}
		}
	}
}
