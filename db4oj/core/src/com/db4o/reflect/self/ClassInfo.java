package com.db4o.reflect.self;

public class ClassInfo {
	private Class _superClass;
	private boolean _isAbstract;
	private FieldInfo[] _fieldInfo;
	
	public ClassInfo(boolean isAbstract,Class superClass,FieldInfo[] fieldInfo) {
		_isAbstract = isAbstract;
		_superClass = superClass;
		_fieldInfo=fieldInfo;
	}

	public boolean isAbstract() {
		return _isAbstract;
	}
	
	public Class superClass() {
		return _superClass;
	}
	
	public FieldInfo[] fieldInfo() {
		return _fieldInfo;
	}
}
