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
	/// <exclude></exclude>
	public class Unobfuscated
	{
		internal static object random;

		internal static bool createDb4oList(object a_stream)
		{
			((com.db4o.YapStream)a_stream).checkClosed();
			return !((com.db4o.YapStream)a_stream).isInstantiating();
		}

		public static byte[] generateSignature()
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(null, 300);
			com.db4o.YLong.writeLong(j4o.lang.JavaSystem.currentTimeMillis(), writer);
			com.db4o.YLong.writeLong(randomLong(), writer);
			com.db4o.YLong.writeLong(randomLong() + 1, writer);
			return writer.getWrittenBytes();
		}

		internal static void logErr(com.db4o.config.Configuration config, int code, string
			 msg, System.Exception t)
		{
			com.db4o.Db4o.logErr(config, code, msg, t);
		}

		internal static void purgeUnsychronized(object a_stream, object a_object)
		{
			((com.db4o.YapStream)a_stream).purge1(a_object);
		}

		public static long randomLong()
		{
			return j4o.lang.JavaSystem.currentTimeMillis();
		}

		internal static void shutDownHookCallback(object a_stream)
		{
			((com.db4o.YapStream)a_stream).failedToShutDown();
		}
	}
}
