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
	public interface YapDataType : com.db4o.YapComparable
	{
		void appendEmbedded3(com.db4o.YapWriter a_bytes);

		bool canHold(com.db4o.reflect.ReflectClass claxx);

		void cascadeActivation(com.db4o.Transaction a_trans, object a_object, int a_depth
			, bool a_activate);

		com.db4o.reflect.ReflectClass classReflector();

		void copyValue(object a_from, object a_to);

		void deleteEmbedded(com.db4o.YapWriter a_bytes);

		int getID();

		bool equals(com.db4o.YapDataType a_dataType);

		object indexEntry(object a_object);

		object indexObject(com.db4o.Transaction a_trans, object a_object);

		int linkLength();

		void prepareLastIoComparison(com.db4o.Transaction a_trans, object obj);

		com.db4o.reflect.ReflectClass primitiveClassReflector();

		object read(com.db4o.YapWriter writer);

		object readIndexObject(com.db4o.YapWriter writer);

		object readQuery(com.db4o.Transaction trans, com.db4o.YapReader reader, bool toArray
			);

		bool supportsIndex();

		void writeNew(object a_object, com.db4o.YapWriter a_bytes);

		int getType();

		com.db4o.YapClass getYapClass(com.db4o.YapStream a_stream);

		/// <summary>performance optimized read (only used for byte[] so far)</summary>
		bool readArray(object array, com.db4o.YapWriter reader);

		void readCandidates(com.db4o.YapReader a_bytes, com.db4o.QCandidates a_candidates
			);

		object readIndexEntry(com.db4o.YapReader a_reader);

		com.db4o.YapDataType readArrayWrapper(com.db4o.Transaction a_trans, com.db4o.YapReader[]
			 a_bytes);

		/// <summary>performance optimized write (only used for byte[] so far)</summary>
		bool writeArray(object array, com.db4o.YapWriter reader);

		void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object);
	}
}
