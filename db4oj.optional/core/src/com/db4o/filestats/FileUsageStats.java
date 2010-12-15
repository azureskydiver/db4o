/* Copyright (C) 2010   Versant Inc.   http://www.db4o.com */

package com.db4o.filestats;

import static com.db4o.filestats.FileUsageStatsUtil.*;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * Byte usage statistics for a db4o database file
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class FileUsageStats {
	private TreeStringObject<ClassUsageStats> _classUsageStats = null;
	private long _fileSize;
	private final long _fileHeader;
	private final long _freespace;
	private final long _idSystem;
	private final long _classMetadata;
	private final long _freespaceUsage;
	private final SlotMap _slots;
	
	FileUsageStats(long fileSize, long fileHeader, long idSystem, long freespace, long classMetadata, long freespaceUsage, SlotMap slots) {
		_fileSize = fileSize;
		_fileHeader = fileHeader;
		_idSystem = idSystem;
		_freespace = freespace;
		_classMetadata = classMetadata;
		_freespaceUsage = freespaceUsage;
		_slots = slots;
	}
	
	/**
	 * @return bytes used by the db4o file header (static and variable parts)
	 */
	public long fileHeader() {
		return _fileHeader;
	}

	/**
	 * @return total number of bytes registered as freespace, available for reuse
	 */
	public long freespace() {
		return _freespace;
	}

	/**
	 * @return bytes used by the id system indices
	 */
	public long idSystem() {
		return _idSystem;
	}

	/**
	 * @return number of bytes used for class metadata (class metadata repository and schema definitions)
	 */
	public long classMetadata() {
		return _classMetadata;
	}

	/**
	 * @return number of bytes used for the bookkeeping of the freespace system itself
	 */
	public long freespaceUsage() {
		return _freespaceUsage;
	}
	
	/**
	 * @return total file size in bytes
	 */
	public long fileSize() {
		return _fileSize;
	}
	
	/**
	 * @return number of bytes used aggregated from all categories - should always be equal to {@link #fileSize()}
	 */
	public long totalUsage() {
		final LongByRef total = new LongByRef(_fileHeader + _freespace + _idSystem + _classMetadata + _freespaceUsage);
		Tree.traverse(_classUsageStats, new Visitor4<TreeStringObject<ClassUsageStats>>() {
			public void visit(TreeStringObject<ClassUsageStats> node) {
				total.value += node._value.totalUsage();
			}
		});
		return total.value;
	}

	/**
	 * @return the statistics for each persisted class
	 */
	public Iterator<ClassUsageStats> classUsageStats() {
		return Iterators.platformIterator(new TreeNodeIterator(_classUsageStats));
	}

	/**
	 * @param name a fully qualified class name
	 * @return the statistics for the class with the given name
	 */
	public ClassUsageStats classStats(String name) {
		TreeStringObject<ClassUsageStats> found = (TreeStringObject<ClassUsageStats>) Tree.find(_classUsageStats, new TreeStringObject<ClassUsageStats>(name, null));
		return found == null ? null : found._value;
	}
	
	@Override
	public String toString() {
		final StringBuffer str = new StringBuffer();
		Tree.traverse(_classUsageStats, new Visitor4<TreeStringObject<ClassUsageStats>>() {
			public void visit(TreeStringObject<ClassUsageStats> node) {
				node._value.toString(str);
			}
		});
		str.append("\n");
		str.append(formatLine("File header", fileHeader()));
		str.append(formatLine("Freespace", freespace()));
		str.append(formatLine("ID system", idSystem()));
		str.append(formatLine("Class metadata", classMetadata()));
		str.append(formatLine("Freespace usage", freespaceUsage()));
		str.append("\n");
		long totalUsage = totalUsage();
		str.append(formatLine("Total", totalUsage));
		str.append(formatLine("Unaccounted", fileSize() - totalUsage));
		str.append(formatLine("File", fileSize()));
		str.append(_slots);
		return str.toString();
	}
	
	void addClassStats(ClassUsageStats classStats) {
		_classUsageStats = Tree.add(_classUsageStats, new TreeStringObject<ClassUsageStats>(classStats.className(), classStats));
	}
	
	void addSlot(Slot slot) {
		_slots.add(slot);
	}	
}