/* Copyright (C) 2010   Versant Inc.   http://www.db4o.com */

package com.db4o.filestats;

import static com.db4o.filestats.FileUsageStatsUtil.*;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

@decaf.Ignore
public class FileUsageStats {
	private TreeStringObject<ClassUsageStats> _classUsageStats = null;
	private long _fileSize;
	private final long _fileHeader;
	private final long _freespace;
	private final long _idSystem;
	private final long _classMetadata;
	private final long _freespaceUsage;
	private final SlotMap _slots;
	
	public FileUsageStats(long fileSize, long fileHeader, long idSystem, long freespace, long classMetadata, long freespaceUsage, SlotMap slots) {
		_fileSize = fileSize;
		_fileHeader = fileHeader;
		_idSystem = idSystem;
		_freespace = freespace;
		_classMetadata = classMetadata;
		_freespaceUsage = freespaceUsage;
		_slots = slots;
	}
	
	public void addClassStats(ClassUsageStats classStats) {
		_classUsageStats = Tree.add(_classUsageStats, new TreeStringObject<ClassUsageStats>(classStats.className(), classStats));
	}
	
	public long fileHeader() {
		return _fileHeader;
	}

	public long freespace() {
		return _freespace;
	}
	
	public long idSystem() {
		return _idSystem;
	}
	
	public long classMetadata() {
		return _classMetadata;
	}
	
	public long freespaceUsage() {
		return _freespaceUsage;
	}
	
	public long fileSize() {
		return _fileSize;
	}
	
	public void addSlot(Slot slot) {
		_slots.add(slot);
	}
	
	public long totalUsage() {
		final LongByRef total = new LongByRef(_fileHeader + _freespace + _idSystem + _classMetadata + _freespaceUsage);
		Tree.traverse(_classUsageStats, new Visitor4<TreeStringObject<ClassUsageStats>>() {
			public void visit(TreeStringObject<ClassUsageStats> node) {
				total.value += node._value.totalUsage();
			}
		});
		return total.value;
	}
	
	@Override
	public String toString() {
		final StringBuffer str = new StringBuffer();
		Tree.traverse(_classUsageStats, new Visitor4<TreeStringObject<ClassUsageStats>>() {
			public void visit(TreeStringObject<ClassUsageStats> node) {
				ClassUsageStats classStats = node._value;
				str.append(classStats.className()).append("\n");
				str.append(formatLine("Slots", classStats.slotUsage()));
				str.append(formatLine("Class index", classStats.classIndexUsage()));
				str.append(formatLine("Field indices", classStats.fieldIndexUsage()));
				if(classStats.miscUsage() > 0) {
					str.append(formatLine("Misc", classStats.miscUsage()));
				}
				str.append(formatLine("Total", classStats.totalUsage()));
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
	
	public ClassUsageStats classStats(String name) {
		TreeStringObject<ClassUsageStats> found = (TreeStringObject<ClassUsageStats>) Tree.find(_classUsageStats, new TreeStringObject<ClassUsageStats>(name, null));
		return found == null ? null : found._value;
	}
}