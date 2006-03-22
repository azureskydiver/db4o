package com.db4o.config;

public class TypeAlias implements Alias {

	private String _storedType;
	private String _runtimeType;

	public TypeAlias(String storedType, String runtimeType) {
		if (null == storedType) throw new IllegalArgumentException("storedType");
		if (null == runtimeType) throw new IllegalArgumentException("runtimeType");
		_storedType = storedType;
		_runtimeType = runtimeType;
	}

	public String resolve(String runtimeType) {
		return _runtimeType.equals(runtimeType)
			? _storedType
			: null;
	}

}
