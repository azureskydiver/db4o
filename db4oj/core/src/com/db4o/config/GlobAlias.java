package com.db4o.config;

public class GlobAlias implements Alias {
	
	private GlobPattern _storedPattern;
	private GlobPattern _runtimePattern;

	public GlobAlias(String storedPattern, String runtimePattern) {
		if (null == storedPattern) throw new IllegalArgumentException("storedPattern");
		if (null == runtimePattern) throw new IllegalArgumentException("runtimePattern");
		
		_storedPattern = new GlobPattern(storedPattern);
		_runtimePattern = new GlobPattern(runtimePattern);
	}

	public String resolve(String runtimeType) {
		String match = _runtimePattern.matches(runtimeType);
		return match != null
			? _storedPattern.inject(match)
			: null;
	}
	
	static class GlobPattern {
		private String _head;
		private String _tail;

		public GlobPattern(String pattern) {
			String[] parts = split(pattern);
			
			_head = parts[0];
			_tail = parts[1];
		}

		public String inject(String s) {
			return _head + s + _tail; 
		}

		public String matches(String s) {
			if (!s.startsWith(_head) || !s.endsWith(_tail)) return null;
			return s.substring(_head.length(), s.length()-_tail.length());
		}

		private void invalidPattern() {
			throw new  IllegalArgumentException("glob pattern must contain one and only one '*' character");
		}
		
		String[] split(String pattern) {
			int index = pattern.indexOf('*');
			if (-1 == index || index != pattern.lastIndexOf('*')) invalidPattern();
			return new String[] {
					pattern.substring(0, index),
					pattern.substring(index+1)
			};
		}
	}

}
