package com.db4o.filestats;

public class ClassUsageStats {
	private final String _className;
	private final long _slotUsage;
	private final long _classIndexUsage;
	private final long _fieldIndexUsage;
	private final long _miscUsage;	
	
	public ClassUsageStats(String className, long slotSpace, long classIndexUsage, long fieldIndexUsage, long miscUsage) {
		_className = className;
		_slotUsage = slotSpace;
		_classIndexUsage = classIndexUsage;
		_fieldIndexUsage = fieldIndexUsage;
		_miscUsage = miscUsage;
	}
	
	public String className() {
		return _className;
	}
	
	public long slotUsage() {
		return _slotUsage;
	}

	public long classIndexUsage() {
		return _classIndexUsage;
	}

	public long fieldIndexUsage() {
		return _fieldIndexUsage;
	}

	public long miscUsage() {
		return _miscUsage;
	}

	public long totalUsage() {
		return _slotUsage + _classIndexUsage + _fieldIndexUsage + _miscUsage;
	}
}