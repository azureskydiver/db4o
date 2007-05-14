package com.db4o.test.nativequery.analysis;

class Base {
	int id;
	Integer idWrap;

	public int getId() {
		return id;
	}

	public Integer getIdWrapped() {
		return idWrap;
	}

	public int getIdPlusOne() {
		return id+1;
	}
}

