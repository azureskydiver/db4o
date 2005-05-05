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
	internal abstract class Debug
	{
		public const bool checkSychronization = false;

		public const bool atHome = false;

		public const bool indexAllFields = false;

		public const bool configureAllClasses = indexAllFields;

		public const bool configureAllFields = indexAllFields;

		public const bool toStrings = false;

		public const bool weakReferences = true;

		public const bool arrayTypes = true;

		public const bool verbose = false;

		public const bool fakeServer = false;

		internal const bool messages = false;

		public const bool nio = true;

		internal const bool lockFile = true;

		internal const bool longTimeOuts = false;

		internal static com.db4o.YapFile serverStream;

		internal static com.db4o.YapClient clientStream;

		internal static com.db4o.Queue4 clientMessageQueue;

		internal static com.db4o.Lock4 clientMessageQueueLock;

		public static void ensureLock(object obj)
		{
		}

		public static bool exceedsMaximumBlockSize(int a_length)
		{
			if (a_length > com.db4o.YapConst.MAXIMUM_BLOCK_SIZE)
			{
				return true;
			}
			return false;
		}

		public static bool exceedsMaximumArrayEntries(int a_entries, bool a_primitive)
		{
			if (a_entries > (a_primitive ? com.db4o.YapConst.MAXIMUM_ARRAY_ENTRIES_PRIMITIVE : 
				com.db4o.YapConst.MAXIMUM_ARRAY_ENTRIES))
			{
				return true;
			}
			return false;
		}
	}
}
