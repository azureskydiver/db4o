package com.db4o.filestats;

import java.util.*;

@decaf.Ignore
public class FileUsageStats {
	private Map<String, ClassUsageStats> _classUsageStats = 
		new TreeMap<String, ClassUsageStats>();
	private long _fileSize;
	private final long _fileHeader;
	private long _freespace;
	private final long _idSystem;
	private final long _classMetadata;
	private final long _freespaceUsage;
	
	public FileUsageStats(long fileSize, long fileHeader, long idSystem, long freespace, long classMetadata, long freespaceUsage) {
		_fileSize = fileSize;
		_fileHeader = fileHeader;
		_idSystem = idSystem;
		_freespace = freespace;
		_classMetadata = classMetadata;
		_freespaceUsage = freespaceUsage;
	}
	
	public void addClassStats(ClassUsageStats classStats) {
		_classUsageStats.put(classStats.className(), classStats);
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
	
	public long totalUsage() {
		long total = _fileHeader + _freespace + _idSystem + _classMetadata + _freespaceUsage;
		for (ClassUsageStats classStats : _classUsageStats.values()) {
			total += classStats.totalUsage();
		}
		return total;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(ClassUsageStats classStats : _classUsageStats.values()) {
			str.append(classStats.className()).append("\n");
			str.append(formatLine("Slots", classStats.slotUsage()));
			str.append(formatLine("Class index", classStats.classIndexUsage()));
			str.append(formatLine("Field indices", classStats.fieldIndexUsage()));
			if(classStats.miscUsage() > 0) {
				str.append(formatLine("Misc", classStats.miscUsage()));
			}
			str.append(formatLine("Total", classStats.totalUsage()));
		}
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
		return str.toString();
	}
	
	private String formatLine(String label, long amount) {
		return String.format("%20s: %12d\n", label, amount);
	}

	public ClassUsageStats classStats(String name) {
		return _classUsageStats.get(name);
	}
}