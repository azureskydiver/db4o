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
	public class DTrace
	{
		public const bool enabled = false;

		private static void breakPoint()
		{
			int placeBreakPointHere = 1;
		}

		private static object init()
		{
			return null;
		}

		private DTrace(bool enabled_, bool break_, string tag_, bool log_)
		{
		}

		private bool _enabled;

		private bool _break;

		private bool _log;

		private string _tag;

		private static long[] rangeStart;

		private static long[] rangeEnd;

		private static int rangeCount;

		public static com.db4o.DTrace BIND;

		public static com.db4o.DTrace CLOSE;

		public static com.db4o.DTrace COMMIT;

		public static com.db4o.DTrace CONTINUESET;

		public static com.db4o.DTrace FREE;

		public static com.db4o.DTrace FREE_ON_COMMIT;

		public static com.db4o.DTrace FREE_ON_ROLLBACK;

		public static com.db4o.DTrace GET_SLOT;

		public static com.db4o.DTrace NEW_INSTANCE;

		public static com.db4o.DTrace READ_ID;

		public static com.db4o.DTrace READ_SLOT;

		public static com.db4o.DTrace REFERENCE_REMOVED;

		public static com.db4o.DTrace REGULAR_SEEK;

		public static com.db4o.DTrace REMOVE_FROM_CLASS_INDEX;

		public static com.db4o.DTrace TRANS_COMMIT;

		public static com.db4o.DTrace TRANS_DONT_DELETE;

		public static com.db4o.DTrace TRANS_DELETE;

		public static com.db4o.DTrace YAPCLASS_BY_ID;

		public static com.db4o.DTrace WRITE_BYTES;

		public static com.db4o.DTrace WRITE_UPDATE_DELETE_MEMBERS;

		private static readonly object forInit = init();

		private static com.db4o.DTrace all;

		private static int current;

		public virtual void log()
		{
		}

		public virtual void log(long p)
		{
		}

		public virtual void logInfo(string info)
		{
		}

		public virtual void log(long p, string info)
		{
		}

		public virtual void logLength(long start, long length)
		{
		}

		public virtual void logEnd(long start, long end)
		{
		}

		public virtual void logEnd(long start, long end, string info)
		{
		}

		public static void addRange(long pos)
		{
		}

		public static void addRangeWithLength(long start, long length)
		{
		}

		public static void addRangeWithEnd(long start, long end)
		{
		}

		private string formatInt(long i)
		{
			return null;
		}

		private static void turnAllOffExceptFor(com.db4o.DTrace[] these)
		{
		}
	}
}
